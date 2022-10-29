package com.wwe.lazy

class Student(name: String) {
    val emails: List<String> by lazy { loadEmailsByName(name) }

    private fun loadEmailsByName(name: String): List<String> {
        println("loadEmailsByName called")
        return listOf("Email1", "Email2", "Email3")
    }
}