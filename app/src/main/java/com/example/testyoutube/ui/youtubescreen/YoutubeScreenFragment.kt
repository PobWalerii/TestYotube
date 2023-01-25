package com.example.testyoutube.ui.youtubescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.testyoutube.R
import com.example.testyoutube.databinding.FragmentYoutubeScreenBinding
import com.example.testyoutube.utils.HideKeyboard.hideKeyboardFromView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YoutubeScreenFragment : Fragment() {
    private var _binding: FragmentYoutubeScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<VideoListViewModel>()
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
        startUI()
        setOnMenuClickListener()
        setOnSearchClickListener()
    }

    private fun startUI() {
        binding.bottomBar.active = 1
        val sPref = requireActivity().getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
        val default = getString(R.string.start_response)
        val startResponse = sPref.getString("textResponse", default)
        getVideoList(startResponse ?: default)
    }

    private fun setOnSearchClickListener() {
        binding.appBarLayout.search.setOnClickListener {
            val textView = binding.appBarLayout.textSearch
            val keyWord = textView.text.toString()
            if (keyWord.isNotEmpty()) {
                hideKeyboardFromView(textView.context, textView)
                getVideoList(keyWord)
            }
        }
    }

    private fun getVideoList(keyWord: String) {


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