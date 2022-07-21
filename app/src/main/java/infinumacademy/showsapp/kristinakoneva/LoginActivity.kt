package infinumacademy.showsapp.kristinakoneva

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MultiAutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import infinumacademy.showsapp.kristinakoneva.databinding.ActivityLoginBinding



class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val emailLiveData = MutableLiveData<String>()
    private val passwordLiveData = MutableLiveData<String>()
    private val isValidLiveData = MediatorLiveData<Boolean>().apply {
        this.value = false

        addSource(emailLiveData){ email->
            val password = passwordLiveData.value
            this.value = validateLoginForm(email,password)
        }
        addSource(passwordLiveData){ password->
            val email = emailLiveData.value
            this.value = validateLoginForm(email,password)
        }
    }
    companion object{
        const val MIN_CHARS_FOR_PASSWORD = 6
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.tilEmail.editText?.doOnTextChanged{ text, _, _, _ ->
            emailLiveData.value = text?.toString()
        }

        binding.tilPassword.editText?.doOnTextChanged{ text, _, _, _ ->
            passwordLiveData.value = text?.toString()
        }

        isValidLiveData.observe(this){ isValid ->
            binding.btnLogin.isEnabled = isValid
        }



        binding.btnLogin.setOnClickListener{
            val intent = ShowsActivity.buildIntent(this)
            intent.putExtra("USERNAME",extractUsername())
            startActivity(intent)
        }

    }


    private fun extractUsername(): String{
        val email = binding.etEmail.text.toString()
        val parts = email.split("@")
        val username = parts[0]
        return username
    }

    private fun validateLoginForm(email: String?, password: String?) : Boolean{
        val isValidEmail = email!=null && email.isNotBlank() && email.matches("^[a-z][a-z0-9\\.\\_]*@[a-z]+\\.[a-z]+".toRegex())
        val isValidPassword = password!=null && password.isNotBlank() && password.length >= MIN_CHARS_FOR_PASSWORD

        setEmailError(isValidEmail)
        setPasswordError(isValidPassword)

        return isValidEmail && isValidPassword
    }

    private fun setEmailError(isValidEmail: Boolean){
        if(!isValidEmail){
            binding.etEmail.error = getString(R.string.invalid_email_error_message)
        }
    }

    private fun setPasswordError(isValidPassword: Boolean){
        if(!isValidPassword){
            binding.etPassword.error = getString(R.string.invalid_password_error_message)
        }
    }

}