package com.carworkz.dearo.addjobcard.createjobcard.voice;

import com.carworkz.dearo.injection.FragmentScoped;

import dagger.Subcomponent;

/**
 * Created by Farhan on 18/8/17.
 */

@FragmentScoped
@Subcomponent(modules = VoicePresenterModule.class)
public interface VoiceComponent {

    void inject(VoiceFragment voiceFragment);
}
