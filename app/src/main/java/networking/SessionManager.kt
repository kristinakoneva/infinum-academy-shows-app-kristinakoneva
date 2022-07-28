package networking

import android.content.Context
import android.content.SharedPreferences
import infinumacademy.showsapp.kristinakoneva.Constants

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(Constants.SHOWS_APP, Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "USER_TOKEN"
        const val CLIENT = "CLIENT"
        const val EXPIRY = "EXPIRY"
        const val UID = "UID"
        const val CONTENT_TYPE = "CONTENT-TYPE"
    }

    fun clearSession(){
        // TODO: implement
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

    fun saveClient(client: String){
        val editor = prefs.edit()
        editor.putString(CLIENT, client)
        editor.apply()
    }

    fun fetchClient(): String? {
        return prefs.getString(CLIENT, null)
    }

    fun saveExpiry(expiry: String){
        val editor = prefs.edit()
        editor.putString(EXPIRY, expiry)
        editor.apply()
    }

    fun fetchExpiry(): String? {
        return prefs.getString(EXPIRY, null)
    }

    fun saveUID(uid: String){
        val editor = prefs.edit()
        editor.putString(UID, uid)
        editor.apply()
    }

    fun fetchUID(): String? {
        return prefs.getString(UID, null)
    }

    fun saveContentType(contentType: String){
        val editor = prefs.edit()
        editor.putString(CONTENT_TYPE, contentType)
        editor.apply()
    }

    fun fetchContentType(): String? {
        return prefs.getString(CONTENT_TYPE, null)
    }
}