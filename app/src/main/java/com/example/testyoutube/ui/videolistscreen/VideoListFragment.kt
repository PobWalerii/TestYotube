package com.example.testyoutube.ui.videolistscreen

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testyoutube.R
import com.example.testyoutube.data.database.entity.ItemVideo
import com.example.testyoutube.databinding.FragmentYoutubeScreenBinding
import com.example.testyoutube.ui.videoplayscreen.VideoPlayViewModel
import com.example.testyoutube.utils.*
import com.example.testyoutube.utils.Constants.COUNT_HORIZONTAL_ITEMS
import com.example.testyoutube.utils.HideKeyboard.hideKeyboardFromView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoListFragment : Fragment() {
    private var _binding: FragmentYoutubeScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<VideoListViewModel>()
    private val playViewModel by viewModels<VideoPlayViewModel>()
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
        setupRecyclers()

        observeUiState()
        setupItemClickListener()
        setOnBottomMenuClickListener()
        startUI()

    //observeListState()

        //setOnSearchClickListener()

        //setItemNavigationListener()
        //startVideoListener()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun startUI() {
        horisontalRecyclerView.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.bottomBar.active = 1
        if ( !viewModel.isStarted ) {
            Toast.makeText(context,"1",Toast.LENGTH_SHORT).show()
            val sPref =
                requireActivity().getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
            val default = getString(R.string.start_response)
            val startResponse = sPref.getString("textSearch", default)
            val keyWord = startResponse ?: default
            viewModel.keyWord = keyWord
            getVideoFromDatabase()
            getVideoList(keyWord)
            viewModel.isStarted = true
        }
        else {
            Toast.makeText(context,"2",Toast.LENGTH_SHORT).show()
            viewModel.getCurrentVideo().apply {
                if(this!=null) {
                    //miniPlayerSetItem(this)
                    //horisontalAdapter.notifyDataSetChanged()
                    //refreshRecyclers(this,2)
                    exchangeCurrentItem(this)
                    refreshRecyclers(this,2)

                }
            }
        //    playViewModel.navigationVideo(0)
        //    binding.responseSize = viewModel.getSizeVideoList()
        //    binding.searchText = viewModel.keyWord
        }
    }

    private fun refreshUi(list: List<ItemVideo>) {
        viewModel.setCurrentList(list)
        horisontalAdapter.setList(list.take(COUNT_HORIZONTAL_ITEMS))
        verticalAdapter.setList(list)
        val keyWord = viewModel.keyWord
        binding.responseSize = list.size
        binding.searchText = keyWord
        saveSearhText(keyWord)
        if(list.isNotEmpty()) {
            exchangeCurrentItem(list[0])
            //viewModel.setCurrentVideo(list[0])
            //playViewModel.navigationVideo(0)
        }
    }

    private fun exchangeCurrentItem(current: ItemVideo) {
        viewModel.setCurrentVideo(current)
        //playViewModel.navigationVideo(0)
        miniPlayerSetItem(current)
    }

    private fun setupItemClickListener() {
        horisontalAdapter.setOnItemClickListener(object : HorisontalListAdapter.OnItemClickListener {
            override fun onItemClick(current: ItemVideo) {
                exchangeCurrentItem(current)
                refreshRecyclers(current,1)
                //viewModel.setCurrentVideo(current)
                //miniPlayerSetItem(current)

            }
        })
        verticalAdapter.setOnItemClickListener(object : VerticalListAdapter.OnItemClickListener {
            override fun onItemClick(current: ItemVideo) {
                exchangeCurrentItem(current)
                refreshRecyclers(current,2)
                //viewModel.setCurrentVideo(current)
                //miniPlayerSetItem(current)

            }
        })
    }

    private fun refreshRecyclers(current: ItemVideo, type: Int = 0) {
        var position = 0
        if( type != 2 ) {
            position = verticalAdapter.getItemPosition(current)
            verticalAdapter.setCurrentIdFromPosition(position)
            verticalRecyclerView.layoutManager?.scrollToPosition(position)
        }
        if( type !=1 ) {
            position = horisontalAdapter.getItemPosition(current)
            Toast.makeText(context,"position = $position",Toast.LENGTH_SHORT).show()
            if (position != -1) {
                horisontalAdapter.setCurrentIdFromPosition(position)
                horisontalRecyclerView.layoutManager?.scrollToPosition(position)
            }
        }
    }














    private fun observeListState() {
        playViewModel.state.observe(viewLifecycleOwner, ::handleItemUiState)
    }
    private fun handleItemUiState(state: ItemUiState) {
        when (state) {
            is ExchangeItem -> {
                state.data.apply {
                    miniPlayerSetItem(this)
                    refreshRecyclers(this)
                }
            }
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




    private fun startVideoListener() {
        binding.miniPlayer.miniPlayerContainer.setOnClickListener {
            startVideoFragment()
        }
    }

    private fun startVideoFragment() {
        findNavController().navigate(
            VideoListFragmentDirections.actionYoutubeScreenFragmentToVideoPlayFragment())
    }



    private fun setItemNavigationListener() {
        binding.miniPlayer.imageNext.setOnClickListener {
            playViewModel.navigationVideo(1)
        }
        binding.miniPlayer.imagePrev.setOnClickListener {
            playViewModel.navigationVideo(-1)
        }
        binding.miniPlayer.imagePlay.setOnClickListener {
            startVideoFragment()
        }
    }











    private fun setOnBottomMenuClickListener() {
        binding.bottomBar.files.setOnClickListener {
            findNavController().navigate(
                VideoListFragmentDirections.actionYoutubeScreenFragmentToPermissionsFragment()
            )
        }
    }

    private fun observeUiState() {
        viewModel.stateList.observe(viewLifecycleOwner, ::handleUiState)
    }

    private fun handleUiState(state: ListUiState) {
        when (state) {
            is ListError -> {
                Toast.makeText(
                    requireContext(), state.message, Toast.LENGTH_SHORT
                ).show()
            }
            is ListLoaded -> {
                state.data.apply {
                    refreshUi(this)
                }
            }
            is ListLoading -> {
                binding.visibleProgress = state.isLoading
            }
            is ListFromBase -> {
                state.data.apply {
                    if(this.isNotEmpty()) {
                        refreshUi(this)
                    }
                    //viewModel.isBaseLoaded = true
                }
            }
        }
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
        horisontalRecyclerView.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        verticalRecyclerView = binding.recyclerContent
        verticalRecyclerView.adapter = verticalAdapter
        verticalRecyclerView.layoutManager = GridLayoutManager(requireContext(),3)
    }

    private fun getVideoList(keyWord: String) {
        viewModel.getVideoList(keyWord)
    }
    private fun getVideoFromDatabase() {
        viewModel.getVideoFromDatabase()
    }

    private fun miniPlayerSetItem(current: ItemVideo) {
        with (binding.miniPlayer) {
            image = current.imageUrl
            channel = current.channelTitle
            title = current.title
        }
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