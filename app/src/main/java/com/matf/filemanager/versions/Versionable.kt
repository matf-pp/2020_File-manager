package com.matf.filemanager.versions

interface Versionable<T> {
    fun getCurrentInstance(): T?
    fun goTo(newElement: T): Boolean
    fun goBack(): Boolean
    fun goForward(): Boolean
}