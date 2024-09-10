package com.carworkz.dearo.addjobcard.createjobcard.voice;

import com.carworkz.dearo.injection.FragmentScoped;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Farhan on 18/8/17.
 */
@Module
public class VoicePresenterModule {

    private final VoiceContract.View view;

    public VoicePresenterModule(VoiceContract.View view) {
        this.view = view;
    }


    @Provides
    @FragmentScoped
    VoiceContract.View providesView(){
        return view;
    }

}
