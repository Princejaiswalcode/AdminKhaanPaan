package com.example.adminAppKhanPaan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminAppKhanPaan.databinding.ActivityLoginBinding

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        auth = FirebaseAuth.getInstance()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.loginButton.setOnClickListener {
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                login(email, password)
            }
        }

        binding.googleButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }

        binding.donthaveaccountbutton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    updateUi(user)
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUi(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                if (task.isSuccessful) {
                    val account: GoogleSignInAccount = task.result

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    auth.signInWithCredential(credential)
                        .addOnCompleteListener { it ->
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Successful Login with Google",
                                    Toast.LENGTH_SHORT
                                ).show()

                                updateUi(auth.currentUser)
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    it.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
}