package infinumacademy.showsapp.kristinakoneva

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import infinumacademy.showsapp.kristinakoneva.databinding.ActivityLoginBinding
import infinumacademy.showsapp.kristinakoneva.databinding.ActivityShowsBinding
import model.Show

class ShowsActivity : AppCompatActivity() {

    private val showsList = listOf(
        Show(0,"The Office","The Office is an American mockumentary sitcom television series that depicts " +
                "the everyday work lives of office employees in the Scranton, Pennsylvania, branch of the fictional " +
                "Dunder Mifflin Paper Company. It aired on NBC from March 24, 2005, to May 16, 2013, lasting a total of nine seasons.",R.drawable.ic_office),
        Show(1,"Stranger Things","The Office is an American mockumentary sitcom television series that depicts \" +\n" +
                "                \"the everyday work lives of office employees in the Scranton, Pennsylvania, branch of the fictional \" +\n" +
                "                \"Dunder Mifflin Paper Company. It aired on NBC from March 24, 2005, to May 16, 2013, lasting a total of nine seasons.",R.drawable.ic_stranger_things),
        Show(2,"Krv nije voda","The Office is an American mockumentary sitcom television series that depicts \" +\n" +
                "                \"the everyday work lives of office employees in the Scranton, Pennsylvania, branch of the fictional \" +\n" +
                "                \"Dunder Mifflin Paper Company. It aired on NBC from March 24, 2005, to May 16, 2013, lasting a total of nine seasons.",R.drawable.ic_krv_nije_voda)
    )

    private lateinit var binding: ActivityShowsBinding
    private lateinit var adapter: ShowsAdapter

    companion object{
        fun buildIntent(activity: Activity): Intent {
            return Intent(activity,ShowsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initShowsRecycler()

    }

    private fun initShowsRecycler(){

        adapter = ShowsAdapter(showsList){ show ->
            showDetailsAbout(show)
        }

        binding.showsRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        binding.showsRecycler.adapter = adapter
    }

    private fun showDetailsAbout(show: Show){
        val intent = ShowDetailsActivity.buildIntent(this)
        intent.putExtra("SHOW",show)
        startActivity(intent)
    }
}