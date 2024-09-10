package com.carworkz.library.filepicker.fragments

interface PhotoPickerFragmentListener {
    fun onItemSelected()
    fun setToolbarTitle(count: Int)
}