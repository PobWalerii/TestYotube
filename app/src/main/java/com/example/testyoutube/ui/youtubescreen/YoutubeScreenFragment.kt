package com.example.testyoutube.ui.youtubescreen

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.delivery.utils.*
import com.example.testyoutube.R
import com.example.testyoutube.data.database.entity.ItemVideo
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
        verticalRecyclerView.layoutManager = GridLayoutManager(requireContext(),3)
        //verticalRecyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
    }
    private fun startUI() {
        binding.bottomBar.active = 1
        if ( !viewModel.isStarted ) {
            val sPref =
                requireActivity().getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
            val default = getString(R.string.start_response)
            val startResponse = sPref.getString("textSearch", default)
            val keyWord = startResponse ?: default
            viewModel.keyWord = keyWord
            getVideoFromDatabase()
            //getVideoList(keyWord)
            viewModel.isStarted = true
        }
    }

    private fun setOnSearchClickListener() {
        binding.appBarLayout.search.setOnClickListener {
            val textView = binding.appBarLayout.textSearch
            val keyWord = textView.text.toString()
            if (keyWord.isNotEmpty()) {
                hideKeyboardFromView(textView.context, textView)
                viewModel.keyWord = keyWord
                getVideoList(keyWord)
            }
        }
    }

    private fun getVideoList(keyWord: String) {
        viewModel.getVideoList(keyWord)
    }
    private fun getVideoFromDatabase() {
        viewModel.getVideoFromDatabase()
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
                    refreshUi(this)
                    //horisontalAdapter.setList(this)
                    //verticalAdapter.setList(this)
                    //val keyWord = viewModel.keyWord
                    //binding.responseSize = this.size
                    //binding.searchText = keyWord
                    //saveSearhText(keyWord)
                }
            }
            is ListLoading -> {
                binding.visibleProgress = state.isLoading
            }
            is ListFromBase -> {
                state.data.apply {
                    refreshUi(this)
                }
            }
        }
    }

    private fun refreshUi(list: List<ItemVideo>) {
        horisontalAdapter.setList(list)
        verticalAdapter.setList(list)
        val keyWord = viewModel.keyWord
        binding.responseSize = list.size
        binding.searchText = keyWord
        saveSearhText(keyWord)
    }

    private fun saveSearhText(keyWord: String) {
        val sPref = requireActivity().getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
        val ed: SharedPreferences.Editor = sPref.edit()
        ed.putString("textSearch", keyWord)
        ed.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}