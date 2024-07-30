package com.example.e_commerce

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.e_commerce.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    lateinit var binding : ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth

        val signin = binding.signin
        signin.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            signin(email, password)
        }

        val signup = binding.signup
        val signUpSpannableString = SpannableString("Sign Up")
        signUpSpannableString.setSpan(UnderlineSpan(), 0, signUpSpannableString.length, 0)
        signup.text = signUpSpannableString
        signup.setOnClickListener {
            val i = Intent(this, SignUpActivity::class.java)
            startActivity(i)
        }

        val resetPassword = binding.resetPassword
        val resetPasswordSpannableString = SpannableString("Forgotten your password?")
        resetPasswordSpannableString.setSpan(UnderlineSpan(), 0, resetPasswordSpannableString.length, 0)
        resetPassword.text = resetPasswordSpannableString
        resetPassword.setOnClickListener {
            val i = Intent(this, ResetPasswordActivity::class.java)
            startActivity(i)
        }
    }

    private fun signin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Toast.makeText(
                        baseContext,
                        "Authentication successed",
                        Toast.LENGTH_LONG,
                    ).show()
                } else {
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }
            }
    }

}