package com.example.adminAppKhanPaan

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminAppKhanPaan.databinding.ActivityAdditemBinding
import com.example.adminAppKhanPaan.model.AllMenu
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class additemActivity : AppCompatActivity() {

    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private var foodImageUri: Uri? = null
    private lateinit var foodIngredient: String

    private val binding: ActivityAdditemBinding by lazy {
        ActivityAdditemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.AddItemButton.setOnClickListener {
            foodName = binding.foodName.text.toString().trim()
            foodPrice = binding.foodPrice.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngredient = binding.ingredint.text.toString().trim()

            if (foodName.isNotBlank() && foodPrice.isNotBlank() &&
                foodDescription.isNotBlank() && foodIngredient.isNotBlank()
            ) {
                uploadData()
            } else {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.selectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.backbutton.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun uploadData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var imageUrl: String? = null

                // ── 1. Upload image to Supabase Storage (if selected) ──────────
                if (foodImageUri != null) {
                    val imageBytes = contentResolver
                        .openInputStream(foodImageUri!!)
                        ?.readBytes()
                        ?: throw Exception("Could not read image")

                    val fileName = "menu_${System.currentTimeMillis()}.jpg"

                    val bucket = SupabaseClient.client.storage.from("menu-images")
                    bucket.upload(fileName, imageBytes) {
                        upsert = false
                    }

                    // Get the public URL of the uploaded image
                    imageUrl = bucket.publicUrl(fileName)
                }

                // ── 2. Insert menu item into Supabase Database ─────────────────
                val newItem = AllMenu(
                    foodName       = foodName,
                    foodPrice      = foodPrice,
                    foodDescription = foodDescription,
                    foodImage      = imageUrl ?: "",
                    foodIngredient = foodIngredient
                )

                SupabaseClient.client.postgrest
                    .from("menu")
                    .insert(newItem)

                // ── 3. Success — back to main thread ───────────────────────────
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@additemActivity, "Item Added", Toast.LENGTH_SHORT).show()
                    finish()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@additemActivity,
                        "Failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                foodImageUri = uri
                binding.selectedImage.setImageURI(uri)
            }
        }
}