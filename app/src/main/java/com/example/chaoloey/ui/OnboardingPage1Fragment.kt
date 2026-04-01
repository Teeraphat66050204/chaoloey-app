package com.example.chaoloey.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chaoloey.databinding.FragmentOnboardingPage1Binding

class OnboardingPage1Fragment : Fragment() {

    private lateinit var binding: FragmentOnboardingPage1Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingPage1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.getStartedButton.setOnClickListener {
            (activity as? OnboardingActivity)?.goToNextPage()
        }
    }
}

