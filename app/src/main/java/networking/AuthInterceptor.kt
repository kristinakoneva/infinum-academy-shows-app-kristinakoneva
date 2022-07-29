package networking

import android.content.Context
import infinumacademy.showsapp.kristinakoneva.Constants
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        sessionManager.fetchAuthToken()?.let {
            requestBuilder.addHeader(Constants.ACCESS_TOKEN, it)
            requestBuilder.addHeader(Constants.TOKEN_TYPE, Constants.BEARER)
        }
        sessionManager.fetchClient()?.let {
            requestBuilder.addHeader(Constants.CLIENT, it)
        }
        sessionManager.fetchExpiry()?.let {
            requestBuilder.addHeader(Constants.EXPIRY, it)
        }
        sessionManager.fetchUID()?.let {
            requestBuilder.addHeader(Constants.UID, it)
        }
        sessionManager.fetchContentType()?.let {
            requestBuilder.addHeader(Constants.CONTENT_TYPE, it)
        }

        return chain.proceed(requestBuilder.build())
    }
}