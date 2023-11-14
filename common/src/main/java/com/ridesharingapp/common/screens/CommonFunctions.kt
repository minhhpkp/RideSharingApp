package com.ridesharingapp.common.screens

fun showError(field: String?, fieldError: Boolean): Boolean {
    return if (field == null) false else fieldError
}