package com.example.gramoproject.model

data class PicuList(val picuListResponse : ArrayList<Picu?>) {
    data class Picu(val userName : String,
                    val description : String)
}
