package com.fimo.aidentist.ui.navigation.home

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fimo.aidentist.R
import com.fimo.aidentist.data.viewmodel.DiseaseViewModel
import com.fimo.aidentist.databinding.FragmentHomeBinding
import com.fimo.aidentist.ui.analisis.*
import com.fimo.aidentist.ui.menu.treatment.DailyTreatmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class HomeFragment : Fragment(), DialogInterface.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var db = Firebase.firestore
    private lateinit var fAuth: FirebaseAuth
    private lateinit var viewModel: DiseaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DiseaseViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        fAuth = Firebase.auth

        binding.dailyTreatment.setOnClickListener {
            val intent = Intent(activity, DailyTreatmentActivity::class.java)
            activity?.startActivity(intent)
        }

        replaceFragment(ShimmerFragment())
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val docRef = db.collection("users").document(currentUser.uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.data?.get("disease") != null) {
                        checkDisease()
                    } else {
                        Log.d(ContentValues.TAG, "No such document")
                        replaceFragment(BlankAnalisisFragment())
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "get failed with ", exception)
                    replaceFragment(BlankAnalisisFragment())
                }
        } else {
            // Handle the case where the user is not logged in
            Log.w(ContentValues.TAG, "No user is currently signed in.")
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = fAuth.currentUser

        if (user != null) {
            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(binding.userProfile)
            } else {
                Picasso.get()
                    .load("https://raw.githubusercontent.com/4ahsanul/SignUpSignIn/main/images/ligin%20and%20reg%20purple/5907.jpg")
                    .into(binding.userProfile)
            }

            binding.userNameToolbar.text = user.displayName
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        val trans = parentFragmentManager.beginTransaction()
        trans.replace(R.id.infoData, fragment)
        trans.commit()
    }


    private fun checkDisease() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            viewModel.getDisease(FirebaseAuth.getInstance().currentUser?.uid ?: "")
                .observe(viewLifecycleOwner, Observer { disease ->
                    handleDiseaseChange(disease)
                })
        } else {
            // Handle the case where the user is not logged in
            Log.w(ContentValues.TAG, "No user is currently signed in.")
        }
    }

    private fun handleDiseaseChange(disease: String) {
        when (disease) {
            "Healthy" -> {
                replaceFragment(AnalisisFragment3())
            }
            "Dental Discoloration" -> {
                replaceFragment(AnalisisFragment2())
            }
            "Periodontal Disease" -> {
                replaceFragment(AnalisisFragment())
            }
            else -> {
                replaceFragment(BlankAnalisisFragment())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {

    }
}