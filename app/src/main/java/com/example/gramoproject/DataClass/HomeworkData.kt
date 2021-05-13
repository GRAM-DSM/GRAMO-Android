package com.example.gramoproject.DataClass

data class HomeworkData (
    val HomeworkListResponse: List<HomeworkResponse>
)

data class HomeworkContentData (
    val HomeworkContent: HomeworkContentResponseData
)

data class TokenRefresh (
    val access_token: String
)

