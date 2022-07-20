package infinumacademy.showsapp.kristinakoneva

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import infinumacademy.showsapp.kristinakoneva.databinding.FragmentShowDetailsBinding
import infinumacademy.showsapp.kristinakoneva.databinding.FragmentShowsBinding


class ShowsFragment : Fragment() {

    private var _binding:FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private val args by navArgs<ShowsFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding =  FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}