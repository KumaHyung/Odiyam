package com.soapclient.place.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.soapclient.place.R
import com.soapclient.place.databinding.ActivityMainBinding
import com.soapclient.place.ext.replaceFragment
import com.soapclient.place.view.account.LoginFragment
import com.soapclient.place.view.intro.SplashFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        replaceFragment(R.id.fragment_container, SplashFragment())
    }
}