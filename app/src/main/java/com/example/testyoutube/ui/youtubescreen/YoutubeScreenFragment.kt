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
import com.example.delivery.utils.*
import com.example.testyoutube.R
import com.example.testyoutube.data.database.entity.ItemVideo
import com.example.testyoutube.databinding.FragmentYoutubeScreenBinding
import com.example.testyoutube.utils.Constants
import com.example.testyoutube.utils.Constants.COUNT_HORIZONTAL_ITEMS
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
        Toast.makeText(context,"onViewCreated",Toast.LENGTH_SHORT).show()
        startUI()
        setupRecyclers()
        observeUiState()
        setOnMenuClickListener()
        setOnSearchClickListener()
        setupItemClickListener()
        setItemNavigationListener()
    }

    private fun setupItemClickListener() {
        horisontalAdapter.setOnItemClickListener(object : HorisontalListAdapter.OnItemClickListener {
            override fun onItemClick(current: ItemVideo) {
                miniPlayerSetItem(current)
            }
        })
        verticalAdapter.setOnItemClickListener(object : VerticalListAdapter.OnItemClickListener {
            override fun onItemClick(current: ItemVideo) {
                miniPlayerSetItem(current)
            }
        })
    }

    private fun miniPlayerSetItem(current: ItemVideo) {
        Toast.makeText(context,"miniPlayerSetItem",Toast.LENGTH_SHORT).show()
        viewModel.currentItem = current
        with (binding.miniPlayer) {
            image = current.imageUrl
            channel = current.channelTitle
            title = current.title
        }

    }

    private fun setupAdapters() {
        Toast.makeText(context,"setupAdapters",Toast.LENGTH_SHORT).show()
        horisontalAdapter = HorisontalListAdapter()
        horisontalAdapter.setHasStableIds(true)
        verticalAdapter = VerticalListAdapter()
        verticalAdapter.setHasStableIds(true)
    }

    private fun setupRecyclers() {
        Toast.makeText(context,"setupRecycler",Toast.LENGTH_SHORT).show()
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
            Toast.makeText(context,"startUI",Toast.LENGTH_SHORT).show()
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
            Toast.makeText(context,"setOnSearchClickListener",Toast.LENGTH_SHORT).show()
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
        Toast.makeText(context,"getVideoList",Toast.LENGTH_SHORT).show()
        viewModel.getVideoList(keyWord)
    }
    private fun getVideoFromDatabase() {
        Toast.makeText(context,"getVideoFromDatabase",Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(context,"ListLoaded",Toast.LENGTH_SHORT).show()
                    refreshUi(this)
                }
            }
            is ListLoading -> {
                binding.visibleProgress = state.isLoading
            }
            is ListFromBase -> {
                state.data.apply {
                Toast.makeText(context, "ListFromBase", Toast.LENGTH_SHORT).show()
                    refreshUi(this)
                }
            }
        }
    }

    private fun refreshUi(list: List<ItemVideo>) {
        Toast.makeText(context,"refreshUi",Toast.LENGTH_SHORT).show()
        horisontalAdapter.setList(list.take(COUNT_HORIZONTAL_ITEMS))
        verticalAdapter.setList(list)
        val keyWord = viewModel.keyWord
        binding.responseSize = list.size
        binding.searchText = keyWord
        saveSearhText(keyWord)
        miniPlayerSetItem(list[0])
    }

    private fun setItemNavigationListener() {
        binding.miniPlayer.imageNext.setOnClickListener {
            refreshPlayer(1)
        }
        binding.miniPlayer.imagePrev.setOnClickListener {
            refreshPlayer(-1)
        }
    }

    private fun refreshPlayer(bias: Int) {
        Toast.makeText(context,"refreshPlayer",Toast.LENGTH_SHORT).show()
        val position = verticalAdapter.getPosition(viewModel.currentItem, bias)
        if(position != -1) {
            val item: ItemVideo = verticalAdapter.getItemFromPosition(position)
            verticalRecyclerView.layoutManager?.scrollToPosition(position)
            if (position < COUNT_HORIZONTAL_ITEMS) {
                horisontalRecyclerView.layoutManager?.scrollToPosition(position)
            }
            miniPlayerSetItem(item)
        }
    }

    private fun saveSearhText(keyWord: String) {
        val sPref = requireActivity().getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
        val ed: SharedPreferences.Editor = sPref.edit()
        ed.putString("textSearch", keyWord)
        ed.apply()
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(context,"onResume",Toast.LENGTH_SHORT).show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}