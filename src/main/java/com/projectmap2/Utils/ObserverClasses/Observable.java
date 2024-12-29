package com.projectmap2.Utils.ObserverClasses;

import com.projectmap2.Utils.Events.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}