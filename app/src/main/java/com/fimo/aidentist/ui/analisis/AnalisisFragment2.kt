package com.fimo.aidentist.ui.analisis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fimo.aidentist.R
import com.fimo.aidentist.databinding.FragmentAnalisis2Binding
import com.fimo.aidentist.databinding.FragmentAnalisisBinding

class AnalisisFragment2 : Fragment() {
    private var _binding: FragmentAnalisis2Binding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAnalisis2Binding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

}