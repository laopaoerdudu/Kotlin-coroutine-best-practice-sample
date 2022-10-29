package com.wwe.delegate

class Person {
    var updateCount = 0

    var name: String = ""
        set(value) {
            field = format(value)
        }
        get() {
            return getter(field)
        }

    var lastname: String = ""
        set(value) {
            field = format(value)
        }
        get() {
            return getter(field)
        }

    private fun getter(value: String): String {
        return value + "-" + value.length
    }

    private fun format(value: String): String {
        updateCount++
        return value.lowercase().replaceFirstChar { it.uppercase() }
    }
}