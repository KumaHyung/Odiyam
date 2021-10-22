package com.soapclient.place.view.account

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import com.soapclient.place.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.soapclient.place.data.Event
import com.soapclient.place.databinding.FragmentCreateAccountBinding
import com.soapclient.place.ext.showToastLong
import com.soapclient.place.ext.showToastShort
import com.soapclient.place.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountFragment : Fragment() {
    private lateinit var binding: FragmentCreateAccountBinding

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateAccountBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        context ?: return binding.root
        setHasOptionsMenu(false)

        initCreateButton()
        initCreateRequestState()

        return binding.root
    }

    private fun initCreateButton() {
        binding.create.setOnClickListener {
            // TODO: Validator 만들기
            with (binding) {
                if (emailAddress.text.isNotEmpty()
                    && password.text.isNotEmpty()
                    && passwordConfirm.text.isNotEmpty()
                    && password.text == passwordConfirm.text) {
                    viewModel.createUserWithEmailAndPassword(
                        emailAddress.text.toString(),
                        password.text.toString()
                    )
                } else if (emailAddress.text.isEmpty()) {
                    showToastShort(getString(R.string.empty_id))
                } else if (password.text.isEmpty()) {
                    showToastShort(getString(R.string.empty_password))
                } else if (passwordConfirm.text.isEmpty()) {
                    showToastShort(getString(R.string.empty_password_confirm))
                } else if (password.text != passwordConfirm.text) {
                    showToastShort(getString(R.string.password_not_equal))
                }
            }
        }
    }

    private fun initCreateRequestState() {
        viewModel.createUserRequestEvent.asLiveData().observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it is Event.Loading
            when (it) {
                is Event.Loading -> {
                }
                is Event.Success<*> -> {
                }
                is Event.Fail<*> -> {
                    showToastLong(getString(R.string.create_account_fail))
                    when (it.error) {
                        is String -> {
                        }
                    }
                }
            }
        }
    }
}
