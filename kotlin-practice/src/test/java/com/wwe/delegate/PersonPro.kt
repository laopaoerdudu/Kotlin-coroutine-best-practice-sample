package com.wwe.delegate

class PersonPro {
    var updateCount = 0
    private val nameDelegate = Delegate()
    private val lastnameDelegate = Delegate()

    var name: String
        set(value) {
            nameDelegate.setValue(this, value)
        }
        get() {
            return nameDelegate.getValue()
        }

    var lastname: String
        set(value) {
            lastnameDelegate.setValue(this, value)
        }
        get() {
            return lastnameDelegate.getValue()
        }
}