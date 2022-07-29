package infinumacademy.showsapp.kristinakoneva

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import model.RegisterRequest
import model.RegisterResponse
import networking.ApiModule
import networking.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val registrationResultLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    fun getRegistrationResultLiveData(): LiveData<Boolean> {
        return registrationResultLiveData
    }

    fun onRegisterButtonClicked(email: String, password: String, sessionManager: SessionManager) {
        val registerRequest = RegisterRequest(
            email = email,
            password = password,
            passwordConfirmation = password
        )
        ApiModule.retrofit.register(registerRequest)
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    registrationResultLiveData.value = response.isSuccessful

                    val token = response.headers()["access-token"].toString()
                    if (response.isSuccessful) {
                        sessionManager.saveAuthToken(token)
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    registrationResultLiveData.value = false
                }
            })


    }
}