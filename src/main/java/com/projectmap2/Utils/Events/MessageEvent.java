package com.projectmap2.Utils.Events;

import com.projectmap2.Domain.Message;

public class MessageEvent implements Event{
    EntityEventType eventType;
    private Message data,oldData;

    public MessageEvent(EntityEventType eventType, Message data) {
        this.eventType = eventType;
        this.data = data;
    }

    public MessageEvent(EntityEventType eventType, Message data, Message oldData) {
        this.eventType = eventType;
        this.data = data;
        this.oldData = oldData;
    }

    public EntityEventType getEventType() {
        return eventType;
    }

    public Message getData() {
        return data;
    }

    public Message getOldData() {
        return oldData;
    }
}
