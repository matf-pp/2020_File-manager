package com.matf.filemanager.UtilClasses;

public enum SaveStatus {
    FILESAVED,
    ERRORSAVING,
    FILENOTCHANGED;

    @Override
    public String toString() {
        if(this == SaveStatus.FILESAVED){
            return "File saved!";
        }else if (this == SaveStatus.ERRORSAVING){
            return "An error has occurred!";
        }else{
            return "There are no changes to save!";
        }
    }
}
