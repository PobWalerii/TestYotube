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
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.example.testyoutube.R
import com.example.testyoutube.data.database.entity.ItemVideo
import com.example.testyoutube.databinding.FragmentYoutubeScreenBinding
import com.example.testyoutube.ui.videoplayscreen.VideoPlayViewModel
import com.example.testyoutube.utils.*
import com.example.testyoutube.utils.Constants.COUNT_HORIZONTAL_ITEMS
import com.example.testyoutube.utils.HideKeyboard.hideKeyboardFromView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
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
    var youTubePlayer: YouTubePlayer? = null

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
        startUI()
        observeUiState()
        setupItemClickListener()
        setOnBottomMenuClickListener()
        startVideoFragmentListener()
        observeListState()
        setPlayerNavigationListener()
        setOnSearchClickListener()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun startUI() {
        binding.bottomBar.active = 1
        playViewModel.currentId = ""
        playViewModel.lastPlayId = ""
        if ( !viewModel.isStarted ) {
            val sPref =
                requireActivity().getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
            val default = getString(R.string.start_response)
            val startResponse = sPref.getString("textSearch", default)
            val keyWord = startResponse ?: default
            viewModel.keyWord = keyWord
        }
        getVideoFromDatabase()
    }

    private fun refreshUi(list: List<ItemVideo>) {
        if(!viewModel.isStarted) {  // start app
            if(viewModel.isBaseLoaded) {
                binding.searchText = viewModel.keyWord
                binding.responseSize = list.size
                horisontalAdapter.setList(
                    if(list.size==0) {
                        list
                    } else {
                        list.subList(0, COUNT_HORIZONTAL_ITEMS).toList()
                    })
                verticalAdapter.setList(list)
                viewModel.setCurrentList(list)
                if(list.isNotEmpty()) {
                    exchangeCurrentItem(list[0])
                }

                viewModel.isBaseLoaded = false
                getVideoList(viewModel.keyWord)
            }
            if(viewModel.isApiLoaded) {
                if(list.isNotEmpty()) {

                    binding.responseSize = list.size
                    horisontalAdapter.setList(list.subList(0, COUNT_HORIZONTAL_ITEMS).toList())
                    verticalAdapter.setList(list)
                    viewModel.setCurrentList(list)
                    exchangeCurrentItem(list[0])

                }
                viewModel.isApiLoaded = false
                viewModel.isStarted = true
            }
        } else if(viewModel.isBaseLoaded) { // return from fragments
                binding.searchText = viewModel.keyWord
                binding.responseSize = list.size
                val current = viewModel.getCurrentVideo()
                if (current != null) {
                    miniPlayerSetItem(current)
                    refreshRecyclers(current)
                }
                viewModel.isBaseLoaded = false
        }

            if(viewModel.isSearhResponse) {  // searchresponse

                viewModel.isSearhResponse=false
                viewModel.isApiLoaded=false

                if(list.isNotEmpty()) {
                    binding.appBarLayout.textSearch.text.clear()
                    viewModel.keyWord = viewModel.keyWordForSearh
                    saveSearhText(viewModel.keyWord)

                    verticalAdapter.setList(emptyList())
                    horisontalAdapter.setList(emptyList())

                    binding.searchText = viewModel.keyWord
                    binding.responseSize = list.size

                    horisontalAdapter.setList(list.subList(0, COUNT_HORIZONTAL_ITEMS))
                    verticalAdapter.setList(list)
                    viewModel.setCurrentList(list)
                    exchangeCurrentItem(list[0])
                    refreshRecyclers(list[0])
                }
            }

    }

    private fun exchangeCurrentItem(current: ItemVideo) {
        viewModel.setCurrentVideo(current)
        miniPlayerSetItem(current)
    }

    private fun setupItemClickListener() {
        horisontalAdapter.setOnItemClickListener(object : HorisontalListAdapter.OnItemClickListener {
            override fun onItemClick(current: ItemVideo) {
                exchangeCurrentItem(current)
                refreshRecyclers(current,1)
            }
        })
        verticalAdapter.setOnItemClickListener(object : VerticalListAdapter.OnItemClickListener {
            override fun onItemClick(current: ItemVideo) {
                exchangeCurrentItem(current)
                refreshRecyclers(current,2)
            }
        })
    }

    private fun refreshRecyclers(current: ItemVideo, type: Int = 0) {
        if( type != 2 ) {
            val position1 = verticalAdapter.getItemPosition(current)
            verticalAdapter.setCurrentIdFromPosition(position1)
            verticalRecyclerView.layoutManager?.scrollToPosition(position1)
        }
        if( type !=1 ) {
            val position2 = horisontalAdapter.getItemPosition(current)
            if (position2 != -1) {
                horisontalAdapter.setCurrentIdFromPosition(position2)
                horisontalRecyclerView.layoutManager?.scrollToPosition(position2)
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
                    val current = viewModel.getCurrentVideo()
                    if(current!=null) {
                        miniPlayerSetItem(current)
                        refreshRecyclers(current)
                    }
                }
            }
        }
    }

    private fun setOnSearchClickListener() {
        binding.appBarLayout.search.setOnClickListener {
            val textView = binding.appBarLayout.textSearch
            val keyWord = textView.text.toString()
            if (keyWord.isNotEmpty()) {
                viewModel.keyWordForSearh = keyWord
                viewModel.isSearhResponse = true
                hideKeyboardFromView(textView.context, textView)
                getVideoList(keyWord)
            }
        }
    }

    private fun startVideoFragmentListener() {
        binding.miniPlayer.miniPlayerContainer.setOnClickListener {
            if(viewModel.getCurrentVideo()!=null) {
                findNavController().navigate(
                    VideoListFragmentDirections.actionYoutubeScreenFragmentToVideoPlayFragment())
            }
        }
    }

    private fun setPlayerNavigationListener() {
        binding.miniPlayer.imageNext.setOnClickListener {
            playViewModel.navigationVideo(1)
            binding.miniPlayer.isPlay = false
            youTubePlayer?.pause()
        }
        binding.miniPlayer.imagePrev.setOnClickListener {
            playViewModel.navigationVideo(-1)
            binding.miniPlayer.isPlay = false
            youTubePlayer?.pause()
        }
        binding.miniPlayer.imagePlay.setOnClickListener {
            val current = viewModel.getCurrentVideo()
            if(current!=null) {
                playViewModel.currentId = current.videoId
                if (playViewModel.lastPlayId == playViewModel.currentId) {
                    youTubePlayer?.play()
                } else {
                    youTubePlayer?.loadVideo(playViewModel.currentId, 0F)
                    playViewModel.lastPlayId = playViewModel.currentId
                }
                binding.miniPlayer.isPlay = true
            }
        }
        binding.miniPlayer.imageStop.setOnClickListener {
            binding.miniPlayer.isPlay = false
            youTubePlayer?.pause()
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
                val textError: String? = state.message
                val stringError: String = context?.getString(R.string.data_error) +
                        if(textError=="HTTP 400 " || textError=="HTTP 401 " || textError=="HTTP 403 " || textError=="HTTP 404 ") {
                            " $textError"
                        } else {
                            " "+ context?.getString(R.string.internet_error)
                        }
                Toast.makeText(
                    requireContext(), stringError, Toast.LENGTH_SHORT
                ).show()
                viewModel.isApiLoaded = true
                refreshUi(emptyList())
            }
            is ListLoaded -> {
                viewModel.isApiLoaded = true
                state.data.apply {
                    if(this.isEmpty()) {
                        Toast.makeText(context, R.string.request_is_empty,Toast.LENGTH_LONG).show()
                    }
                    refreshUi(this)
                }
            }
            is ListLoading -> {
                binding.visibleProgress = state.isLoading
            }
            is ListFromBase -> {
                viewModel.isBaseLoaded = true
                state.data.apply {
                    refreshUi(this)
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
        horisontalRecyclerView.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        verticalRecyclerView = binding.recyclerContent
        verticalRecyclerView.adapter = verticalAdapter
        verticalRecyclerView.layoutManager = GridLayoutManager(context,3)
    }

    private fun getVideoList(keyWord: String) {
        viewModel.getVideoList(keyWord)
    }
    private fun getVideoFromDatabase() {
        viewModel.getVideoFromDatabase()
    }

    private fun miniPlayerSetItem(current: ItemVideo) {
        binding.miniPlayer.image = current.imageUrl
        binding.miniPlayer.channel = current.channelTitle
        binding.miniPlayer.title = current.title
        playViewModel.currentId= current.videoId
        if( youTubePlayer == null ) {
            playViewModel.lastPlayId = current.videoId
            initYoutubePlayerView()
        } else {
            youTubePlayer?.pause()
            binding.miniPlayer.isPlay = false
        }
    }

    private fun saveSearhText(keyWord: String) {
        val sPref = requireActivity().getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
        val ed: SharedPreferences.Editor = sPref.edit()
        ed.putString("textSearch", keyWord)
        ed.apply()
    }

    private fun initYoutubePlayerView() {
        val youTubePlayerView = binding.miniPlayer.youtubePlayerView
        lifecycle.addObserver(youTubePlayerView)
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                super.onError(youTubePlayer, error)
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
            }
            override fun onReady(youTubePlayer: YouTubePlayer) {
                this@VideoListFragment.youTubePlayer = youTubePlayer
                youTubePlayer.cueVideo(playViewModel.currentId,0F)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        youTubePlayer = null
        _binding = null
    }

}