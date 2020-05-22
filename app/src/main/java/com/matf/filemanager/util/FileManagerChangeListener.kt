package com.matf.filemanager.util

import java.io.File

/**
 * Osluskivac za promene i dogadjaje koje signalizira FileManager
 * Koristi se za azuriranje liste fajlova kao i za njihovo otvaranje po zahtevu
 */
interface FileManagerChangeListener {
    /**
     * Callback funkcija koja je pozvana kada se promeni lista fajlova ili fajlovi u clipboard-u
     */
    fun onEntriesChange()

    /**
     * Callback funkcija koja je pozvana kada se promeni rezim za selekciju
     *
     * @param mode Novi rezim za selekciju
     */
    fun onSelectionModeChange(mode : MenuMode)

    /**
     * Callback funkcija koja je pozvana kada se promeni rezim clipboard-a
     *
     * @param mode Novi rezim za clipboard
     */
    fun onClipboardChange(mode : ClipboardMode)

    /**
     * Callback funkcija kojom se zahteva otvaranje fajla pomocu ove aplikacije
     * Ako ga mi ne podrzavamo, onda ce se otvoriti nekom sistemskom aplikacijom
     *
     * @param file Fajl koji treba otvoriti
     * @return Da li je fajl uspesno otvoren
     */
    fun onRequestFileOpen(file: File): Boolean

    /**
     * Callback funkcija kojom se zahteva otvaranje fajla pomocu sistemske aplikacije
     *
     * @param file Fajl koji treba otvoriti
     * @return Da li je fajl uspesno otvoren
     */
    fun onRequestFileOpenWith(file: File) : Boolean

    /**
     * Callback funkcija koja zapocinje kopiranje fajlova
     *
     * @param targets Lista fajlova koje treba kopirati
     * @param dest Destinacija za kopiranje
     */
    fun copyFile(targets: List<File>, dest: File)

    /**
     * Callback funkcija koja zapocinje premestanje fajlova
     *
     * @param targets Lista fajlova koje treba premestiti
     * @param dest Destinacija za premestanje
     */
    fun moveFile(targets: List<File>, dest: File)

    /**
     * Callback funkcija koja zapocinje brisanje fajlova
     *
     * @param targets Lista fajlova koje treba obrisati
     */
    fun deleteFile(targets: List<File>)
}