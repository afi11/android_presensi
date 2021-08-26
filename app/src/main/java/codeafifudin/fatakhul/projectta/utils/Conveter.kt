package codeafifudin.fatakhul.projectta.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class Conveter(context: Context) {

    val context = context

    fun removerCharacter(character: String, string: String) : String {
        val removeSpasi = character.replace("\\s".toRegex(),"")
        val result = removeSpasi.replace(string,"")
        return result
    }

    fun getBase64ForUriAndPossiblyCrash(uri: Uri): String {
        val bytes = context.contentResolver.openInputStream(uri)!!.readBytes()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun encodeBitmap(imageUri: Uri) : Bitmap {
        val imageStream : InputStream
        imageStream = context.contentResolver.openInputStream(imageUri)!!
        val selectedImage : Bitmap = BitmapFactory.decodeStream(imageStream)
        return selectedImage
    }

    fun encodeImageToBase64(bm : Bitmap) : String {
        val baos : ByteArrayOutputStream = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 75, baos)
        val b : ByteArray = baos.toByteArray()
        val encodedImage = Base64.encodeToString(b, Base64.DEFAULT)
        Log.d("camera64",encodedImage)
        return encodedImage
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun stringToDate(date: String) : String {
        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
        val date = LocalDate.parse(date, formatter)
        return date.toString();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun stringToTime(time: String) : String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
        val date = LocalDate.parse(time, formatter)
        return date.toString()
    }


}