package com.example.file_manager.activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.file_manager.databinding.ActivityHomeBinding



class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}