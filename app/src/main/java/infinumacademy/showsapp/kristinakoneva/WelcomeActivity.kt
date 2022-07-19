package infinumacademy.showsapp.kristinakoneva

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import infinumacademy.showsapp.kristinakoneva.databinding.ActivityLoginBinding
import infinumacademy.showsapp.kristinakoneva.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)

        val username = intent.extras?.getString("USERNAME")
        binding.tvWelcomeText.text = "Welcome, $username"

        setContentView(binding.root)
    }
}