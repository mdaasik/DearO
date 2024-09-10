package com.carworkz.dearo.search

import com.carworkz.dearo.injection.ActivityScoped
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.pdf.mediator.PdfMediatorImpl
import dagger.Module
import dagger.Provides

/**
 * Created by kush on 11/9/17.
 */
@Module
class SearchPresenterModule(private val view: SearchContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): SearchContract.View {
        return view
    }

    @Provides
    @ActivityScoped
    fun providesPdfMediator(): PdfMediator {
        return PdfMediatorImpl()
    }
}
