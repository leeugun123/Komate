package org.techtown.kormate.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentMyBinding


class MyFragment : Fragment() {

    private var binding : FragmentMyBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentMyBinding.inflate(layoutInflater)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return binding?.root
    }

    override fun onDestroyView() {

        binding = null
        super.onDestroyView()

    }


}