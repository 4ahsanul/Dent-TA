package com.fimo.aidentist.ui.navigation.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fimo.aidentist.MainActivity
import com.fimo.aidentist.R
import com.fimo.aidentist.databinding.ActivityHistoryBinding
import com.fimo.aidentist.ui.adapter.ImageAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rvItem)
        recyclerView.layoutManager = LinearLayoutManager(this)
        displayAllImagesFromFirebaseStorage(recyclerView)
    }

    private fun displayAllImagesFromFirebaseStorage(recyclerView: RecyclerView) {
        val storageRef = Firebase.storage.reference
        val user = Firebase.auth.currentUser
        if (user != null) {
            val imagesRef = storageRef.child("users/${user.uid}/images")

            imagesRef.listAll().addOnSuccessListener { listResult ->
                if (listResult.items.isEmpty()) {
                    // If there are no images, show a "no data" view
                    recyclerView.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                } else {
                    val images = mutableListOf<Pair<String, String>>()

                    // Loop through all files in the images folder and add their download URLs to the list
                    for (item in listResult.items) {
                        binding.progressBar.visibility = View.VISIBLE
                        item.downloadUrl.addOnSuccessListener { uri ->
                            // Add a pair of the file name and its download URL to the list
                            images.add(Pair(item.name, uri.toString()))

                            // If we've added all images to the list, display them in the RecyclerView
                            if (images.size == listResult.items.size) {
                                // If there are images, display them in the RecyclerView
                                recyclerView.visibility = View.VISIBLE
                                binding.tvNoData.visibility = View.GONE
                                val adapter = ImageAdapter(images)
                                adapter.setOnItemClickListener { image ->
                                    // Handle item click here
                                    // Move to DetailHistoryActivity and pass the image name and download URL
                                    val intent = Intent(this@HistoryActivity, DetailHistoryActivity::class.java)
                                    intent.putExtra("name", image.first)
                                    intent.putExtra("url", image.second)
                                    startActivity(intent)
                                    Log.d("TAG", "Item clicked: $image")
                                }
                                recyclerView.adapter = adapter
                                recyclerView.layoutManager = LinearLayoutManager(this@HistoryActivity)
                                binding.progressBar.visibility = View.GONE
                            }
                        }.addOnFailureListener { exception ->
                            Log.e("TAG", "Error retrieving image URL: $exception")
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("TAG", "Error retrieving list of files: $exception")
                binding.progressBar.visibility = View.GONE
            }
        } else {
            Log.e("TAG", "No user signed in")
            binding.progressBar.visibility = View.GONE
        }
    }


//    private fun displayAllImagesFromFirebaseStorage(recyclerView: RecyclerView) {
//        val storageRef = Firebase.storage.reference
//        val user = Firebase.auth.currentUser
//        if (user != null) {
//            val imagesRef = storageRef.child("users/${user.uid}/images")
//
//            imagesRef.listAll().addOnSuccessListener { listResult ->
//                if (listResult.items.isEmpty()) {
//                    // If there are no images, show a "no data" view
//                    recyclerView.visibility = View.GONE
//                    binding.tvNoData.visibility = View.VISIBLE
//                } else {
//                    val images = mutableListOf<Pair<String, String>>()
//
//                    // Loop through all files in the images folder and add their download URLs to the list
//                    for (item in listResult.items) {
//                        binding.progressBar.visibility = View.VISIBLE
//                        item.downloadUrl.addOnSuccessListener { uri ->
//                            // Add a pair of the file name and its download URL to the list
//                            images.add(Pair(item.name, uri.toString()))
//
//                            // If we've added all images to the list, display them in the RecyclerView
//                            if (images.size == listResult.items.size) {
//                                // If there are images, display them in the RecyclerView
//                                recyclerView.visibility = View.VISIBLE
//                                binding.tvNoData.visibility = View.GONE
//                                val adapter = ImageAdapter(images)
//                                recyclerView.adapter = adapter
//                                recyclerView.layoutManager = LinearLayoutManager(this@HistoryActivity)
//                                binding.progressBar.visibility = View.GONE
//                            }
//                        }.addOnFailureListener { exception ->
//                            Log.e("TAG", "Error retrieving image URL: $exception")
//                            binding.progressBar.visibility = View.GONE
//                        }
//                    }
//                }
//            }.addOnFailureListener { exception ->
//                Log.e("TAG", "Error retrieving list of files: $exception")
//                binding.progressBar.visibility = View.GONE
//            }
//        } else {
//            Log.e("TAG", "No user signed in")
//            binding.progressBar.visibility = View.GONE
//        }
//    }

}