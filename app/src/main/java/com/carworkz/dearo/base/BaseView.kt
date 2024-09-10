package com.carworkz.dearo.base

/**
 * Created by Farhan on 25/7/17.
 */

/*out is alternative of <? extends T>
* in is alternative of <? super T>
* */
interface BaseView<T> where T : BasePresenter {

    fun showProgressIndicator()

    fun dismissProgressIndicator()

    fun showGenericError(errorMsg: String)
}