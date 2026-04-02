package com.example.adminAppKhanPaan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminAppKhanPaan.databinding.ActivityProfileBinding
import com.example.adminAppKhanPaan.model.AdminModel
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class profileActivity : AppCompatActivity() {

    private val binding: ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backbutton.setOnClickListener { finish() }

        // ── Load existing profile data ─────────────────────────────────────
        loadProfileData()

        // ── Save info button ───────────────────────────────────────────────
        binding.saveinfo.setOnClickListener {
            saveProfileData()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadProfileData() {
        val userId = auth.currentUser?.uid ?: return

        // ── Show Firebase email immediately ────────────────────────────────
        binding.adminEmail.setText(auth.currentUser?.email ?: "")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = SupabaseClient.client.postgrest
                    .from("admin_users")
                    .select {
                        filter { eq("id", userId) }
                    }
                    .decodeList<AdminModel>()

                withContext(Dispatchers.Main) {
                    if (result.isNotEmpty()) {
                        val admin = result.first()
                        if (!admin.name.isNullOrBlank())    binding.Name.setText(admin.name)
                        if (!admin.email.isNullOrBlank())   binding.adminEmail.setText(admin.email)
                        if (!admin.phone.isNullOrBlank())   binding.Phone.setText(admin.phone)
                        if (!admin.address.isNullOrBlank()) binding.Address.setText(admin.address)
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@profileActivity,
                        "Failed to load profile: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun saveProfileData() {
        val userId = auth.currentUser?.uid ?: return

        val name    = binding.Name.text.toString().trim()
        val email   = binding.adminEmail.text.toString().trim()
        val phone   = binding.Phone.text.toString().trim()
        val address = binding.Address.text.toString().trim()

        if (name.isBlank()) {
            binding.Name.error = "Name is required"
            return
        }

        // ── Disable button while saving ────────────────────────────────────
        binding.saveinfo.isEnabled = false
        binding.saveinfo.text = "Saving..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseClient.client.postgrest
                    .from("admin_users")
                    .upsert(
                        AdminModel(
                            id      = userId,
                            name    = name,
                            email   = email,
                            phone   = phone,
                            address = address
                        )
                    )

                withContext(Dispatchers.Main) {
                    binding.saveinfo.isEnabled = true
                    binding.saveinfo.text = "Save info"
                    Toast.makeText(
                        this@profileActivity,
                        "Profile saved!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.saveinfo.isEnabled = true
                    binding.saveinfo.text = "Save info"
                    Toast.makeText(
                        this@profileActivity,
                        "Failed to save: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}