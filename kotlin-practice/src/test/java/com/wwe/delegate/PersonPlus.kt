package com.wwe.delegate

class PersonPlus {
    var updateCount = 0
    var name: String by DelegatePlus()
    var lastname: String by DelegatePlus()
}