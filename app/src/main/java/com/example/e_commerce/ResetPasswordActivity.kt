package com.example.e_commerce

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.e_commerce.databinding.ActivityResetPasswordBinding
import com.example.e_commerce.utils.FirebaseUtil
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding : ActivityResetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val resetPassword = binding.resetPassword
        resetPassword.setOnClickListener {
            val email = binding.email
            if(!FirebaseUtil.isEditTextEmpty(email)) {
                resetPassword(email.text.toString())
            }
        }

    }

    private fun resetPassword(email: String) {

        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext,
                        "Reset password email send to $email",
                        Toast.LENGTH_LONG,
                    ).show()

                    val i = Intent(this, SignInActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    val errorMessage = when (val exception = task.exception) {
                        is FirebaseNetworkException -> "Network error. Please try again."
                        else -> "Error: ${exception?.message}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }
}