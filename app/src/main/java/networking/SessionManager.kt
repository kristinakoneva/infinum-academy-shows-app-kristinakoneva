package networking

import android.content.Context
import android.content.SharedPreferences
import infinumacademy.showsapp.kristinakoneva.SHOWS_APP

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(SHOWS_APP, Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "USER_TOKEN"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }
}