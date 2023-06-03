package com.fimo.aidentist.ui.menu.treatment

import android.content.ContentValues
import android.content.DialogInterface
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
import com.fimo.aidentist.databinding.FragmentBlankTreatmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BlankTreatmentFragment : Fragment(), DialogInterface.OnClickListener {
    private var _binding: FragmentBlankTreatmentBinding? = null
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
        _binding = FragmentBlankTreatmentBinding.inflate(inflater, container, false)
        val view = binding.root
        val user = FirebaseAuth.getInstance().currentUser
//        val docRef = db.collection("users").document(user!!.uid)
//        docRef.get()
//            .addOnSuccessListener { document ->
//                if (document.data?.get("disease") != null) {
//                    checkDisease()
//                } else {
//                    Log.d(ContentValues.TAG, "No such document for the current user")
//                    replaceFragment(ScanTreatmentFragment())
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d(ContentValues.TAG, "get failed with", exception)
//                replaceFragment(ScanTreatmentFragment())
//            }

//        val docRef = db.collection("users").document("user")
//        docRef.get()
//            .addOnSuccessListener { document ->
//                if (document.data?.get("disease") != null) {
//                    checkDisease()
//
//                } else {
//                    Log.d(ContentValues.TAG, "No such document")
//                    replaceFragment(ScanTreatmentFragment())
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d(ContentValues.TAG, "get failed with ", exception)
//                replaceFragment(ScanTreatmentFragment())
//            }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val docRef = db.collection("users").document(currentUser.uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.data?.get("disease") != null) {
                        checkDisease()
                    } else {
                        Log.d(ContentValues.TAG, "No such document")
                        replaceFragment(ScanTreatmentFragment())
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "get failed with", exception)
                    replaceFragment(ScanTreatmentFragment())
                }
        } else {
            Log.w(ContentValues.TAG, "No user is currently signed in.")
        }
        return view
    }


    private fun replaceFragment(fragment: Fragment) {
        val nav = parentFragmentManager
        val trans = nav.beginTransaction()
        trans.replace(R.id.containerTreatment, fragment)
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
                replaceFragment(HealthyTreatmentFragment())
            }
            "Dental Discoloration" -> {
                replaceFragment(DiscolorationTreatmentFragment())
            }
            "Periodontal Disease" -> {
                replaceFragment(PeriodontalTreatmentFragment())
            }
            else -> {
                replaceFragment(ScanTreatmentFragment())
            }
        }
    }

//    private fun checkDisease() {
//        val docRef = db.collection("users").document("user")
//        docRef.get()
//            .addOnSuccessListener { document ->
//                if (document.data?.get("disease") != "null") {
//
//                    when {
//                        document.data?.get("disease") == "Healthy" -> {
//                            replaceFragment(TreatmentFragment())
//                        }
//                        document.data?.get("disease") == "Dental Discoloration" -> {
//                            replaceFragment(TreatmentFragment())
//                        }
//                        document.data?.get("disease") == "Periodontal Disease" -> {
//                            replaceFragment(TreatmentFragment())
//                        }
//                    }
//
//                } else {
//                    Log.d(ContentValues.TAG, "Check")
//
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d(ContentValues.TAG, "get failed with ", exception)
//
//            }
//    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {

    }
}