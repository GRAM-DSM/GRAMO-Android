package com.gram.gramoproject.model

import java.io.Serializable

data class HomeworkResponse(
    val description: String,
    val endDate: String,
    val homeworkId: Int,
    val isRejected: Boolean = false,
    val major: String,
    val studentName: String,
    val startDate: String,
    val teacherName: String,
    val title: String
) : Serializable