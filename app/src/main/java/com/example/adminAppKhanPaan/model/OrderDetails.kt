package com.example.adminAppKhanPaan.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDetails(
    @SerialName("id")               val id: String? = null,
    @SerialName("user_id")          val userId: String? = null,
    @SerialName("user_name")        val userName: String? = null,
    @SerialName("food_names")       val foodNames: String? = null,
    @SerialName("food_prices")      val foodPrices: String? = null,
    @SerialName("food_images")      val foodImages: String? = null,
    @SerialName("food_quantities")  val foodQuantities: String? = null,
    @SerialName("address")          val address: String? = null,
    @SerialName("phone")            val phone: String? = null,
    @SerialName("total_amount")     val totalAmount: String? = null,
    @SerialName("order_accepted")   val orderAccepted: Boolean? = false,
    @SerialName("payment_received") val paymentReceived: Boolean? = false,
    @SerialName("created_at")       val createdAt: String? = null
)