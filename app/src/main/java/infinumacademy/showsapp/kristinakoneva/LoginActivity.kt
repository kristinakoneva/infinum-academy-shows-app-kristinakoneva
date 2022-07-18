package infinumacademy.showsapp.kristinakoneva

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import infinumacademy.showsapp.kristinakoneva.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}