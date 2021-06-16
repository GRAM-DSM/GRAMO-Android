package com.example.gramoproject.model

data class PicuList(val picuContentResponses: ArrayList<Picu?>) {
    data class Picu(
        val picuId: Int,
        val userName: String,
        val description: String
    )
}
