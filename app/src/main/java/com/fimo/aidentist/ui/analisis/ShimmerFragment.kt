package com.fimo.aidentist.ui.analisis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.shimmer.ShimmerFrameLayout
import com.fimo.aidentist.R


class ShimmerFragment : Fragment() {

    private var mShimmerFrameLayout: ShimmerFrameLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_shimmer, container, false)
        mShimmerFrameLayout = view.findViewById(R.id.shimmer_view_container)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mShimmerFrameLayout?.stopShimmer()
    }
}