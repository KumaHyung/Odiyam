package com.soapclient.place.view.account

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import com.soapclient.place.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.soapclient.place.data.Event
import com.soapclient.place.databinding.FragmentLoginBinding
import com.soapclient.place.ext.addFragment
import com.soapclient.place.ext.replaceFragment
import com.soapclient.place.ext.showToastLong
import com.soapclient.place.ext.showToastShort
import com.soapclient.place.view.intro.PermissionCheckFragment
import com.soapclient.place.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        context ?: return binding.root
        setHasOptionsMenu(false)

        initLoginButton()
        initLoginRequestState()
        initCreateAccountButton()

        return binding.root
    }

    private fun initCreateAccountButton() {
        binding.createAccount.setOnClickListener {
            moveToCreateAccountFragment()
        }
    }

    private fun initLoginButton() {
        binding.login.setOnClickListener {
            // TODO: Validator 만들기
            with (binding) {
                if (id.text.isNotEmpty() && password.text.isNotEmpty()) {
                    viewModel.loginWithEmailAndPassword(id.text.toString(), password.text.toString())
                } else if (id.text.isEmpty()) {
                    showToastShort(getString(R.string.empty_id))
                } else if (password.text.isEmpty()) {
                    showToastShort(getString(R.string.empty_password))
                }
            }
        }
    }

    private fun initLoginRequestState() {
        viewModel.loginRequestEvent.asLiveData().observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it is Event.Loading
            binding.login.isEnabled = it !is Event.Loading

            when (it) {
                is Event.Loading -> {
                }
                is Event.Success<*> -> {
                    moveToPermissionCheckFragment()
                }
                is Event.Fail<*> -> {
                    showToastLong(getString(R.string.login_fail))
                    when (it.error) {
                        is String -> {
                        }
                    }
                }
            }
        }
    }

    private fun moveToPermissionCheckFragment() {
        replaceFragment(R.id.fragment_container, PermissionCheckFragment())
    }

    private fun moveToCreateAccountFragment() {
        addFragment(R.id.fragment_container, CreateAccountFragment())
    }
}