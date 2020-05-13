package com.matf.filemanager.util

/**
 * Enum koji oznacava tip akcije koju treba izvrsiti nad selektovanim fajlovima
 *
 * Moguce vrednosti su:
 * COPY - Fajlove treba kopirati
 * MOVE - Fajlove treba premestiti
 * DELETE - Fajlove treba obrisati
 */
enum class FileActions {
    COPY,
    MOVE,
    DELETE
}