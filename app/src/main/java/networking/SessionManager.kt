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

    fun saveSession(token: String, client: String, expiry: String, uid: String, contentType: String) {
        saveAuthToken(token)
        saveClient(client)
        saveExpiry(expiry)
        saveUID(uid)
        saveContentType(contentType)
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, null)
        editor.putString(CLIENT, null)
        editor.putString(EXPIRY, null)
        editor.putString(UID, null)
        editor.putString(CONTENT_TYPE, null)
        editor.apply()
    }

    private fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    private fun saveClient(client: String) {
        val editor = prefs.edit()
        editor.putString(CLIENT, client)
        editor.apply()
    }

    fun fetchClient(): String? {
        return prefs.getString(CLIENT, null)
    }

    private fun saveExpiry(expiry: String) {
        val editor = prefs.edit()
        editor.putString(EXPIRY, expiry)
        editor.apply()
    }

    fun fetchExpiry(): String? {
        return prefs.getString(EXPIRY, null)
    }

    private fun saveUID(uid: String) {
        val editor = prefs.edit()
        editor.putString(UID, uid)
        editor.apply()
    }

    fun fetchUID(): String? {
        return prefs.getString(UID, null)
    }

    private fun saveContentType(contentType: String) {
        val editor = prefs.edit()
        editor.putString(CONTENT_TYPE, contentType)
        editor.apply()
    }

    fun fetchContentType(): String? {
        return prefs.getString(CONTENT_TYPE, null)
    }
}