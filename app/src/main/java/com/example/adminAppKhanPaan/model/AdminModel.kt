package com.example.adminAppKhanPaan.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminModel(
    @SerialName("id")      val id: String? = null,
    @SerialName("name")    val name: String? = null,
    @SerialName("email")   val email: String? = null,
    @SerialName("phone")   val phone: String? = null,
    @SerialName("address") val address: String? = null
)