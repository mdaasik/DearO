package com.carworkz.dearo.interactionprovider

/**
 * Created by Farhan on 26/10/17.
 */
interface ToolBarInteractionProvider : DefaultInteractionProvider {

    fun getActionBtnTitle(): String

    fun onActionBtnClick()
}