package  com.fivegbet.fiveg.bets.esportedasrote.data

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

object DataManager {

    fun saveData(context:Context,isFirst:Boolean, link:String){
        val sharedPreferences:SharedPreferences = context.getSharedPreferences("main_settings",AppCompatActivity.MODE_PRIVATE)
        var settings = sharedPreferences.edit()
        settings.putString("link",link)
        settings.putBoolean("app_state",isFirst)
        settings.apply()
    }

    fun loadData(context: Context){

        var sharedPreferences:SharedPreferences = context.getSharedPreferences("main_settings",AppCompatActivity.MODE_PRIVATE)
        sharedPreferences.getBoolean("app_state",AppSettings.isFirstOpen)
        sharedPreferences.getString("link",AppSettings.linkForOpen)





    }

}