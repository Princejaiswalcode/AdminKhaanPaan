// model/AllMenu.kt
package com.example.adminAppKhanPaan.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class AllMenu(
    @SerialName("id")               val id: String? = null,
    @SerialName("food_name")        val foodName: String = "",
    @SerialName("food_price")       val foodPrice: String = "",
    @SerialName("food_description") val foodDescription: String = "",
    @SerialName("food_image_url")       val foodImage: String? = null,  // ← nullable
    @SerialName("food_ingredient")  val foodIngredient: String = ""
)