package com.taskail.mixion.utils.steemitutils

/**
 *Created by ed on 1/31/18.
 */

const val TYPE_DTUBE = 1
const val TYPE_YOUTUBE = 2
const val TYPE_DMANIA = 3

fun String.isFromDmania(): Boolean{
    return this.startsWith("<center>")
}