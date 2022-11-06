package com.example.nasa.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nasa.R
import com.example.nasa.databinding.ActivityMainBinding
import com.example.nasa.ui.picture.PictureOfTheDayFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        //setTheme(R.style.PinkTheme)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PictureOfTheDayFragment.newInstance())
                .commitNow()
        }
    }
}