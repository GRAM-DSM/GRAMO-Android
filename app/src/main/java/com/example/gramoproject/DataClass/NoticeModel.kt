package com.example.gramoproject.DataClass

data class Notice(val notice: ArrayList<GetNotice>) {
        data class GetNotice(
                val id: Int,
                val name: String,
                val title: String,
                val content: String,
                val user_name: String,
                val created_at: String
        )
}