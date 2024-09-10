package com.carworkz.dearo.addjobcard.createjobcard.damage

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.FileObject

/**
 * Created by kush on 23/8/17.
 */

interface DamageContract {
    interface View : BaseView<Presenter> {
        fun displayDamages(imageList: List<FileObject>)

        fun displayPdcImages(imageList: List<FileObject>)

        fun updateAlert()

        fun moveToNextScreen()
    }

    interface Presenter : BasePresenter {
        fun getGalleryData(jobId: String)

        fun getPdcData(jobId: String)

        fun deleteDamage(fileObject: FileObject)

        fun editCaption(Image_id: String, caption: String, jobId: String)
    }
}
