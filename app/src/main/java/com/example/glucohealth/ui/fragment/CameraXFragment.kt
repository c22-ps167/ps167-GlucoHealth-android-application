package com.example.glucohealth.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.glucohealth.databinding.FragmentCameraXBinding


class CameraXFragment : Fragment() {

    private var _binding: FragmentCameraXBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraXBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)?.supportActionBar?.run {
            hide()
            setShowHideAnimationEnabled(false)
        }

        binding.tvProduct.isSelected = true
        return binding.root
    }

}