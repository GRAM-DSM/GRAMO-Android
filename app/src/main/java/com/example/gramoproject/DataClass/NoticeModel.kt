package com.example.gramoproject.DataClass

data class NoticeList(val notice: ArrayList<GetNotice?>) {
        data class GetNotice(
                val id: Int,
                val title: String,
                val content: String,
                val user_name: String,
                val created_at: String
        )
}