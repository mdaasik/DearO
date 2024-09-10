package com.carworkz.dearo.mrn

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.Part

interface MrnContract {

    interface View : BaseView<Presenter> {

        fun displayMrn(parts: List<Part>?)
    }

    interface Presenter : BasePresenter {

        fun getMrnParts(jobCardId: String)
    }
}