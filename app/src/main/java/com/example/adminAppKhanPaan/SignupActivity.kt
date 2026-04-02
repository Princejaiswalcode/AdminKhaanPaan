package com.example.adminAppKhanPaan

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminAppKhanPaan.databinding.ActivitySignupBinding
import com.example.adminAppKhanPaan.model.AdminModel
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val binding: ActivitySignupBinding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.createUserButton.setOnClickListener {
            val userName          = binding.name.text.toString().trim()
            val nameOfRestaurant  = binding.resturantName.text.toString().trim()
            val email             = binding.emailOrPhone.text.toString().trim()
            val password          = binding.password.text.toString().trim()
            val location          = binding.listoflocation.text.toString().trim()

            when {
                userName.isBlank()         -> binding.name.error = "Required"
                nameOfRestaurant.isBlank() -> binding.resturantName.error = "Required"
                email.isBlank()            -> binding.emailOrPhone.error = "Required"
                password.isBlank()         -> binding.password.error = "Required"
                password.length < 6        -> binding.password.error = "Min 6 characters"
                else -> createAccount(email, password, userName, nameOfRestaurant, location)
            }
        }

        binding.alreadyHaveAccountButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))  // ← fixed (was SignupActivity)
        }

        val listOfLocation = arrayOf("Jaipur", "Ujjain", "Indore", "Delhi", "Mumbai", "Agra")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            listOfLocation
        )
        binding.listoflocation.setAdapter(adapter)
    }

    private fun createAccount(
        email: String,
        password: String,
        userName: String,
        restaurantName: String,
        location: String
    ) {
        binding.createUserButton.isEnabled = false
        binding.createUserButton.text = "Creating..."

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    // ── Save admin to Supabase ─────────────────────────────
                    saveAdminToSupabase(userId, userName, email, restaurantName, location)
                } else {
                    binding.createUserButton.isEnabled = true
                    binding.createUserButton.text = "Create Account"
                    Toast.makeText(
                        this,
                        "Failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun saveAdminToSupabase(
        userId: String,
        name: String,
        email: String,
        restaurantName: String,
        location: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseClient.client.postgrest
                    .from("admin_users")
                    .upsert(
                        AdminModel(
                            id             = userId,
                            name           = name,
                            email          = email,
                            restaurantName = restaurantName,
                            location       = location,
                            role           = "admin"
                        )
                    )

                withContext(Dispatchers.Main) {
                    binding.createUserButton.isEnabled = true
                    binding.createUserButton.text = "Create Account"
                    Toast.makeText(
                        this@SignupActivity,
                        "Account created successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                    finish()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.createUserButton.isEnabled = true
                    binding.createUserButton.text = "Create Account"
                    Toast.makeText(
                        this@SignupActivity,
                        "Failed to save: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}