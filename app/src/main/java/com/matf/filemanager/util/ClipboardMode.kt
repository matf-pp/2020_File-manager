package com.matf.filemanager.util

/**
 * Enum koji oznacava stanje clipboard-a
 *
 * Moguce vrednosti su:
 * NONE - Nista se ne cuva trenutno
 * COPY - Cuvaju se fajlovi koji ce biti kopirani
 * CUT - Cuvaju se fajlovi koji ce biti premesteni
 */
enum class ClipboardMode {
    NONE,
    COPY,
    CUT
}