package com.matf.filemanager.UtilClasses

enum class SaveStatus {
    FILESAVED, ERRORSAVING, FILENOTCHANGED;

    override fun toString(): String {
        return when {
            this == FILESAVED -> {
                "File saved!"
            }
            this == ERRORSAVING -> {
                "An error has occurred!"
            }
            else -> {
                "There are no changes to save!"
            }
        }
    }
}
