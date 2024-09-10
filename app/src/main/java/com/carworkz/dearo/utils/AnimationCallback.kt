package com.carworkz.dearo.utils

interface AnimationCallback {

    enum class Toggle {
        UP, DOWN
    }

    fun onAnimationEnd(endState: Toggle)
}