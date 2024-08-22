package com.example.e_commerce

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.e_commerce.databinding.ActivityEmailVerificationBinding
import com.example.e_commerce.utils.FirebaseUtil

class EmailVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailVerificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmailVerificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val signUpBtn = binding.GoToSignUpBtn
        signUpBtn.setOnClickListener {
            val i = Intent(this, SignUpActivity::class.java)
            startActivity(i)
            finish()
        }

        val textView = binding.textView
        val email = intent.getStringExtra("Email")
        val message = getString(R.string.verification_email_sent, email)
        textView.text = message

        val resendEmailBtn = binding.resendEmailBtn
        resendEmailBtn.setOnClickListener {
            FirebaseUtil.sendEmailVerification(this)
        }
    }
}