package io.swtf.jt.dto

import io.swtf.jt.enums.Resource

data class GraphDataEntry(val resource: Resource, var amount: Long, var price: Long) {

    fun add(amount: Long, price: Long) {
        this.amount += amount
        this.price += price
    }
}