package com.wwe

import com.google.gson.Gson

fun requestData(type: Int, call: (code: Int, type: Int) -> Unit) {
    call(200, type)
}

class HappySingleton private constructor() {

    companion object {

        // 方式一
       // val INSTANCE1 by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { WorkSingleton() }

        // 方式二 默认就是 LazyThreadSafetyMode.SYNCHRONIZED，可以省略不写，如下所示
        // 这就可以理解为什么 by lazy 声明的变量只能用 val，因为初始化完成之后它的值是不会变的。
        val INSTANCE2 by lazy { HappySingleton() }
    }
}

inline fun <reified T> Gson.fromJson(json: String) =
    fromJson(json, T::class.java)