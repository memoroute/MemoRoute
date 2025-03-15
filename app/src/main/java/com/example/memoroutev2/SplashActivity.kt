package com.example.memoroutev2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.memoroutev2.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_DELAY = 2000L // 2秒延迟

    override fun onCreate(savedInstanceState: Bundle?) {
        // 在setContentView之前禁用系统启动画面
        setTheme(R.style.Theme_Memoroutev2_Splash)
        
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 确保立即显示
        window.decorView.visibility = View.VISIBLE

        // 应用动画效果
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        
        binding.splashLogo.startAnimation(fadeIn)
        binding.appName.startAnimation(slideUp)
        binding.appSlogan.startAnimation(slideUp)

        // 延迟后跳转到主界面
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish() // 结束当前活动，防止返回
            
            // 使用淡入淡出的转场效果
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, SPLASH_DELAY)
    }
} 