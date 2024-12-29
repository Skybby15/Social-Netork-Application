package com.projectmap2.Utils.Events;

import com.projectmap2.Domain.Prietenie;

public class PrietenieEvent implements Event{
    EntityEventType eventType;
    private Prietenie data,oldData;

    public PrietenieEvent(EntityEventType eventType, Prietenie data) {
        this.eventType = eventType;
        this.data = data;
    }

    public PrietenieEvent(EntityEventType eventType, Prietenie data, Prietenie oldData) {
        this.eventType = eventType;
        this.data = data;
        this.oldData = oldData;
    }

    public EntityEventType getEventType() {
        return eventType;
    }

    public Prietenie getData() {
        return data;
    }

    public Prietenie getOldData() {
        return oldData;
    }
}