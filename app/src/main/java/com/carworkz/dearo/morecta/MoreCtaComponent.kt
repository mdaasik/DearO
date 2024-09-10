package com.carworkz.dearo.morecta

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Subcomponent

/**
 * Created by Farhan on 29/11/17.
 */
@FragmentScoped
@Subcomponent(modules = [(MoreCtaPresenterModule::class)])
interface MoreCtaComponent {

    fun inject(moreCtaFragment: MoreCtaListDialogFragment)
}