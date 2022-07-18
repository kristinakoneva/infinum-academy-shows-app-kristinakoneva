package infinumacademy.showsapp.kristinakoneva

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import infinumacademy.showsapp.kristinakoneva.databinding.ActivityShowDetailsBinding
import model.Show


class ShowDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowDetailsBinding

    companion object{
        fun buildIntent(activity: Activity): Intent {
            return Intent(activity,ShowDetailsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val show = getIntent ().getExtras()?.getParcelable<Show>("SHOW") as Show
        binding.showName.text = show.name
        binding.showDesc.text = show.description
        binding.showImg.setImageResource(show.imageResourceId)

        binding.btnGoBack.setOnClickListener{
            val intent = ShowsActivity.buildIntent(this)
            startActivity(intent)
        }




    }
}