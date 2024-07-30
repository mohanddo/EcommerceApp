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
import com.example.e_commerce.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth

        val signUp = binding.signup
        signUp.setOnClickListener {
            val password = binding.password.text.toString()
            val email = binding.email.text.toString()
            val fullName = binding.fullName.text.toString()
            signUp(email, password)
        }

        val signin = binding.signin
        val mSpannableString = SpannableString("Sign In")
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
        signin.text = mSpannableString

        signin.setOnClickListener {
            val i = Intent(this, SignInActivity::class.java)
            startActivity(i)
        }


    }



    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
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
                        "Authentication Failed.",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
    }
}