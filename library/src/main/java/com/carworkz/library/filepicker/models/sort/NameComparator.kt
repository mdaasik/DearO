package com.carworkz.library.filepicker.models.sort

import com.carworkz.library.filepicker.models.Document
import java.util.*

/**
 * Created by gabriel on 10/2/17.
 */
class NameComparator : Comparator<Document> {
    override fun compare(o1: Document, o2: Document): Int {
        return o1.name.toLowerCase(Locale.getDefault()).compareTo(o2.name.toLowerCase(Locale.getDefault()))
    }
}