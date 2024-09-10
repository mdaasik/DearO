package com.carworkz.dearo.addjobcard.createjobcard.damage;

import com.carworkz.dearo.injection.FragmentScoped;

import dagger.Subcomponent;

/**
 * Created by kush on 23/8/17.
 */
@FragmentScoped
@Subcomponent(modules = DamagePresenterModule.class)
public interface DamageComponent {

    void inject(DamageFragment damageFragment);
}
