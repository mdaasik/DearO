package com.carworkz.dearo.addjobcard.quickjobcard.quickconcerns

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.CustomerConcern
import timber.log.Timber
import javax.inject.Inject

class QuickConcernPresenter @Inject constructor(private var view: QuickConcernsContract.View?, private val repository: DearODataRepository) : QuickConcernsContract.Presenter {
    override fun getSuggestions(query: String) {
        repository.getCustomerConcernSuggestions(query, object : DataSource.OnResponseCallback<List<CustomerConcern>> {
            override fun onSuccess(obj: List<CustomerConcern>) {
                view?.displaySuggestions(obj)
            }

            override fun onError(error: ErrorWrapper) {
                Timber.d("suggestion error ${error.errorMessage}")
            }
        })
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
    }
}