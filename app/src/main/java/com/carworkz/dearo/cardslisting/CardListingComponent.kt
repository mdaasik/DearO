package com.carworkz.dearo.cardslisting

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Subcomponent

/**
 * Created by farhan on 03/01/18.
 */

@FragmentScoped
@Subcomponent(modules = [(CardListingPresenterModule::class)])
interface CardListingComponent {
    fun inject(cardListingFragment: CardListingFragment)
}