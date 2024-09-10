package com.carworkz.dearo.addjobcard.createjobcard.accidental

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.Accidental
import com.carworkz.dearo.domain.entities.FileObject
import com.carworkz.dearo.domain.entities.InsuranceCompany
import com.carworkz.dearo.domain.entities.JobCard

interface AccidentalContract {
    interface View : BaseView<Presenter> {
        fun displayJobCardData(jobCard: JobCard, obj: List<InsuranceCompany>)
        fun displayCompanyList(companyList: List<InsuranceCompany>)
        fun displayCity(city: String)
        fun moveToNextScreen()
        fun displayDocuments(obj: List<FileObject>)
        fun insuranceError(error: String)
        fun updateDocView()
        fun showPinCodeError(error: String)
    }

    interface Presenter : BasePresenter {
        fun getJobCardById(jobCardId: String)
        fun getCompanyList(jobCard: JobCard)
        fun getCityByPincode(pincode: Int)
        fun saveData(jobCardId: String, accidental: Accidental)
        fun getDocuments(jobCardID: String)
        fun deleteDocument(fileObject: FileObject)
        fun clearCache()
    }
}