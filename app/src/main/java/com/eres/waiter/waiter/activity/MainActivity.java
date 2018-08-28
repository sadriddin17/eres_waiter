package com.eres.waiter.waiter.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.eres.waiter.waiter.R;
import com.eres.waiter.waiter.app.App;
import com.eres.waiter.waiter.fragment.FirstFragment;
import com.eres.waiter.waiter.interfaces.MyTestInterface;
import com.eres.waiter.waiter.model.IAmTables;
import com.eres.waiter.waiter.model.Table;
import com.eres.waiter.waiter.model.enums.NotificationTypees;
import com.eres.waiter.waiter.model.events.EventIAmTableChange;
import com.eres.waiter.waiter.model.events.EventOnBack;
import com.eres.waiter.waiter.model.singelton.DataSingelton;
import com.eres.waiter.waiter.model.test.IEventTable;
import com.eres.waiter.waiter.server.NotificationData;
import com.eres.waiter.waiter.viewpager.helper.ObservableCollection;
import com.eres.waiter.waiter.viewpager.model.Hall;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements MyTestInterface {
    private static final String TAG = "TAG_R";
    private int k = 0;
    private Disposable disposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadLocalServer();
        FirstFragment firstFragment = new FirstFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, firstFragment);

        transaction.addToBackStack("first");
        transaction.commit();
    }

    @Override
    public void onBackPressed() {

        int a = getSupportFragmentManager().getBackStackEntryCount();

        if (a == 1) {
            k++;
            if (k == 2) {
                EventBus.getDefault().post(new EventOnBack(true));
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                finish();
            } else {

                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                new CountDownTimer(30000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        k = 0;
                    }
                }.start();
            }
        } else super.onBackPressed();
        Log.d(TAG, "onBackPressed: " + a);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null)
            disposable.dispose();
    }

    public void loadLocalServer() {
        Log.d("TAG_R", "loadLocalServer: " + App.getMessage().size());
        ObservableCollection<NotificationData> noteAll = App.getMessage();
        com.eres.waiter.waiter.viewpager.service.WebServer.messages.setCollectionChangeListener(
                new ObservableCollection.CollectionChangeListener() {
                    @Override
                    public void onCollectionChange(ObservableCollection.NotifyCollectionChangedAction action, Object obj, long position) {
                        Log.d("TAG_R", "onCollectionChange: ");
                        if (action == ObservableCollection.NotifyCollectionChangedAction.Add) {
                            int size = noteAll.size();
                            for (NotificationData note : noteAll) {


                                int a = note.getNotificationTypeId();
                                if (a == NotificationTypees.MenuChanged.ordinal()) {
                                    DataSingelton.singelton.loadData();
                                    noteAll.remove(note);
                                }
                                if (a == NotificationTypees.OrderAcceptedInKitchen.ordinal() || NotificationTypees.OrderProblemsInKithen.ordinal() == a || NotificationTypees.CompleteKitchen.ordinal() == a) {

                                    Log.d(TAG, "onCollectionChangeTable: " + note.getTableId());
                                    checkEventIAmTable(note.getTableId());
                                    // Qaysi afitsiant notifay qsa ham event ketaveradi !!!!
                                }

                                if (a == 15) {
                                    first:
                                    for (Hall hall : DataSingelton.singelton.getHalls()) {
                                        for (Table table : hall.getTables()) {
                                            if (table.getId() == note.getTableId()) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        hall.getTables().remove(table);
                                                        noteAll.remove(note);
                                                    }
                                                });
                                                break first;
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }

                }
        );
    }

    @Override
    public void send(String s) {
        Log.d("MY_LOG", "send: " + s);
    }

    public void checkEventIAmTable(int id) {

        EventBus.getDefault().post(new EventIAmTableChange(true));

        DataSingelton.eventTables.add(id);
    }
}
