package com.example.seekhoanime.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.seekhoanime.R
import com.example.seekhoanime.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.txtTitle.post {
            val paint = binding.txtTitle.paint
            val width = paint.measureText(binding.txtTitle.text.toString())

            val shader = LinearGradient(
                0f, 0f,
                width, binding.txtTitle.textSize,
                intArrayOf(
                    Color.parseColor("#8A49D9"),
                    Color.parseColor("#ED3FC1")
                ),
                null,
                Shader.TileMode.CLAMP
            )

            paint.shader = shader
            binding.txtTitle.invalidate()
        }


        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 200)
    }
}