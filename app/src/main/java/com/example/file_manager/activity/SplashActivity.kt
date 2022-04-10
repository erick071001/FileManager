package com.example.file_manager.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import com.example.file_manager.R
import com.example.file_manager.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    val topAnim by lazy {AnimationUtils.loadAnimation(this, R.anim.top_animation)}
    val bottomAnim by lazy{ AnimationUtils.loadAnimation(this, R.anim.bottom_animation)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            binding.logo1.animation = topAnim
            binding.logo2.animation = topAnim
            binding.name1.animation = bottomAnim
            binding.name2.animation = bottomAnim
            delay(3000)
            startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
        }
    }
}