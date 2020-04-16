package com.matf.filemanager.Versions;

public interface JVersionable<T> {
    T getCurrentInstance();
    boolean goTo(T newElement);
    boolean goBack();
    boolean goForward();
}
