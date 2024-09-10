package com.carworkz.dearo.cardslisting

import com.carworkz.dearo.injection.FragmentScoped
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.pdf.mediator.PdfMediatorImpl
import dagger.Module
import dagger.Provides

/**
 * Created by farhan on 03/01/18.
 */
@Module
class CardListingPresenterModule(private val view: CardListingContract.View) {

    @Provides
    @FragmentScoped
    fun providesView(): CardListingContract.View {
        return view
    }

    @Provides
    @FragmentScoped
    fun providesPdfMediator(): PdfMediator {
        return PdfMediatorImpl()
    }
}