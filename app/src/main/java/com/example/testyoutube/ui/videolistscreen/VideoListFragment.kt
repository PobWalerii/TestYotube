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
        if ( !viewModel.isStarted ) {
            val sPref =
                requireActivity().getSharedPreferences("MyPref", AppCompatActivity.MODE_PRIVATE)
            val default = getString(R.string.start_response)
            val startResponse = sPref.getString("textSearch", default)
            val keyWord = startResponse ?: default
            viewModel.keyWord = keyWord
            getVideoFromDatabase()
            getVideoList(keyWord)
        }
        else {
            getVideoFromDatabase()
            //viewModel.getCurrentVideo().apply {
              //  if(this!=null) {
                    //miniPlayerSetItem(this)


                    //horisontalAdapter.notifyDataSetChanged()
                    //refreshRecyclers(this,2)

                    //exchangeCurrentItem(this)

                    //refreshRecyclers(viewModel.getFirstFromCurrentList(),2)
                    //refreshRecyclers(this,2)

                //}
            //}
        //    playViewModel.navigationVideo(0)
        //    binding.responseSize = viewModel.getSizeVideoList()
        //    binding.searchText = viewModel.keyWord
        }
    }

    private fun refreshUi(list: List<ItemVideo>) {
        binding.responseSize = list.size
        binding.searchText = viewModel.keyWord
        if(!viewModel.isStarted) {
            viewModel.setCurrentList(list)
            viewModel.isStarted = true
            horisontalAdapter.setList(list.subList(0, COUNT_HORIZONTAL_ITEMS))
            verticalAdapter.setList(list)
            saveSearhText(viewModel.keyWord)
            exchangeCurrentItem(list[0])
        } else {
            val current = viewModel.getCurrentVideo()
            if( current != null ) {
                miniPlayerSetItem(current)
                refreshRecyclers(current)
            }
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
                state.data.let {
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
                hideKeyboardFromView(textView.context, textView)
                viewModel.keyWord = keyWord
                viewModel.isStarted = false
                getVideoList(keyWord)
                verticalAdapter.setList(emptyList())
                horisontalAdapter.setList(emptyList())
            }
        }
    }




    private fun startVideoFragmentListener() {
        binding.miniPlayer.miniPlayerContainer.setOnClickListener {
            startVideoFragment()
        }
    }

    private fun startVideoFragment() {
        findNavController().navigate(
            VideoListFragmentDirections.actionYoutubeScreenFragmentToVideoPlayFragment())
    }













    private fun setPlayerNavigationListener() {
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
                viewModel.isStarted = false
                viewModel.getVideoFromDatabase()
            }
            is ListLoaded -> {
                state.data.apply {
                    if(this.isNotEmpty()) {
                        binding.appBarLayout.textSearch.text.clear()
                        refreshUi(this)
                    } else {
                        Toast.makeText(context, R.string.request_is_empty,Toast.LENGTH_LONG).show()
                        viewModel.isStarted = false
                        viewModel.getVideoFromDatabase()
                    }
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