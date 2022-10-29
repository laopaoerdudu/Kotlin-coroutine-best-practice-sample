package com.wwe.delegate

import kotlin.reflect.KProperty

/**
 * 当我们定义委托类时并不是一定要实现 Kotlin 提供的 ReadWriteProperty 和 ReadOnlyProperty 接口
 * 实际上，只要保持委托类里的 setValue 和 getValue 方法签名与约定的一致就可以了。
 */
class DelegateMax {
    var result = ""

    // 注意这里的 operator 不可以省略
    operator fun getValue(thisRef: Any, property: KProperty<*>): String {
        return result + "-" + result.length
    }
    // 注意这里的 operator 不可以省略
    operator fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        if (thisRef is PersonMax) {
            thisRef.updateCount++
        }
        result = value.lowercase().replaceFirstChar { it.uppercase() }
    }
}