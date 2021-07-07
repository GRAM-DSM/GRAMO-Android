package com.gram.gramoproject.model

data class HomeworkContentResponseData(
    val description: String,
    val endDate: String,
    val isMine: Boolean,
    val isSubmitted: Boolean,
    val major: String,
    val startDate: String,
    val teacherName: String,
    val title: String
)