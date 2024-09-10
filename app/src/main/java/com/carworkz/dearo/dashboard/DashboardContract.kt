package com.carworkz.dearo.dashboard

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.WorkshopResource

interface DashboardContract {

    interface Presenter : BasePresenter {
        fun getDashBoardDetails()
    }

    interface View : BaseView<Presenter> {

        fun moveToNextScreen()

        fun showDashBoardData(obj: WorkshopResource)
    }
}