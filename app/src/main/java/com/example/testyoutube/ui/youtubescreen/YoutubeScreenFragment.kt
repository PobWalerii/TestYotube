package com.example.testyoutube.ui.youtubescreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.testyoutube.R
import com.example.testyoutube.databinding.FragmentYoutubeScreenBinding

class YoutubeScreenFragment : Fragment() {
    private var _binding: FragmentYoutubeScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentYoutubeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bottomBar.active = 1
        setOnMenuClickListener()
    }

    private fun setOnMenuClickListener() {
        binding.bottomBar.files.setOnClickListener {
            findNavController().navigate(
                YoutubeScreenFragmentDirections.actionYoutubeScreenFragmentToFilesScreenFragment()
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}