package com.matf.filemanager.Versions;

import java.util.ArrayList;

public class JStateSaver<T> implements JVersionable<T> {
    private ArrayList<T> history;
    private int currentInstanceIndex;

    public JStateSaver(T initialElement) {
        currentInstanceIndex = 0;
        history = new ArrayList<>();
        history.add(initialElement);
    }

    @Override
    public T getCurrentInstance() {
        return history.get(currentInstanceIndex);
    }

    @Override
    public boolean goTo(T newElement) {
        if(currentInstanceIndex == history.size() - 1){
            history.add(newElement);
            currentInstanceIndex++;
        }else{
            for(int i =  history.size() - 1; i >= currentInstanceIndex + 1; i--){
                history.remove(i);
            }
            history.add(newElement);
            currentInstanceIndex++;
        }

        return true;
    }

    @Override
    public boolean goBack() {
        if(currentInstanceIndex == 0) return false;
        currentInstanceIndex--;
        return true;
    }

    @Override
    public boolean goForward() {
        if(currentInstanceIndex == history.size() - 1) return false;
        currentInstanceIndex++;
        return true;
    }
}
