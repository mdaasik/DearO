package com.carworkz.dearo.cronjobs.forceupdatestatus

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView

/**
 * Created by ambab on 8/11/17.
 */
interface ForceUpdateContract {

    interface View : BaseView<Presenter> {
        fun onJobDone()

        fun onJobFailure()
    }

    interface Presenter : BasePresenter {
        fun checkForceUpdate(appName: String, platform: String, versionCode: Int)
    }
}
