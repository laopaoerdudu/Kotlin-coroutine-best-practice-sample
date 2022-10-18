package com.wwe

import org.junit.Test
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

class Day3Test {

    @Test
    fun test() {
        val property = User::class.declaredMemberProperties.find { it.name == "David" }
        property?.call(User::class.createInstance())
        User::class
        Class.forName("com.wwe.User").kotlin
        User::class.primaryConstructor?.call()
        val method = User::class.declaredFunctions.find { it.name == "David" }
        method?.call(User::class.createInstance())
    }

    @Test
    fun test1() {
        val method = Person::class.declaredFunctions.find { it.name == "getName" }

        assert(method?.isAccessible == false)
    }

    @Test
    fun test2() {
        val method = Person::class.declaredFunctions.find { it.name == "getAddress" }

        assert(method?.isAccessible == false)
    }

    @Test
    fun test4() {
        var data: String? = null
        data = ""
        data?.let {
            println("let")
        } ?: run {
            println("run")
        }
    }
}