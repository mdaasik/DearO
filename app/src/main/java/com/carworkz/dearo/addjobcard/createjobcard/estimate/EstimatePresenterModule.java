package com.carworkz.dearo.addjobcard.createjobcard.estimate;

import com.carworkz.dearo.injection.FragmentScoped;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Farhan on 19/8/17.
 */

@Module
public class EstimatePresenterModule {

    private final EstimateContract.View view;

    public EstimatePresenterModule(EstimateContract.View view) {
        this.view = view;
    }

    @Provides
    @FragmentScoped
    EstimateContract.View providesView() {
        return view;
    }
}
