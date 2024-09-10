package com.carworkz.dearo.addjobcard.createjobcard.inventory;

import com.carworkz.dearo.injection.FragmentScoped;

import dagger.Subcomponent;

/**
 * Created by Farhan on 18/8/17.
 */
@FragmentScoped
@Subcomponent(modules = InventoryPresenterModule.class)
public interface InventoryComponent {

    void inject(InventoryFragment inventoryFragment);
}
