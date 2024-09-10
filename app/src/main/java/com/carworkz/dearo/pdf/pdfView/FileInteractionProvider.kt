package com.carworkz.dearo.pdf.pdfView

import java.io.File

interface FileInteractionProvider {
    fun getPdfFile(): File?

    fun getPdfFileUrl(): String?

    fun isFileDownloading(): Boolean
}