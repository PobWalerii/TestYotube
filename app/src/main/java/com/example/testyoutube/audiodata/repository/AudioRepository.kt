package com.example.testyoutube.audiodata.repository

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.example.testyoutube.audiodata.entity.ItemAudio
import com.example.testyoutube.utils.AudioListState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AudioRepository @Inject constructor(
    @ApplicationContext
    private val context: Context
){

    fun getAllAudioFromDevice(): Flow<AudioListState<List<ItemAudio>>> {
        return flow {
            //emit(AudioListState.SearchFiles(true))
            try {
                val list: List<ItemAudio> = responseAudio(context)
                emit(AudioListState.Success(list))
            } catch (exception: Exception) {
                emit(AudioListState.Error(exception.message ?: "Data loading error!"))
            }
            //emit(AudioListState.SearchFiles(false))
        }
    }

    @SuppressLint("InlinedApi")
    private fun responseAudio(context: Context?): List<ItemAudio> {

        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projections = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.BUCKET_ID
        )
        val cursor = context?.contentResolver?.query(
            uri,
            projections,
            selection,
            null,
            "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC"
        )

        val audioList = mutableListOf<ItemAudio>()
        if (cursor != null) {

            if (cursor.moveToFirst()) {
                val mmr = MediaMetadataRetriever()
                do {
                    val name: String = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))

                    if (name.endsWith(".mp3")) {
                        val id: Long = cursor.getLong(
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                        )
                        val contentUri = Uri.withAppendedPath(uri, id.toString()).toString()
                        val path: String = cursor.getString(
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                        )
                        val albumId: Long = cursor.getLong(
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                        )
                        val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                        val imageUri = Uri.withAppendedPath(sArtworkUri, albumId.toString())

                        mmr.setDataSource(path)
                        val artBytes = mmr.embeddedPicture
                        var bm: Bitmap? = null
                        if (artBytes != null) {
                            bm = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.size)
                        }

                        val audioContent = ItemAudio(
                            name,
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                            id,
                            contentUri,
                            path,
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)),
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                            imageUri,
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER)),
                            bm
                        )
                        audioList.add(audioContent)
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return audioList
    }


}