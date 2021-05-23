package com.example.androidapp.model

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.util.JsonReader
import android.util.Log
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter


data class CardInfo(
    @SerializedName("STATUS") var status: String,
    @SerializedName("TICKETID") var ticketid: String,
    @SerializedName("REPORTEDBY") var reportedby: String,
    @SerializedName("CLASSIDMAIN") var classidmain: String,
    @SerializedName("CRITIC_LEVEL") var criticLevel: String,
    @SerializedName("ISKNOWNERRORDATE") var isknownerrordate: String,
    @SerializedName("TARGETFINISH") var targetfinish: String,
    @SerializedName("DESCRIPTION") var description: String,
    @SerializedName("EXTSYSNAME") var extsysname: String,
    @SerializedName("NORM") var norm: String,
    @SerializedName("LNORM") var lnorm: String
) {

}

inline fun <reified T> Gson.fromJson(json: String) =
    fromJson<T>(json, object : TypeToken<T>() {}.type)

@SuppressLint("StaticFieldLeak")
object DataModel {
    private const val READ_EXTERNAL_STORAGE_REQUEST = 1
    private const val WRITE_EXTERNAL_STORAGE = 2

    private var activity: Activity? = null

    private var userList: MutableList<CardInfo> = ArrayList()

    fun onAttach(activity: Activity) {
        this.activity = activity
    }

    fun onDetach() {
        this.activity = null
    }

    private fun createNew() {
        val baseDir = activity!!.getExternalFilesDir("/")?.absolutePath
        val myDir = File("$baseDir/SDCARD")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val file = File(myDir, "incidents.json")
        if (!file.exists()) {
            Log.d("gog", "File not exist. Creating.")
            file.createNewFile()
            val fileWriter = FileWriter(file)
            fileWriter.write("[{\"STATUS\":\"НОВОЕ\",\"TICKETID\":\"197144857\",\"REPORTEDBY\":\"P688038\",\"CLASSIDMAIN\":\"090004012679\",\"CRITIC_LEVEL\":\"3\",\"ISKNOWNERRORDATE\":\"2021-04-28T00:00:00+03:00\",\"TARGETFINISH\":\"2021-04-30T23:00:00+03:00\",\"DESCRIPTION\":\"П/Недовернутые шурупы; АКТИВ: Стрелочный перевод № 10, на станции УЖОВКА ( 0 км, 1 пк, 67 м  - 0 км, 1 пк, 67 м  )\",\"EXTSYSNAME\":\"Ручной ввод\",\"NORM\":\"0.0\",\"LNORM\":\"0\"}, {\"STATUS\":\"НОВОЕ\",\"TICKETID\":\"196003784\",\"REPORTEDBY\":\"P686280\",\"CLASSIDMAIN\":\"090004000823\",\"CRITIC_LEVEL\":\"3\",\"ISKNOWNERRORDATE\":\"2021-04-21T13:38:08+03:00\",\"TARGETFINISH\":\"2021-05-05T13:38:06+03:00\",\"DESCRIPTION\":\"Ослабление вертикальных болтов; АКТИВ: Стрелочный перевод № 10, на станции УЖОВКА; ОТКЛОНЕНИЕ: 1\",\"EXTSYSNAME\":\"МРМ\",\"NORM\":\"1.0\",\"LNORM\":\"0\"}]")
            fileWriter.flush()
            fileWriter.close()
        }



        Log.d("gog", "$file exist:${file.exists()} listFiles:${file.listFiles()}")
    }

    fun getCardsFromSDCARD(): List<CardInfo> {
        checkPermissions()

        createNew()

        val baseDir = activity!!.getExternalFilesDir("/")?.absolutePath
        val dir =  File(baseDir).listFiles()[0]
        val file = dir.listFiles()[0]

        val reader = FileReader(file)
        val json = reader.readLines()[0]

        val gson = Gson()

        userList = gson.fromJson<List<CardInfo>>(json).toMutableList()
        Log.d("gog", userList.toString())
        return userList
    }

    fun getCardById(id: String): CardInfo {
        return userList.find { card -> card.ticketid == id }!!
    }

    private fun checkPermissions() {
        if (checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_REQUEST
            )
            return
        }
        if (checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE)
            return
        }
    }
}