package infinumacademy.showsapp.kristinakoneva

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import infinumacademy.showsapp.kristinakoneva.databinding.ActivityLoginBinding
import infinumacademy.showsapp.kristinakoneva.databinding.ActivityShowsBinding
import model.Show

class ShowsActivity : AppCompatActivity() {

    private val showsList = listOf(
        Show(0,"The Office","The Office is an American mockumentary sitcom television series that depicts " +
                "the everyday work lives of office employees in the Scranton, Pennsylvania, branch of the fictional " +
                "Dunder Mifflin Paper Company. It aired on NBC from March 24, 2005, to May 16, 2013, lasting a total of nine seasons.",R.drawable.ic_office),
        Show(1,"Stranger Things","In 1980s Indiana, a group of young friends witness supernatural forces and secret government exploits. " +
                "As they search for answers, the children unravel a series of extraordinary mysteries.",R.drawable.ic_stranger_things),
        Show(2,"Krv nije voda","Lorem ipsum dolor sit amet. Sit voluptatibus vitae qui quis minus non dignissimos autem! " +
                "Qui cupiditate tempore rem perspiciatis galisum et quia nihil rem consequatur quia aut quia saepe.",R.drawable.ic_krv_nije_voda)
    )
    private var showEmptyState = true

    private lateinit var binding: ActivityShowsBinding
    private lateinit var adapter: ShowsAdapter


    companion object{
        const val USERNAME = "USERNAME"
        const val SHOW = "SHOW"
        fun buildIntent(activity: Activity): Intent {
            return Intent(activity,ShowsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initShowsRecycler()

        initListeners()

    }

    private fun initListeners(){
        binding.btnShowHideEmptyState.setOnClickListener{
            if(showEmptyState){
                showEmptyState = false
                showShows()
            }else{
                showEmptyState = true
                hideShows()
            }
            resetVisibility()
        }
    }

    private fun resetVisibility(){
        binding.showsEmptyState.isVisible = !binding.showsEmptyState.isVisible
        binding.showsRecycler.isVisible = !binding.showsRecycler.isVisible
    }

    private fun showShows(){
        adapter.addAllItems(showsList)
    }
    private fun hideShows(){
        adapter.addAllItems(emptyList())
    }

    private fun initShowsRecycler(){

        adapter = ShowsAdapter(emptyList()){ show ->
            showDetailsAbout(show)
        }

        binding.showsRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        binding.showsRecycler.adapter = adapter
    }

    private fun getUsername(): String? {
        return intent.getStringExtra(USERNAME)
    }

    private fun showDetailsAbout(show: Show){
        val intent = ShowDetailsActivity.buildIntent(this)
        intent.putExtra(SHOW,show)
        val username = getUsername()
        intent.putExtra(USERNAME,username)
        startActivity(intent)
    }
}