package com.example.adminAppKhanPaan.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminModel(
    @SerialName("id")              val id: String? = null,
    @SerialName("name")            val name: String? = null,
    @SerialName("email")           val email: String? = null,
    @SerialName("phone")           val phone: String? = null,
    @SerialName("address")         val address: String? = null,
    @SerialName("restaurant_name") val restaurantName: String? = null,
    @SerialName("location")        val location: String? = null,
    @SerialName("role")            val role: String? = "admin"
)