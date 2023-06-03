package com.fimo.aidentist.ui.navigation.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.fimo.aidentist.R
import com.fimo.aidentist.databinding.ActivityDetailHistoryBinding
import com.fimo.aidentist.ui.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class DetailHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailHistoryBinding
    private var currentDisease: String? = null
    private var currentTreatment: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.extras?.getString("name")
        val url = intent.extras?.getString("url")

        Glide.with(this)
            .load(url)
            .into(binding.imgDetailPhoto)

        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        binding.buttonDelete.setOnClickListener {
            val userId = Firebase.auth.currentUser?.uid
            val imageName = intent.getStringExtra("name")

            if (userId != null && imageName != null) {
                val imagePath = "users/$userId/images/$imageName"
                deleteImageFromFirebaseStorage(imagePath)
            }
        }

        binding.tvDetailTitle.text = name

        // Set up the ViewPager2
        val viewPagerAdapter = ViewPagerAdapter(this)
        val viewPager: ViewPager2 = binding.viewPager

        // Set ViewPager adapter
        viewPager.adapter = viewPagerAdapter

        //  Get TabLayout reference and set up the tabs
        val tabs: TabLayout = binding.tabs
        val tabTitle = resources.getStringArray(R.array.tab_title)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = tabTitle[position]
        }.attach()

        // Call handleDiseaseChange with the initial disease value
        currentDisease = name
        handleDiseaseChange(currentDisease)

        // Call handleTreatmentChange with the initial treatment value
        currentTreatment = name
        handleTreatmentChange(currentTreatment)
    }

    private fun deleteImageFromFirebaseStorage(imagePath: String) {
        val storageRef = FirebaseStorage.getInstance().getReference(imagePath)

        storageRef.delete()
            .addOnSuccessListener {
                // Delete operation successful
                Log.d("TAG", "Image deleted successfully")
                // Perform any additional actions after deleting the image, such as updating the UI or navigating back
                val intent = Intent(this, HistoryActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { exception ->
                // Error occurred during delete operation
                Log.e("TAG", "Error deleting image: $exception")
            }
    }

    private fun handleDiseaseChange(disease: String?) {
        val viewPagerAdapter = binding.viewPager.adapter as ViewPagerAdapter
        viewPagerAdapter.setDisease(disease)
    }

    private fun handleTreatmentChange(treatment: String?) {
        val viewPagerAdapter = binding.viewPager.adapter as ViewPagerAdapter
        viewPagerAdapter.setTreatment(treatment)
    }
}


//class DetailHistoryActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityDetailHistoryBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val name = intent.extras?.getString("name")
//        val url = intent.extras?.getString("url")
//
//        Glide.with(this)
//            .load(url)
//            .into(binding.imgDetailPhoto)
//
//        binding.tvDetailTitle.text = name
//
//        // Set up the ViewPager2
//        val viewPagerAdapter = ViewPagerAdapter(this)
//        val viewPager: ViewPager2 = binding.viewPager
//
//        // Set ViewPager adapter
//        viewPager.adapter = viewPagerAdapter
//
//        //  Get TabLayout reference and set up the tabs
//        val tabs: TabLayout = binding.tabs
//        val tabTitle = resources.getStringArray(R.array.tab_title)
//        TabLayoutMediator(tabs, viewPager) { tab, position ->
//            tab.text = tabTitle[position]
//        }.attach()
//    }
//}

