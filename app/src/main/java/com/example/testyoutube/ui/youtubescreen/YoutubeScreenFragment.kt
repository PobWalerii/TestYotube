package com.example.testyoutube.ui.youtubescreen

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.testyoutube.databinding.FragmentYoutubeScreenBinding
import com.example.testyoutube.utils.HideKeyboard.hideKeyboardFromView


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
        setOnSearchClickListener()
    }

    private fun setOnSearchClickListener() {
        binding.appBarLayout.search.setOnClickListener {
            val textView = binding.appBarLayout.textSearch
            val keyWord = textView.text.toString()
            if (keyWord.isNotEmpty()) {
                hideKeyboardFromView(textView.context, textView)
            }
        }
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