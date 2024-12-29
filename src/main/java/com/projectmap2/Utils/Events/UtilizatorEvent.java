package com.projectmap2.Utils.Events;

import com.projectmap2.Domain.Utilizator;

public class UtilizatorEvent implements Event{
    EntityEventType eventType;
    private Utilizator data,oldData;

    public UtilizatorEvent(EntityEventType eventType, Utilizator data) {
        this.eventType = eventType;
        this.data = data;
    }

    public UtilizatorEvent(EntityEventType eventType, Utilizator data, Utilizator oldData) {
        this.eventType = eventType;
        this.data = data;
        this.oldData = oldData;
    }

    public EntityEventType getEventType() {
        return eventType;
    }

    public Utilizator getData() {
        return data;
    }

    public Utilizator getOldData() {
        return oldData;
    }
}
