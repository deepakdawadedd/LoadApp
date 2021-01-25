package com.udacity.nanodegree.loadapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.nanodegree.loadapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    companion object {
        private const val DOWNLOAD_FILE = "download_file"
        fun getStartIntent(context: Context, downloadedFile: String): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(DOWNLOAD_FILE, downloadedFile)
            }
        }
    }

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        setSupportActionBar(binding.detailsToolbar)
        if (intent.hasExtra(DOWNLOAD_FILE)) {
            binding.detailsContentLayout.contentDetailsFileName.text =
                intent.getStringExtra(DOWNLOAD_FILE)
            binding.detailsContentLayout.contentDetailsStatus.text = "Success"
        }

        binding.detailsContentLayout.contentDetailOkButton.setOnClickListener {

            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }

    }

}
