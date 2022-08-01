package infinumacademy.showsapp.kristinakoneva

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import infinumacademy.showsapp.kristinakoneva.databinding.ActivityMainBinding
import networking.ApiModule

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        ApiModule.initRetrofit(this)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}