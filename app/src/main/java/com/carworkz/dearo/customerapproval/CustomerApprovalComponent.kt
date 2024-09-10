package com.carworkz.dearo.customerapproval

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [CustomerApprovalModule::class])
interface CustomerApprovalComponent {

    fun inject(activity: CustomerApprovalActivity)
}