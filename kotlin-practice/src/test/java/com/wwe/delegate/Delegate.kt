package com.wwe.delegate

class Delegate {
    var result: String = ""

    fun getValue(): String {
        return result + "-" + result.length
    }

    fun setValue(thisRef: Any, value: String) {
        if (thisRef is Person) {
            thisRef.updateCount++
        }
        result = value.lowercase().replaceFirstChar { it.uppercase() }
    }
}