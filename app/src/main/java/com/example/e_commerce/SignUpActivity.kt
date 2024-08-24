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
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.e_commerce.admin.ProductsAdminActivity
import com.example.e_commerce.databinding.ActivitySignUpBinding
import com.example.e_commerce.models.User
import com.example.e_commerce.user.UserActivity
import com.example.e_commerce.utils.FirebaseUtil
import com.example.e_commerce.utils.FirebaseUtil.usersRef
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private lateinit var progressBar: ProgressBar
    private lateinit var signUpBtn: Button
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

        progressBar = binding.progressBar

        signUpBtn = binding.signup
        val passwordEditText = binding.password
        val emailEditText = binding.email
        val fullNameEditText = binding.fullName
        val phoneNumberEditText = binding.phoneNumber
        signUpBtn.setOnClickListener {

            if(!FirebaseUtil.isEditTextEmpty(fullNameEditText) && !FirebaseUtil.isEditTextEmpty(phoneNumberEditText) && !FirebaseUtil.isEditTextEmpty(emailEditText) && FirebaseUtil.isValidPassword(passwordEditText)) {
                signUp(emailEditText,
                    phoneNumberEditText,
                    passwordEditText,
                    fullNameEditText)
            }

        }

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


        val signin = binding.signin
        val mSpannableString = SpannableString("Sign In")
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
        signin.text = mSpannableString

        signin.setOnClickListener {
            val i = Intent(this, SignInActivity::class.java)
            startActivity(i)
            finish()
        }


    }



    private fun signUp(emailEditText: EditText, phoneNumberEditText: EditText, passwordEditText: EditText, fullNameEditText: EditText) {

        progressBar.visibility = View.VISIBLE
        signUpBtn.isEnabled = false

        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val fullName = fullNameEditText.text.toString().trim()
        val phoneNumber = phoneNumberEditText.text.toString().trim()

        FirebaseUtil.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User created successfully!", Toast.LENGTH_SHORT).show()
                    writeUserToDB(email, phoneNumber, fullName)
                    fetchUserData()
                } else {
                    val exceptionMessage = when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> "Invalid email format."
                        is FirebaseAuthUserCollisionException -> "Email is already in use."
                        is FirebaseNetworkException -> "Network error. Please try again."
                        is FirebaseTooManyRequestsException -> "Too many requests. Please try again later."
                        else -> "User creation failed: ${task.exception?.message}"
                    }
                    Toast.makeText(this, exceptionMessage, Toast.LENGTH_LONG).show()
                    Log.e("Error", task.exception!!.message!!)
                    signUpBtn.isEnabled = true
                }
                progressBar.visibility = View.GONE
            }

    }

    private fun writeUserToDB(email: String, phoneNumber: String, fullName: String) {
        val user = User(fullName, phoneNumber, email, mutableListOf())
        val userRef = FirebaseUtil.usersRef.child(FirebaseUtil.auth.currentUser!!.uid)
        userRef.setValue(user)
    }

    private fun fetchUserData() {
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(User::class.java)?.let {
                    FirebaseUtil.user = it
                    val i = Intent(this@SignUpActivity, UserActivity::class.java)
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