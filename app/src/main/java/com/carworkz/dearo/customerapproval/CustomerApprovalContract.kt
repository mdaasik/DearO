package com.carworkz.dearo.customerapproval

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.CostEstimate
import com.carworkz.dearo.domain.entities.JobCard

interface CustomerApprovalContract {

    interface View : BaseView<Presenter> {
        fun onFetchJobCard(obj: JobCard)
        fun onSaveSuccess()
    }

    interface Presenter : BasePresenter {
        fun getEstimation(jobCardId: String)
        fun saveEstimation(jobCardId: String, obj: CostEstimate)
    }
}