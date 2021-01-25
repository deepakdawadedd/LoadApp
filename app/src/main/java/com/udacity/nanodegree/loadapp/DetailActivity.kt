package com.udacity.nanodegree.loadapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    companion object {
        private const val DOWNLOAD_FILE = "download_file"
        fun getStartIntent(context: Context, downloadedFile: String): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(DOWNLOAD_FILE, downloadedFile)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
    }

}
