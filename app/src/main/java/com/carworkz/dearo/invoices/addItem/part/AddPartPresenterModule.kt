package com.carworkz.dearo.invoices.addItem.part

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

/**
 * Created by kush on 13/9/17.
 */
@Module
class AddPartPresenterModule constructor(val view: AddPartContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): AddPartContract.View {
        return view
    }
}