package com.carworkz.dearo.predeliverycheck

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.FileObject
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.PdcBase
import com.carworkz.dearo.domain.entities.PdcEntity

interface PdcContract {

    interface View : BaseView<Presenter> {
        fun onFetchJobCard(obj: JobCard)
        fun onFetchChecklist(list :List<PdcBase>)
        fun onSaveSuccess(print: Boolean)
        fun displayPdcImages(imageList: List<FileObject>)
        fun updateAlert()
    }

    interface Presenter : BasePresenter {
        fun getEstimation(jobCardId: String)
        fun getChecklist(jobCardId: String)
        fun saveChecklist(jobCardId: String, pdcEntity: PdcEntity, print: Boolean)

//        fun getGalleryData(jobId: String)

        fun getPdcData(jobId: String)

        fun deleteDamage(fileObject: FileObject)

        fun editCaption(Image_id: String, caption: String, jobId: String)
    }
}