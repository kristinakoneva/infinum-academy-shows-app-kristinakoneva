package infinumacademy.showsapp.kristinakoneva

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import infinumacademy.showsapp.kristinakoneva.databinding.ActivityLoginBinding
import infinumacademy.showsapp.kristinakoneva.databinding.ActivityShowsBinding

class ShowsActivity : AppCompatActivity() {
    lateinit var binding: ActivityShowsBinding

    companion object{
        fun buildIntent(activity: Activity): Intent {
            return Intent(activity,ShowsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowsBinding.inflate(layoutInflater)

        setContentView(binding.root)

    }
}