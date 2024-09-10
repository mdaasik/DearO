package com.carworkz.dearo.interactionprovider

/**
 * Created by Farhan on 26/10/17.
 */
interface MultiTitleInteractionProvider {

    fun getToolBarTitleOne(): String?

    fun getToolBarTitleTwo(): String

    fun getActionBtnTitle(): String

    fun onActionBtnClick()
}