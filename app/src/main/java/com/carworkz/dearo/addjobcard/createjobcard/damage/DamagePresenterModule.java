package com.carworkz.dearo.addjobcard.createjobcard.damage;

import com.carworkz.dearo.injection.FragmentScoped;

import dagger.Module;
import dagger.Provides;

/**
 * Created by kush on 23/8/17.
 */
@Module
public class DamagePresenterModule {
    private final DamageContract.View view;

    public DamagePresenterModule(DamageContract.View view) {
        this.view = view;
    }

    @Provides
    @FragmentScoped
    DamageContract.View providesView(){return view;}
}
