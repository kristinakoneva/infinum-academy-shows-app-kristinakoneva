package networking

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        sessionManager.fetchAuthToken()?.let {
            requestBuilder.addHeader("access-token", it)
            requestBuilder.addHeader("token-type", "Bearer")
        }
        sessionManager.fetchClient()?.let {
            requestBuilder.addHeader("client", it)
        }
        sessionManager.fetchExpiry()?.let {
            requestBuilder.addHeader("expiry", it)
        }
        sessionManager.fetchUID()?.let {
            requestBuilder.addHeader("uid", it)
        }
        sessionManager.fetchContentType()?.let {
            requestBuilder.addHeader("content-type", it)
        }

        return chain.proceed(requestBuilder.build())
    }
}