package infinumacademy.showsapp.kristinakoneva

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import infinumacademy.showsapp.kristinakoneva.databinding.FragmentShowDetailsBinding
import infinumacademy.showsapp.kristinakoneva.databinding.FragmentShowsBinding
import model.Show


class ShowsFragment : Fragment() {

    private var _binding:FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter

    private var showEmptyState = true


    private val showsList = listOf(
        Show(0,"The Office","The Office is an American mockumentary sitcom television series that depicts " +
                "the everyday work lives of office employees in the Scranton, Pennsylvania, branch of the fictional " +
                "Dunder Mifflin Paper Company. It aired on NBC from March 24, 2005, to May 16, 2013, lasting a total of nine seasons.",R.drawable.the_office),
        Show(1,"Stranger Things","In 1980s Indiana, a group of young friends witness supernatural forces and secret government exploits. " +
                "As they search for answers, the children unravel a series of extraordinary mysteries.",R.drawable.stranger_things),
        Show(2,"Krv nije voda","Lorem ipsum dolor sit amet. Sit voluptatibus vitae qui quis minus non dignissimos autem! " +
                "Qui cupiditate tempore rem perspiciatis galisum et quia nihil rem consequatur quia aut quia saepe.",R.drawable.krv_nije_voda)
    )

    private val args by navArgs<ShowsFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding =  FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.btnLogout.setOnClickListener{
            findNavController().navigate(R.id.toLoginFragment)
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

        binding.showsRecycler.layoutManager = LinearLayoutManager(requireContext())

        binding.showsRecycler.adapter = adapter
    }

    private fun showDetailsAbout(show: Show){
        val username = args.username
        val directions = ShowsFragmentDirections.toShowDetailsFragment(username,show)
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}