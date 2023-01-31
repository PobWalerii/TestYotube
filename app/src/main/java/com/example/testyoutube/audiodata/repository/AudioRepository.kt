package com.example.testyoutube.audiodata.repository

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.testyoutube.audiodata.entity.ItemAudio
import com.example.testyoutube.utils.AudioListState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AudioRepository {

    var context: Context? = null

    fun getAllAudioFromDevice(): Flow<AudioListState<List<ItemAudio>>> {
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

    @SuppressLint("InlinedApi")
    private fun responseAudio(context: Context?): List<ItemAudio> {

        val audioList = mutableListOf<ItemAudio>()
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
        val cursor = context?.getContentResolver()?.query(
            uri,
            projections,
            selection,
            null,
            "LOWER (" + MediaStore.Audio.Media.TITLE + ") ASC"
        )

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    //if (file.name.endsWith(".mp3")) {
                    val id: Long = cursor.getLong(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    )
                    val contentUri = Uri.withAppendedPath(uri, id.toString()).toString()
                    val path: String = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                    val album_id: Long = cursor.getLong(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                    val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                    val imageUri = Uri.withAppendedPath(sArtworkUri, album_id.toString())

                    val audioContent = ItemAudio(
                        cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                MediaStore.Audio.Media.DISPLAY_NAME)),
                        cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                MediaStore.Audio.Media.TITLE)),
                        id,
                        contentUri,
                        path,

                        cursor.getLong(
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)),

                        cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                MediaStore.Audio.Media.ALBUM)),
                        cursor.getLong(
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),

                        imageUri,

                        cursor.getString(
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),

                        cursor.getString(
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER)),

                        GetGenre(
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)))
                    )
                    audioList.add(audioContent)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return audioList
    }


    private fun GetGenre(media_id: Int): String? {
        val genresProj = arrayOf(
            MediaStore.Audio.Genres.NAME,
            MediaStore.Audio.Genres._ID
        )
        val uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", media_id)
        val genresCursor: Cursor? =
            context?.getContentResolver()?.query(uri, genresProj, null, null, null)
        val genreIndex = genresCursor?.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)
        var genre: String? = ""
        if(genresCursor!=null && genreIndex!=null) {
            while (genresCursor.moveToNext()) {
                genre = genresCursor.getString(genreIndex)
            }
        }
        genresCursor?.close()
        return genre
    }




}