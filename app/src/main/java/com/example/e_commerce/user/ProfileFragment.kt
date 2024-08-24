package com.example.e_commerce.user

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.EditAccountActivity
import com.example.e_commerce.R
import com.example.e_commerce.WelcomeActivity
import com.example.e_commerce.adapters.ProductAdapter
import com.example.e_commerce.databinding.FragmentHomeBinding
import com.example.e_commerce.databinding.FragmentProfileBinding
import com.example.e_commerce.utils.FirebaseUtil
import com.example.e_commerce.utils.FirebaseUtil.products
import com.example.e_commerce.utils.FirebaseUtil.user
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        val bundle = arguments
        val name = bundle?.getString("FullName") ?: user!!.name
        binding.fullName.text = name
        binding.email.text = user!!.email
        binding.phoneNumber.text = user!!.phoneNumber
        bundle?.getString("NewProfilePicUri")?.let {
            Picasso.get().load(it).into(binding.profilePic)
        }


        binding.EditBtn.setOnClickListener {
            val i = Intent(requireContext(), EditAccountActivity::class.java)
            i.putExtra("FullName", binding.fullName.text)
            startActivity(i)
        }

        binding.logout.setOnClickListener {
            FirebaseUtil.auth.signOut()
            startActivity(Intent(requireContext(), WelcomeActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}