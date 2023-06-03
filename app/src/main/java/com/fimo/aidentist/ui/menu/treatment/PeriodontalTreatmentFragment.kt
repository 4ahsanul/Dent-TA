package com.fimo.aidentist.ui.menu.treatment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fimo.aidentist.databinding.FragmentPeriodontalTreatmentBinding

class PeriodontalTreatmentFragment : Fragment() {
    private var _binding: FragmentPeriodontalTreatmentBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPeriodontalTreatmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

}