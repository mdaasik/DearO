package com.carworkz.dearo.invoices.invoiceremarks

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [(InvoiceRemarksPresenterModule::class)])
interface InvoiceRemarkComponent {
        fun inject(activity: InvoiceRemarksActivity)
}