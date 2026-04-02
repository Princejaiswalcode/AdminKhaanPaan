package com.example.adminAppKhanPaan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminAppKhanPaan.databinding.ActivityCreatenewadminBinding
import com.example.adminAppKhanPaan.model.AdminModel
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateNewAdminActivity : AppCompatActivity() {

    private val binding: ActivityCreatenewadminBinding by lazy {
        ActivityCreatenewadminBinding.inflate(layoutInflater)
    }
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backbutton.setOnClickListener { finish() }

        binding.Createnewadmin.setOnClickListener {
            val name     = binding.name.text.toString().trim()
            val email    = binding.emailOrPhone.text.toString().trim()
            val password = binding.password.text.toString().trim()

            // ── Validate fields ────────────────────────────────────────────
            when {
                name.isBlank()     -> binding.name.error = "Name is required"
                email.isBlank()    -> binding.emailOrPhone.error = "Email is required"
                password.isBlank() -> binding.password.error = "Password is required"
                password.length < 6 -> binding.password.error = "Password must be at least 6 characters"
                else -> createAdmin(name, email, password)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createAdmin(name: String, email: String, password: String) {
        // ── Disable button while creating ──────────────────────────────────
        binding.Createnewadmin.isEnabled = false
        binding.Createnewadmin.text = "Creating..."

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    // ── Save new admin to Supabase ─────────────────────────
                    saveAdminToSupabase(userId, name, email)
                } else {
                    binding.Createnewadmin.isEnabled = true
                    binding.Createnewadmin.text = "Create New Admin"
                    Toast.makeText(
                        this,
                        "Failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun saveAdminToSupabase(userId: String, name: String, email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseClient.client.postgrest
                    .from("admin_users")
                    .upsert(
                        AdminModel(
                            id    = userId,
                            name  = name,
                            email = email
                        )
                    )

                withContext(Dispatchers.Main) {
                    binding.Createnewadmin.isEnabled = true
                    binding.Createnewadmin.text = "Create New Admin"

                    // ── Clear fields after success ─────────────────────────
                    binding.name.text?.clear()
                    binding.emailOrPhone.text?.clear()
                    binding.password.text?.clear()

                    Toast.makeText(
                        this@CreateNewAdminActivity,
                        "Admin '$name' created successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.Createnewadmin.isEnabled = true
                    binding.Createnewadmin.text = "Create New Admin"
                    Toast.makeText(
                        this@CreateNewAdminActivity,
                        "Failed to save admin: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}