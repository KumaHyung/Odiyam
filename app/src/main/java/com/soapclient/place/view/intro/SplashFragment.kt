package com.soapclient.place.view.intro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import com.soapclient.place.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.soapclient.place.databinding.FragmentSplashBinding
import com.soapclient.place.ext.replaceFragment
import com.soapclient.place.view.account.LoginFragment
import com.soapclient.place.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        context ?: return binding.root
        setHasOptionsMenu(false)

        Handler(Looper.getMainLooper()).postDelayed({
            if (viewModel.isNeedLogin()) {
                moveToLoginFragment()
            } else {
                moveToPermissionCheckFragment()
            }
        }, 1000)

        return binding.root
    }

    private fun moveToLoginFragment() {
        replaceFragment(R.id.fragment_container, LoginFragment())
    }

    private fun moveToPermissionCheckFragment() {
        replaceFragment(R.id.fragment_container, PermissionCheckFragment())
    }
}