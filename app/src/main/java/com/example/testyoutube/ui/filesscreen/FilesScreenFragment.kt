package com.example.testyoutube.ui.filesscreen

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.testyoutube.data.audiodata.AudioFiles
import com.example.testyoutube.databinding.FragmentFilesScreenBinding

//https://riptutorial.com/android/example/23916/fetch-audio-mp3-files-from-specific-folder-of-device-or-fetch-all-files
//android kotlin mp3 files in directory
//https://developer.android.com/training/data-storage/shared/media

class FilesScreenFragment : Fragment() {

    private var _binding: FragmentFilesScreenBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFilesScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startUI()
        setOnMenuClickListener()
    }

    private fun startUI() {
        binding.bottomBar.active = 2
        val list: List<AudioFiles> = getAllAudioFromDevice()
        binding.count.text = "${list.size}"
    }

    private fun setOnMenuClickListener() {
        binding.bottomBar.music.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getAllAudioFromDevice(): List<AudioFiles> {

        val audioList: MutableList<AudioFiles> = mutableListOf()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.ArtistColumns.ARTIST
        )
        val cursor = context?.contentResolver?.query(
            uri,
            projection,
            MediaStore.Audio.Media.DATA + " like ? ",
            arrayOf("%utm%"),
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val file = AudioFiles(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
                )
                audioList.add(file)
            }
            cursor.close()
        }
        return audioList
    }

}