package com.matf.filemanager.versions

/**
 * Interfejs za podrsku objekata kojima zelimo da pamtimo istoriju promena
 *
 * @param T Tip objekta za koji zelimo da cuvamo istoriju
 */
interface Versionable<T> {

    /**
     * Vraca trenutno stanje objekta
     */
    fun getCurrentInstance(): T?

    /**
     * Prelazi u novo stanje
     *
     * @param newElement Stanje u koje treba da se predje
     * @return Da li je uspela promena stanja
     */
    fun goTo(newElement: T): Boolean

    /**
     * Povratak na prethodno stanje
     *
     * @return Da li je uspela promena stanja
     */
    fun goBack(): Boolean

    /**
     * Povratak na sledece stanje
     * Moguce samo ukoliko je prethodno pozvana goBack funkcija
     *
     * @return Da li je uspela promena stanja
     */
    fun goForward(): Boolean

}