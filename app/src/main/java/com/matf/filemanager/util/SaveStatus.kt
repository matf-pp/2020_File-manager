package com.matf.filemanager.util

enum class SaveStatus {
    SAVED, ERROR_SAVING, NOT_CHANGED;

    override fun toString(): String {
        return when {
            this == SAVED -> {
                "File saved!"
            }
            this == ERROR_SAVING -> {
                "An error has occurred!"
            }
            else -> {
                "There are no changes to save!"
            }
        }
    }
}
