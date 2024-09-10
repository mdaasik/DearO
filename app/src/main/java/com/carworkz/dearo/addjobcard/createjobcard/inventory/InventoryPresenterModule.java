package com.carworkz.dearo.addjobcard.createjobcard.inventory;

import com.carworkz.dearo.injection.FragmentScoped;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Farhan on 18/8/17.
 */
@Module
public class InventoryPresenterModule {

    private final InventoryContract.View view;

    InventoryPresenterModule(InventoryContract.View view) {
        this.view = view;
    }

    @Provides
    @FragmentScoped
    InventoryContract.View providesView(){
        return view;
    }
}
