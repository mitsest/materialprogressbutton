package com.mitsest.materialprogressbuttonexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.mitsest.materialprogressbutton.MaterialProgressButton
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<MaterialProgressButton>(R.id.fetch_data_button).setOnClickListener {
            (it as MaterialProgressButton).setShowProgress(true)
            Handler(Looper.getMainLooper()).postDelayed({ it.setShowProgress(false) }, 2500)
        }
    }
}