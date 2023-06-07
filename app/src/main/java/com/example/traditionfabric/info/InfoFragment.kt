package com.example.trafa.ui.info

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.trafa.R

class InfoFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = InfoFragment()
    }

    private lateinit var viewModel: InfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)

        val btnTentang: Button = view.findViewById(R.id.btn_tentang)
        btnTentang.setOnClickListener(this)

        val btnDisclaimer: Button = view.findViewById(R.id.btn_disclaimer)
        btnDisclaimer.setOnClickListener(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(InfoViewModel::class.java)
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when(p0.id){
                R.id.btn_tentang -> {
                    val moveIntent = Intent(requireActivity(), InfoActivity::class.java)
                    startActivity(moveIntent)
                }
                R.id.btn_disclaimer -> {
                    val moveIntent = Intent(requireContext(),DisclaimerActivity::class.java)
                    startActivity(moveIntent)
                }
            }
        }
    }

}