package com.carworkz.dearo.addjobcard.createjobcard.customercarsearch

import com.carworkz.dearo.injection.ActivityScoped
import com.carworkz.dearo.injection.FragmentScoped
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.pdf.mediator.PdfMediatorImpl
import dagger.Module

import dagger.Provides

/**
 * Created by kush on 17/8/17.
 */

@Module
class CustomerCarSearchPresenterModule(private val view: CustomerCarSearchContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): CustomerCarSearchContract.View {
        return view
    }

    @Provides
    @ActivityScoped
    fun providesPdfMediator(): PdfMediator {
        return PdfMediatorImpl()
    }


}
