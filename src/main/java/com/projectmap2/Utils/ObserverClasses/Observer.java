package com.projectmap2.Utils.ObserverClasses;

import com.projectmap2.Utils.Events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}
