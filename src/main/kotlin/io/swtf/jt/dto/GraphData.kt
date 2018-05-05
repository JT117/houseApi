package io.swtf.jt.dto

import io.swtf.jt.enums.Resource

class GraphData {

    val data: MutableMap< Resource, MutableMap<String, MutableList<String>>> = mutableMapOf()

    init {
        Resource.values().forEach {
            data[it] = mutableMapOf()
        }
    }
}