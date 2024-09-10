package com.carworkz.dearo.morecta

import com.carworkz.dearo.injection.FragmentScoped
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.pdf.mediator.PdfMediatorImpl
import dagger.Module
import dagger.Provides

/**
 * Created by Farhan on 29/11/17.
 */
@Module
class MoreCtaPresenterModule {

    @Provides
    @FragmentScoped
    fun providesPdfMediator(): PdfMediator {
        return PdfMediatorImpl()
    }
}