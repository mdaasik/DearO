package com.carworkz.dearo.interactionprovider

import androidx.annotation.DrawableRes

/**
 * Created by Farhan on 26/10/17.
 */
interface ToolBarImgInteractionProvider : DefaultInteractionProvider {

    @DrawableRes
    fun getNavigationImage(): Int

    fun onActionBtnClick()

    fun onSecondaryActionBtnClick()

    @DrawableRes
    fun getActionBarImage(): Int

    @DrawableRes
    fun getSecondaryActionBarImage(): Int
}