package com.juansandoval.sandovalportfolio.ui.auth

interface AuthListener {
    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String?)
}