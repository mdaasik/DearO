package com.carworkz.dearo.addjobcard.addcustomer;


import com.carworkz.dearo.injection.ActivityScoped;

import dagger.Subcomponent;

/**
 * Created by kush on 17/8/17.
 */
@ActivityScoped
@Subcomponent(modules = AddCustomerPresenterModule.class)
public interface AddCustomerComponent {
    void inject(AddCustomerActivity addCustomerActivity);
}
