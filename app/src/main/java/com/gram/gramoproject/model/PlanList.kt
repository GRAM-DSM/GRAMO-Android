package com.gram.gramoproject.model

data class PlanList(val planContentResponses: ArrayList<Plan?>) {
    data class Plan(val planId: Int,
                    val title: String,
                    val description: String)
}
