package com.example.file_manager.inf
//interface xử lý event back của Device
interface OnBackPressed {
    fun onClick()
    fun isClosed(): Boolean
}