package com.example.example_unlock

import android.widget.ImageView

interface ILock {
    fun changeImage(model: Model,isNormal:Boolean)

    fun changeVisibility(view: ImageView)
}