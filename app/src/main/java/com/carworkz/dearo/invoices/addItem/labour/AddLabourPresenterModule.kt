package com.carworkz.dearo.invoices.addItem.labour

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

/**
 * Created by kush on 13/9/17.
 */
@Module
class AddLabourPresenterModule(val view: AddLabourContract.View) {

    @ActivityScoped
    @Provides
    fun providesView(): AddLabourContract.View {
        return view
    }
}