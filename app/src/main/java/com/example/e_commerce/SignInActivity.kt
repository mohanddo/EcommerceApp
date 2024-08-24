package com.example.e_commerce

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.e_commerce.admin.ProductsAdminActivity
import com.example.e_commerce.databinding.ActivitySignInBinding
import com.example.e_commerce.models.User
import com.example.e_commerce.user.UserActivity
import com.example.e_commerce.utils.FirebaseUtil
import com.example.e_commerce.utils.FirebaseUtil.usersRef
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    lateinit var binding : ActivitySignInBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var signin: Button
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

        progressBar = binding.progressBar

        //add to fixe the password

        // When the EditText gains focus, clear the hint
        binding.password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.passwordLayout.hint = ""
            } else {
                // Restore hint if EditText loses focus and is empty
                if (binding.password.text.isNullOrEmpty()) {
                    binding.passwordLayout.hint = getString(R.string.password)
                }
            }
        }

        binding.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Clear the hint when the user starts typing
                if (!s.isNullOrEmpty()) {
                    binding.passwordLayout.hint = ""
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Restore the hint if the EditText is empty after typing
                if (s.isNullOrEmpty()) {
                    binding.passwordLayout.hint = getString(R.string.password)
                }
            }
        })


        //this is the end of the add to fixe password

        signin = binding.signin
        signin.setOnClickListener {
            val email = binding.email
            val password = binding.password
            if(!FirebaseUtil.isEditTextEmpty(email) && !FirebaseUtil.isEditTextEmpty(password)) {
                signin(email.text.toString(),
                    password.text.toString())
            }

        }

        val signup = binding.signup
        val signUpSpannableString = SpannableString("Sign Up")
        signUpSpannableString.setSpan(UnderlineSpan(), 0, signUpSpannableString.length, 0)
        signup.text = signUpSpannableString
        signup.setOnClickListener {
            val i = Intent(this, SignUpActivity::class.java)
            startActivity(i)
            finish()
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

        progressBar.visibility = View.VISIBLE
        signin.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    fetchUserData()
                } else {
                    val exceptionMessage = when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException, is
                        FirebaseAuthInvalidUserException -> "Wrong email or password."
                        is FirebaseNetworkException -> "Network error. Please try again."
                        is FirebaseTooManyRequestsException -> "Too many requests. Please try again later."
                        is FirebaseAuthRecentLoginRequiredException -> "Recent login required. Please log in again."
                        else -> "Sign-in failed: ${task.exception?.message}"
                    }
                    Toast.makeText(this, exceptionMessage, Toast.LENGTH_LONG).show()
                    signin.isEnabled = true
                }
                progressBar.visibility = View.GONE
            }
    }

    private fun fetchUserData() {
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(User::class.java)?.let {
                    FirebaseUtil.user = it
                    val i = Intent(this@SignInActivity, UserActivity::class.java)
                    startActivity(i)
                    finish()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException())
            }
        }

        usersRef.child(FirebaseUtil.auth.uid!!).addListenerForSingleValueEvent(userListener)
    }

}