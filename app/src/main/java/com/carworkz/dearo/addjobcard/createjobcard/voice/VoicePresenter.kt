package com.carworkz.dearo.addjobcard.createjobcard.voice

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.injection.FragmentScoped
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Farhan on 14/8/17.
 */
@FragmentScoped
class VoicePresenter @Inject constructor(private val dataRepository: DearODataRepository, private var view: VoiceContract.View?) : VoiceContract.Presenter {

    override fun getVerbatimForJobCard(jobCardId: String) {
        view?.showProgressIndicator()
        dataRepository.getJobCardDetails(jobCardId, arrayOf("vehicle"), object : DataSource.OnResponseCallback<JobCard> {
            override fun onSuccess(obj: JobCard) {
                view?.dismissProgressIndicator()
                view?.displayVoice(obj.verbatim)
                view?.displayResource(obj.workshopResource, obj.serviceAdviserId, obj.bayId, obj.technicianId, obj.source,obj.sourceType)
            }

            override fun onError(error: ErrorWrapper) {
                view?.showGenericError(error.errorMessage)
                view?.dismissProgressIndicator()
            }
        })
    }
    
/*    override fun getSource(appointmentId : String?) {
        view?.showProgressIndicator()
        if(appointmentId!=null)
        {
        dataRepository.getAppointmentsByID(appointmentId, object  : DataSource.OnResponseCallback<Appointment> {
            override fun onSuccess(obj : Appointment) {
                view?.dismissProgressIndicator()
                view?.displaySource(obj.source)
            }
    
            override fun onError(error : ErrorWrapper) {
                view?.showGenericError(error.errorMessage)
                view?.dismissProgressIndicator()
            }
    
        })
        }
        else{
            view?.displaySource(null)
        }
    }*/
    
    override fun saveVoice(jobCardId: String, verbatim: Verbatim) {
        dataRepository.saveVoice(jobCardId, verbatim, object : DataSource.OnResponseCallback<Verbatim> {
            override fun onSuccess(obj: Verbatim) {
                view?.moveToNextScreen()
            }

            override fun onError(error: ErrorWrapper) {
                view?.showGenericError(error.errorMessage)
            }
        })
    }

    override fun getVerbatimSuggestions(query: String) {
        dataRepository.getCustomerConcernSuggestions(query, object : DataSource.OnResponseCallback<List<CustomerConcern>> {
            override fun onSuccess(obj: List<CustomerConcern>) {
                view?.displayVoiceSuggestions(obj)
            }

            override fun onError(error: ErrorWrapper) {
                Timber.d("Error fetching suggestions ${error.errorMessage}")
            }
        })
    }

    override fun getSourceTypes() {
        view?.showProgressIndicator()
        dataRepository.getSourceTypes( object : DataSource.OnResponseCallback<List<CustomerSourceType>> {
            override fun onSuccess(obj: List<CustomerSourceType>) {
                view?.dismissProgressIndicator()
                view?.displaySourceTypes(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                Timber.d("Error fetching suggestions ${error.errorMessage}")
            }
        })
    }

    override fun getSources(sourceTypeId: String) {
        view?.showProgressIndicator()
        dataRepository.getSourcesById(sourceTypeId, object : DataSource.OnResponseCallback<List<CustomerSource>> {
            override fun onSuccess(obj: List<CustomerSource>) {
                view?.dismissProgressIndicator()
                view?.displaySources(obj)
            }

            override fun onError(error: ErrorWrapper) {
                view?.dismissProgressIndicator()
                Timber.d("Error fetching suggestions ${error.errorMessage}")
            }
        })
    }

    override fun start() {
    }

    override fun detachView() {
        view = null
    }
}