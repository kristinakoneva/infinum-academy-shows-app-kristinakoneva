package infinumacademy.showsapp.kristinakoneva

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import infinumacademy.showsapp.kristinakoneva.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModels<LoginViewModel>()

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences(Constants.SHOWS_APP, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkRememberMe()
        observeLiveDataForValidation()
        initListeners()
    }

    private fun checkRememberMe() {
        binding.cbRememberMe.isChecked = sharedPreferences.getBoolean(Constants.REMEMBER_ME, false)
        if (binding.cbRememberMe.isChecked) {
            val directions = LoginFragmentDirections.toShowsNavGraph(getString(R.string.username_placeholder))
            findNavController().navigate(directions)
        }
    }

    private fun saveRememberMe() {
        sharedPreferences.edit {
            putBoolean(Constants.REMEMBER_ME, binding.cbRememberMe.isChecked)
        }
    }

    private fun saveUsernameAndEmail(username: String, email: String) {
        sharedPreferences.edit {
            putString(Constants.USERNAME, username)
            putString(Constants.EMAIL, email)
        }
    }

    private fun observeLiveDataForValidation() {
        binding.tilEmail.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onEmailTextChanged(text?.toString())
            setEmailError()
        }

        binding.tilPassword.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onPasswordTextChanges(text?.toString())
            setPasswordError()
        }

        viewModel.isValidLiveData.observe(viewLifecycleOwner) { isValid ->
            binding.btnLogin.isEnabled = isValid
        }
    }

    private fun initListeners() {
        binding.btnLogin.setOnClickListener {
            saveRememberMe()
            val username = extractUsername()
            val email = binding.etEmail.text.toString()
            saveUsernameAndEmail(username, email)
            val directions = LoginFragmentDirections.toShowsNavGraph(username)
            findNavController().navigate(directions)
        }
    }

    private fun extractUsername(): String {
        val email = binding.etEmail.text.toString()
        val parts = email.split("@")
        val username = parts[0]
        return username
    }

    private fun setEmailError() {
        viewModel.isValidEmail.observe(viewLifecycleOwner) { isValid ->
            if (!isValid) {
                binding.etEmail.error = getString(R.string.invalid_email_error_message)
            }
        }

    }

    private fun setPasswordError() {
        viewModel.isValidPassword.observe(viewLifecycleOwner) { isValid ->
            if (!isValid) {
                binding.etPassword.error = getString(R.string.invalid_password_error_message)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}