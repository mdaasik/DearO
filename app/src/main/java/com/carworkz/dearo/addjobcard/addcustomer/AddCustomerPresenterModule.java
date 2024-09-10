package com.carworkz.dearo.addjobcard.addcustomer;
import com.carworkz.dearo.injection.ActivityScoped;

import dagger.Module;
import dagger.Provides;

/**
 * Created by kush on 17/8/17.
 */
@Module
public class AddCustomerPresenterModule {

    private final AddCustomerContract.View view;

    public AddCustomerPresenterModule(AddCustomerContract.View view) {
        this.view = view;
    }

    @Provides
    @ActivityScoped
    public AddCustomerContract.View providesView(){
        return view;
    }
}
