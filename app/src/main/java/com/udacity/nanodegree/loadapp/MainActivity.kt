package com.udacity.nanodegree.loadapp

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.nanodegree.loadapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.layoutMainContent.mainViewModel = viewModel
        setSupportActionBar(binding.mainToolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.layoutMainContent.mainContentCustomButton.setOnClickListener {
            download()
        }
        viewModel.checkedId.observe(this) {
            viewModel.selectedURL.value = when (it) {
                R.id.main_content_option_glide -> MainViewModel.GLIDE_URL
                R.id.main_content_option_udacity -> MainViewModel.GITHUB_URL
                R.id.main_content_option_retrofit -> MainViewModel.RETROFIT_URL
                else -> ""

            }
        }

        viewModel.selectedURL.observe(this) {
            viewModel.selectedName.value = when (it) {
                MainViewModel.RETROFIT_URL -> getString(R.string.retrofit_by_square_inc)
                MainViewModel.GLIDE_URL -> getString(R.string.glide_by_bumptech)
                MainViewModel.GITHUB_URL -> getString(R.string.current_repository_by_udacity)
                else -> ""
            }
        }

        createChannel(
            getString(R.string.load_app_channel_id),
            getString(R.string.load_app_channel_name)
        )

    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.channel_description)

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            displayNotification()
        }
    }

    private fun displayNotification() {
        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            "Download item complete",
            this,
            viewModel.selectedName.value ?: ""
        )
    }

    private fun download() {
        if (viewModel.selectedURL.value.isNullOrBlank())
            Toast.makeText(
                this,
                getString(R.string.select_file_to_download_message),
                Toast.LENGTH_SHORT
            ).show()
        else {

            val request =
                DownloadManager.Request(Uri.parse(URL))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }
}