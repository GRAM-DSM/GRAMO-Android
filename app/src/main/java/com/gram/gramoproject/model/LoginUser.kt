package com.gram.gramoproject.model

data class LoginUser(
        val name: String,
        val major: String,
        val access_token: String,
        val refresh_token: String
)
