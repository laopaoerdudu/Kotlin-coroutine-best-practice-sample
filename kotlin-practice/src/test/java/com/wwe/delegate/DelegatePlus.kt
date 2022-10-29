package com.wwe.delegate

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// Kotlin 提供了 ReadWriteProperty 和 ReadOnlyProperty 封装了约定的方法给我们使用
class DelegatePlus : ReadWriteProperty<Any, String> {
    var result = ""

    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return result + "-" + result.length
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        if (thisRef is PersonPlus) {
            thisRef.updateCount++
        }
        result = value.lowercase().replaceFirstChar { it.uppercase() }
    }
}