package com.example.testyoutube.audiodata.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import com.example.testyoutube.audiodata.entity.ItemAudio
import com.example.testyoutube.utils.AudioListState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class AudioRepository {

    fun getAllAudioFromDevice(context: Context): Flow<AudioListState<List<ItemAudio>>> {
        return flow {
            emit(AudioListState.Loading(true))
            try {
                val list: List<ItemAudio> = responseAudio(context)
                emit(AudioListState.Success(list))
            } catch (exception: Exception) {
                emit(AudioListState.Error(exception.message ?: "Data loading error!"))
            }
            emit(AudioListState.Loading(false))
        }
    }

    private fun responseAudio(context: Context): List<ItemAudio> {

        val list = mutableListOf<ItemAudio>()
        var ii: Long = 0

        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection: Array<String> = arrayOf(
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.ArtistColumns.ARTIST
        )
        val cursor = context.getContentResolver().query(
            uri,
            projection,
            MediaStore.Audio.Media.DATA + " like ? ",
            arrayOf("%utm%"),
            null
        )
        Toast.makeText(context,"${cursor?.getString(0)}",Toast.LENGTH_LONG).show()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(ItemAudio(
                    ii++,
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
                ))
                Toast.makeText(context,"Есть",Toast.LENGTH_LONG).show()
            }
            cursor.close()
        }

        return list

    }


}