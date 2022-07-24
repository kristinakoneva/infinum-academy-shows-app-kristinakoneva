package infinumacademy.showsapp.kristinakoneva

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import infinumacademy.showsapp.kristinakoneva.databinding.ActivityMainBinding

const val USERNAME = "USERNAME"
const val REMEMBER_ME = "REMEMBER_ME"
const val EMAIL = "EMAIL"
const val SHOWS_APP = "ShowsApp"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}