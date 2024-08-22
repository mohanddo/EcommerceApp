package com.example.e_commerce

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.e_commerce.databinding.ActivityEditAccountBinding
import com.example.e_commerce.user.ProfileFragment
import com.example.e_commerce.user.UserActivity
import com.example.e_commerce.utils.FirebaseUtil

class EditAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val progressBar = binding.progressBar

        binding.CancelBtn.setOnClickListener {
            finish()
        }

        val fullNameEditText = binding.FullNameEditText
        fullNameEditText.setText(intent.getStringExtra("FullName")!!)

        val saveBtn = binding.SaveBtn
        saveBtn.setOnClickListener {
            if (!FirebaseUtil.isEditTextEmpty(fullNameEditText)) {
                saveBtn.isEnabled = false
                saveBtn.setTextColor(ContextCompat.getColor(this@EditAccountActivity, R.color.gray600))
                val intent = Intent(this, UserActivity::class.java)
                        intent.putExtra("NewName", fullNameEditText.text.toString())
                        startActivity(intent)
                        finish()
//                progressBar.visibility = View.VISIBLE
//                FirebaseUtil.usersRef.child(FirebaseUtil.auth.uid!!).child("name").setValue(fullNameEditText.text.toString()).addOnCompleteListener { task ->
//                    if(task.isSuccessful) {
//                        val intent = Intent(this, UserActivity::class.java)
//                        intent.putExtra("NewName", fullNameEditText.text.toString())
//                        startActivity(intent)
//                        finish()
//                    } else {
//
//                    }
//                    progressBar.visibility = View.GONE
//                }
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Optional: Not needed in this case
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Optional: Not needed in this case
            }

            override fun afterTextChanged(s: Editable?) {
                // Perform your action after the text has changed
                val newText = s.toString()

                saveBtn.isEnabled = true
                saveBtn.setTextColor(ContextCompat.getColor(this@EditAccountActivity, R.color.black))

                fullNameEditText.removeTextChangedListener(this)
            }
        }
        fullNameEditText.addTextChangedListener(textWatcher)

    }
}