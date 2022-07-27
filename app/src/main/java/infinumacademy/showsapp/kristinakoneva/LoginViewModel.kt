package infinumacademy.showsapp.kristinakoneva

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import model.LoginRequest
import model.LoginResponse
import networking.ApiModule
import networking.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel: ViewModel() {
    private val loginResultLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun getLoginResultLiveData(): LiveData<Boolean>{
        return loginResultLiveData
    }

    fun onLoginButtonClicked(email: String, password: String, sessionManager: SessionManager){
        val loginRequest = LoginRequest(
            email = email,
            password = password
        )
        ApiModule.retrofit.login(loginRequest).enqueue(object: Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                loginResultLiveData.value = response.isSuccessful

                val token = response.headers()["access-token"].toString()
                if(response.isSuccessful){
                    sessionManager.saveAuthToken(token)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResultLiveData.value = false
            }

        })
    }
}