package com.wwe

fun requestData(type: Int, call: (code: Int, type: Int) -> Unit) {
    call(200, type)
}