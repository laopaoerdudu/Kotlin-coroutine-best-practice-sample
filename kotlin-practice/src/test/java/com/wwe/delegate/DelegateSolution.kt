package com.wwe.delegate

class DelegateSolution<T>(
    val innerSet: MutableSet<T> = HashSet<T>()
) : MutableSet<T> by innerSet {
    var objectAdded = 0
    override fun add(element: T): Boolean {
        objectAdded++
        return innerSet.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        objectAdded += elements.size
        return innerSet.addAll(elements)
    }
}