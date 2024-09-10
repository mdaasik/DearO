package com.carworkz.dearo.othersyshistory

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [OtherHistoryPresenterModule::class])
interface OtherHistoryComponent {
    fun inject(view: OtherSysHistoryActivity)
}