package com.example.gramoproject.dataclass

data class HomeworkModel(
    var homeworkID : Int = 0,
    var major : String = "",
    var endDate : String = "",
    var studentEmail : String = "",
    var description : String = "",
    var title : String = ""
)