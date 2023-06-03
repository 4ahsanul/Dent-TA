package com.fimo.aidentist.ui.menu.treatment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fimo.aidentist.R
import com.fimo.aidentist.databinding.FragmentDiscolorationTreatmentBinding
import com.fimo.aidentist.databinding.FragmentPeriodontalTreatmentBinding

class DiscolorationTreatmentFragment : Fragment() {
    private var _binding: FragmentDiscolorationTreatmentBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDiscolorationTreatmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

}