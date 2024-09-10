package com.carworkz.dearo.addjobcard.quickjobcard.quickconcerns

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.CustomerConcern

interface QuickConcernsContract {

    interface View : BaseView<Presenter> {

        fun displaySuggestions(suggestions: List<CustomerConcern>)
    }

    interface Presenter : BasePresenter {
        fun getSuggestions(query: String)
    }
}