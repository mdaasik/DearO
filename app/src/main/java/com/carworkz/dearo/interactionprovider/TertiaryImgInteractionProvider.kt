package com.carworkz.dearo.interactionprovider

import androidx.annotation.DrawableRes

interface TertiaryImgInteractionProvider : ToolBarImgInteractionProvider {

    @DrawableRes
    fun getTertiaryActionBarImage(): Int

    fun onTertiaryActionBtnClick()
}