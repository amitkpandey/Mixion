package com.taskail.mixion.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 *Created by ed on 1/29/18.
 */

fun View.hideSoftKeyboard(){
    val keyboard = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    keyboard.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.fadeInAnimation(){
    this.alpha = 0f
    this.visibility = View.VISIBLE
    this.animate().alpha(1f)
            .setDuration(this.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .setListener(null)
}

fun View.fadeOutAnimation(callback: FadeOutCallBack){
    this.animate().alpha(0f)
            .setDuration(this.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    this@fadeOutAnimation.visibility = View.GONE
                    callback.onAnimationEnd()
                }
            })
}

interface FadeOutCallBack{
    fun onAnimationEnd()
}