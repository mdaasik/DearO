package com.carworkz.dearo.addjobcard.createjobcard.damage

import com.carworkz.dearo.domain.entities.FileObject

/**
 * Created by kush on 1/9/17.
 */

interface EditDamageCallback {
    fun onEditCaption(imageId: String, caption: String)
    fun onDeleteImage(fileObject: FileObject)
}
