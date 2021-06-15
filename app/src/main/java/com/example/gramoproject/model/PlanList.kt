package com.example.gramoproject.model

data class PlanList(val planListResponse: ArrayList<Plan?>) {
    data class Plan(val planId: Int,
                    val title: String,
                    val description: String)
}
