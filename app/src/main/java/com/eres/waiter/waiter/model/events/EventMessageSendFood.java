package com.eres.waiter.waiter.model.events;

public class EventMessageSendFood {
    boolean sendFood;

    public boolean isSendFood() {
        return sendFood;
    }

    public EventMessageSendFood(boolean sendFood) {
        this.sendFood = sendFood;
    }
}
