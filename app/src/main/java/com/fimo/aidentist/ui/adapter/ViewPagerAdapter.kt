package com.fimo.aidentist.ui.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fimo.aidentist.ui.analisis.AnalisisFragment
import com.fimo.aidentist.ui.analisis.AnalisisFragment2
import com.fimo.aidentist.ui.analisis.AnalisisFragment3
import com.fimo.aidentist.ui.analisis.BlankAnalisisFragment
import com.fimo.aidentist.ui.menu.treatment.BlankTreatmentFragment
import com.fimo.aidentist.ui.menu.treatment.DiscolorationTreatmentFragment
import com.fimo.aidentist.ui.menu.treatment.HealthyTreatmentFragment
import com.fimo.aidentist.ui.menu.treatment.PeriodontalTreatmentFragment

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    private var disease: String? = null
    private var treatment: String? = null

    fun setDisease(disease: String?) {
        this.disease = disease
        notifyDataSetChanged()
    }

    fun setTreatment(treatment: String?) {
        this.treatment = treatment
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> if (disease?.contains("Periodontal Disease") == true) {
                AnalisisFragment()
            } else if (disease?.contains("Dental Discoloration") == true) {
                AnalisisFragment2()
            } else if (disease?.contains("Healthy") == true) {
                AnalisisFragment3()
            } else {
                BlankAnalisisFragment()
            }
            1 -> if (treatment?.contains("Periodontal Disease") == true) {
                PeriodontalTreatmentFragment()
            } else if (treatment?.contains("Dental Discoloration") == true) {
                DiscolorationTreatmentFragment()
            } else if (treatment?.contains("Healthy") == true) {
                HealthyTreatmentFragment()
            } else {
                BlankTreatmentFragment()
            }
            else -> {
                throw IllegalArgumentException("Invalid position $position")
            }
        }
    }
}

