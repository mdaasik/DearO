package com.carworkz.dearo.addjobcard.createjobcard.voice

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.*


interface VoiceContract {

    interface View : BaseView<Presenter> {

        fun moveToNextScreen()

        fun displayResource(workshopResource: WorkshopResource, serviceAdviserId: String?, bayId: String?, technicianId: String?, source: String?,sourceType: String?)
       
//        fun displaySource(source: String?)

        fun displaySourceTypes(sourceTypes : List<CustomerSourceType>)

        fun displaySources(sources : List<CustomerSource>)

        fun displayVoice(list: List<String>?)

        fun displayVoiceSuggestions(concerns: List<CustomerConcern>)
    }

    interface Presenter : BasePresenter {

        fun saveVoice(jobCardId: String, verbatim: Verbatim)

        fun getVerbatimForJobCard(jobCardId: String)
        
//        fun getSource(appointmentId: String?)

        fun getVerbatimSuggestions(query: String)

        fun getSourceTypes()

        fun getSources(sourceTypeId : String)
    }
}