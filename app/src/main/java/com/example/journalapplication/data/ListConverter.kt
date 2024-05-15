package com.example.journalapplication.data

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter

class ArrayListConverter {
    @TypeConverter
    fun fromUriToString(value: Uri): String {
        return value.toString()
    }

    @TypeConverter
    fun fromStringToUri(value: String): Uri {
        return value.toUri()
    }

}
