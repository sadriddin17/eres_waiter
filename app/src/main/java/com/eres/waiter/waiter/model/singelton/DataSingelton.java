package com.eres.waiter.waiter.model.singelton;

import android.content.Context;
import android.util.Log;

import com.eres.waiter.waiter.model.ArmoredTables;
import com.eres.waiter.waiter.model.DataMenu;
import com.eres.waiter.waiter.model.EmptyTable;
import com.eres.waiter.waiter.model.IAmTables;
import com.eres.waiter.waiter.model.OrderItemsItem;
import com.eres.waiter.waiter.model.ProductsItem;
import com.eres.waiter.waiter.model.TablesItem;
import com.eres.waiter.waiter.model.test.DataAddList;
import com.eres.waiter.waiter.model.test.NotificationEventAlarm;
import com.eres.waiter.waiter.retrofit.ApiClient;
import com.eres.waiter.waiter.retrofit.ApiInterface;
import com.eres.waiter.waiter.viewpager.helper.ObservableCollection;
import com.eres.waiter.waiter.viewpager.model.Hall;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataSingelton {
    public static DataSingelton singelton;
    public static ArrayList<EmptyTable> emptyTables;
    private static ArrayList<ArmoredTables> armoredTables;
    public static ArrayList<IAmTables> iAmTables;
    private ObservableCollection<Hall> halls;
    private static Context mC;
    private static ArrayList<DataMenu> dataMenus;
    private static ArrayList<DataAddList> productsItems;
    private static boolean load = false;
    private static boolean loadITable = false;
    private static boolean loadArmoredTable = false;
    public static HashSet<ProductsItem> testSet;
    private static final String TAG = "DataSingelton";
    ApiInterface apiInterface;
    public static ObservableCollection<ProductsItem> searchItems;
    public static HashMap<String, Integer> eventNotifAlarm;
    public static ArrayList<String> strings;

    public ObservableCollection<Hall> getHalls() {
        return halls;
    }

    public void setHalls(ObservableCollection<Hall> halls) {
        this.halls = halls;
    }

    public static ArrayList<OrderItemsItem> getMyOrders() {
        return myOrders;
    }

    public static void setMyOrders(ArrayList<OrderItemsItem> myOrders) {
        DataSingelton.myOrders = myOrders;
    }

    private static ArrayList<OrderItemsItem> myOrders;


    public static ArrayList<DataAddList> getProducts() {
        return productsItems;
    }

    public static void setProducts(ArrayList<DataAddList> productsItems) {
        DataSingelton.productsItems = productsItems;
    }

    public static boolean isLoadITable() {
        return loadITable;
    }

    public static boolean isLoadArmoredTable() {
        return loadArmoredTable;
    }

    public static ArrayList<ArmoredTables> getArmoredTables() {
        return isLoadArmoredTable() ? armoredTables : null;
    }

    public static ArrayList<IAmTables> getiAmTables() {
        return iAmTables;
    }

    public static ArrayList<EmptyTable> getEmptyTables() {
        return emptyTables;
    }

    public static ArrayList<DataMenu> getDataMenus() {
        return isLoad() ? dataMenus : null;
    }

    public static boolean isLoad() {
        return load;
    }


    private DataSingelton() {
        apiInterface = ApiClient.getRetrofit(mC).create(ApiInterface.class);
        emptyTables = new ArrayList<>();
        halls = new ObservableCollection<>();
        dataMenus = new ArrayList<>();
        productsItems = new ArrayList<>();
        testSet = new HashSet<>();
        myOrders = new ArrayList<>();
        searchItems = new ObservableCollection<>();
        armoredTables = new ArrayList<>();
        iAmTables = new ArrayList<>();
        strings = new ArrayList<>();
        eventNotifAlarm = new HashMap<>();
    }


    public IAmTables parseTable(TablesItem item) {
        return new IAmTables(item.getHallId(), item.getDescription(), item.getId(), item.isExtOrder(), item.getDefaultWaiterId(), item.getCurrentWaiterId(), item.getName(), item.getTableState());
    }

    public void addImTableData(int id) {
        for (int i = 0; i < getEmptyTables().size(); i++) {
            for (int i1 = 0; i1 < getEmptyTables().get(i).getTables().size(); i1++) {
                if (getEmptyTables().get(i).getTables().get(i1).getId() == id) {
                    getiAmTables().add(parseTable(getEmptyTables().get(i).getTables().get(i1)));
                    getEmptyTables().get(i).getTables().remove(i1);
                    Log.d(TAG, "addImTableData: " + i1 + "==" + id);
                    break;
                }
            }
        }

    }


    public static DataSingelton getInstance(Context context) {
        if (singelton == null) {
            mC = context;
            singelton = new DataSingelton();
        }
        return singelton;
    }

    public void loadArmoredTable() {
        Call<ArrayList<ArmoredTables>> call = apiInterface.getArmoredTables();
        call.enqueue(new Callback<ArrayList<ArmoredTables>>() {
            @Override
            public void onResponse(Call<ArrayList<ArmoredTables>> call, Response<ArrayList<ArmoredTables>> response) {
                if (response.body() != null) {
                    armoredTables.clear();
                    armoredTables.addAll(response.body());
                    loadArmoredTable = true;
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ArmoredTables>> call, Throwable t) {
                Log.d(TAG, "onFailure:  Armored ");
                loadArmoredTable = false;
            }
        });

    }

    public void loadITable() {
        Call<ArrayList<IAmTables>> call = apiInterface.getIamTables();
        call.enqueue(new Callback<ArrayList<IAmTables>>() {
            @Override
            public void onResponse(Call<ArrayList<IAmTables>> call, Response<ArrayList<IAmTables>> response) {
                if (response.body() != null) {
                    iAmTables.clear();
                    iAmTables.addAll(response.body());
                    Log.d("MY_LOG", "onResponse: " + iAmTables.size() + "==" + response.body().size());

                }
            }

            @Override
            public void onFailure(Call<ArrayList<IAmTables>> call, Throwable t) {
            }
        });

    }


    public void loadEmptyTable() {
        emptyTables = new ArrayList<>();
        Call<ArrayList<EmptyTable>> call = apiInterface.getEmptyTable();
        call.enqueue(new Callback<ArrayList<EmptyTable>>() {
            @Override
            public void onResponse(Call<ArrayList<EmptyTable>> call, Response<ArrayList<EmptyTable>> response) {
                if (response.body() != null) {
                    emptyTables.clear();
                    emptyTables.addAll(response.body());
                    Log.d(TAG, "onResponse: " + response.body().size() + "asd" + emptyTables.size());

                }
            }

            @Override
            public void onFailure(Call<ArrayList<EmptyTable>> call, Throwable t) {
                Log.d(TAG, "onFailure: Error load table !!!! ");
            }
        });

    }

    public void loadData() {
        Call<ArrayList<DataMenu>> call = apiInterface.getMenuAll();
        call.enqueue(new Callback<ArrayList<DataMenu>>() {
            @Override
            public void onResponse(Call<ArrayList<DataMenu>> call, Response<ArrayList<DataMenu>> response) {
                if (response.body() != null) {
                    dataMenus.clear();
                    dataMenus.addAll(response.body());
                    load = true;
                    Log.d(TAG, TAG + dataMenus.size());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DataMenu>> call, Throwable t) {
                Log.d(TAG, "onFailure: DataMenu");
                t.printStackTrace();
            }
        });
    }

}
