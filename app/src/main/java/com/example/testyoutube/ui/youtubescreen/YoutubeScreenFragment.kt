package com.example.testyoutube.ui.youtubescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.delivery.utils.ListError
import com.example.delivery.utils.ListLoaded
import com.example.delivery.utils.ListLoading
import com.example.delivery.utils.ListUiState
import com.example.testyoutube.R
import com.example.testyoutube.databinding.FragmentYoutubeScreenBinding
import com.example.testyoutube.utils.HideKeyboard.hideKeyboardFromView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YoutubeScreenFragment : Fragment() {
    private var _binding: FragmentYoutubeScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<VideoListViewModel>()
    private lateinit var horisontalAdapter: HorisontalListAdapter
    private lateinit var verticalAdapter: VerticalListAdapter
    private lateinit var horisontalRecyclerView: RecyclerView
    private lateinit var verticalRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAdapters()
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
        setupRecyclers()
        observeUiState()
        setOnMenuClickListener()
        setOnSearchClickListener()
    }

    private fun setupAdapters() {
        horisontalAdapter = HorisontalListAdapter()
        horisontalAdapter.setHasStableIds(true)
        verticalAdapter = VerticalListAdapter()
        verticalAdapter.setHasStableIds(true)
    }

    private fun setupRecyclers() {
        horisontalRecyclerView = binding.recyclerChannels
        horisontalRecyclerView.adapter = horisontalAdapter
        verticalRecyclerView = binding.recyclerContent
        verticalRecyclerView.adapter = verticalAdapter
        verticalRecyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
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
        viewModel.getVideoList(keyWord)
    }

    private fun setOnMenuClickListener() {
        binding.bottomBar.files.setOnClickListener {
            findNavController().navigate(
                YoutubeScreenFragmentDirections.actionYoutubeScreenFragmentToFilesScreenFragment()
            )
        }
    }

    private fun observeUiState() {
        viewModel.state.observe(viewLifecycleOwner, ::handleUiState)
    }

    private fun handleUiState(state: ListUiState) {
        when (state) {
            is ListError -> Toast.makeText(
                requireContext(), state.message, Toast.LENGTH_SHORT
            ).show()
            is ListLoaded -> {
                state.data.apply {
                    horisontalAdapter.setList(this)
                    verticalAdapter.setList(this)
                }
            }
            is ListLoading -> {
                binding.visibleProgress = state.isLoading
            }
        }
    }






    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}