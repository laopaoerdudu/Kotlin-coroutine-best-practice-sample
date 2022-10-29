package com.wwe.delegate

class PersonMax {
    var name: String by DelegateMax()
    var lastname: String by DelegateMax()
    var updateCount = 0
}