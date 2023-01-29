package com.example.testyoutube.audiodata.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import com.example.testyoutube.audiodata.entity.AudioFiles
import com.example.testyoutube.utils.AudioListState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class AudioRepository {

    fun getAllAudioFromDevice(context: Context): Flow<AudioListState<List<AudioFiles>>> {
        return flow {
            emit(AudioListState.Loading(true))

            try {
                val audioList: MutableList<AudioFiles> = mutableListOf()
                audioList.add(AudioFiles(
                    "cursor.getString(0)",
                    "cursor.getString(1)",
                    "cursor.getString(2)",
                    "cursor.getString(3)"
                ))


                val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                val projection = arrayOf(
                    MediaStore.Audio.AudioColumns.DATA,
                    MediaStore.Audio.AudioColumns.TITLE,
                    MediaStore.Audio.AudioColumns.ALBUM,
                    MediaStore.Audio.ArtistColumns.ARTIST
                )
                val cursor = context.contentResolver?.query(
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
                emit(AudioListState.Success(audioList))
            } catch (exception: Exception) {
                emit(AudioListState.Error(exception.message ?: "Data loading error!"))
            }
            emit(AudioListState.Loading(false))
        }
    }


}