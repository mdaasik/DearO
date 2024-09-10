package com.carworkz.dearo.invoices.invoiceremarks

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class InvoiceRemarksPresenterModule(private val view: InvoiceRemarksContract.View) {
    @ActivityScoped
    @Provides
    fun providesView(): InvoiceRemarksContract.View {
        return view
    }
}