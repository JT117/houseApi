package io.swtf.jt.dto

class GraphData {

    val data: MutableMap<String, MutableList<GraphDataEntry>> = mutableMapOf()

    override fun toString(): String {
        return "GraphData(data=$data)"
    }
}