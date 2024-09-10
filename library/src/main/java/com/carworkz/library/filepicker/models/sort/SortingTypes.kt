package com.carworkz.library.filepicker.models.sort

import com.carworkz.library.filepicker.models.Document
import java.util.*

/**
 * Created by gabriel on 10/2/17.
 */
enum class SortingTypes(val comparator: Comparator<Document>?) {
    NAME(NameComparator()), NONE(null);
}