package infinumacademy.showsapp.kristinakoneva

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import infinumacademy.showsapp.kristinakoneva.databinding.FragmentRegisterBinding
import networking.ApiModule
import networking.SessionManager

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null

    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var sessionManager: SessionManager

    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiModule.initRetrofit(requireContext())
        sessionManager = SessionManager(requireContext())

        sharedPreferences = requireContext().getSharedPreferences(Constants.SHOWS_APP, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.getRegistrationResultLiveData().observe(viewLifecycleOwner) { registrationSuccessful ->
            if (registrationSuccessful) {
                val directions = RegisterFragmentDirections.toLoginFragment(comingFromRegister = true)
                findNavController().navigate(directions)
            } else {
                Toast.makeText(requireContext(), getString(R.string.unsuccessful_registration_msg), Toast.LENGTH_SHORT).show()
            }
        }
        initListeners()
    }

    private fun initListeners() {
        binding.btnRegister.setOnClickListener {
            viewModel.onRegisterButtonClicked(
                email = binding.etEmail.text.toString(),
                password = binding.etPassword.text.toString(),
                sessionManager = sessionManager
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}