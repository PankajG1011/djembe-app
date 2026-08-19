package com.djembe.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.djembe.android.ui.DjembeView

class MainActivity : AppCompatActivity() {

    private lateinit var djembeView: DjembeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        djembeView = findViewById(R.id.djembeView)
        // Practice mode, lesson content, and rhythm play-along screens hang off
        // this same DjembeView instance - it's the reusable "instrument" widget.
    }

    override fun onDestroy() {
        super.onDestroy()
        djembeView.release()
    }
}
