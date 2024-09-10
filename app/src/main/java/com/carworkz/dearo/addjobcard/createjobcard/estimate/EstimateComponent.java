package com.carworkz.dearo.addjobcard.createjobcard.estimate;

import com.carworkz.dearo.addjobcard.quickjobcard.quickestimate.QuickEstimateFragment;
import com.carworkz.dearo.injection.FragmentScoped;

import dagger.Subcomponent;

/**
 * Created by Farhan on 19/8/17.
 */

@FragmentScoped
@Subcomponent(modules = EstimatePresenterModule.class)
public interface EstimateComponent {

    void inject(EstimateFragment estimateFragment);

    void inject(QuickEstimateFragment quickEstimateFragment);
}
