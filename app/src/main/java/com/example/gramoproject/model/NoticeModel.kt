package com.example.gramoproject.model

data class NoticeList(val notice: ArrayList<GetNotice?>, val next_page: Boolean) {
        data class GetNotice(
                val id: Int,
                val title: String,
                val content: String,
                val user_name: String,
                val created_at: String
        )
}