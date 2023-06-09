package com.example.traditionfabric.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.traditionfabric.R
import com.example.traditionfabric.TfLite.TensorFlow
import com.example.traditionfabric.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // pemangglan btnCamera
        val cameraButton: Button = root.findViewById(R.id.cameraBtn)

        // pemanggilan search di dalam home
        searchView = root.findViewById(R.id.searchView)

        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it

            // menghubungkan button camera ke halam tensorflow
            cameraButton.setOnClickListener {
                val intent = Intent(requireContext(), TensorFlow::class.java)
                startActivity(intent)
            }

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}