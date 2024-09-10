package com.carworkz.dearo.addjobcard.createjobcard.damage.damagegallery

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.FileObject

interface DamageGalleryContract {
    interface View : BaseView<Presenter> {
        fun displayGalleryImages(list: ArrayList<FileObject>)
    }

    interface Presenter : BasePresenter {
        fun getGalleryImages(jobCardId: String, sort: String)
        fun getPdcData(jobId: String)
    }
}