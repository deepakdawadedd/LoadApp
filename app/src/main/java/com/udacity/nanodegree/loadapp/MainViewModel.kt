package com.udacity.nanodegree.loadapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel


/**
 * Created by @author Deepak Dawade on 1/21/2021 at 1:57 AM.
 * Copyright (c) 2021 deepak.dawade.dd1@gmail.com All rights reserved.
 *
 */
class MainViewModel : ViewModel() {
    companion object {
        private const val GITHUB_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/v4.11.0.zip"

        private const val RETROFIT_URL =
            "https://search.maven.org/remote_content?g=com.squareup.retrofit2&a=retrofit&v=LATEST"
    }

    val checkedId: MutableLiveData<Int> = MutableLiveData()
    val selectedURL: LiveData<String> = Transformations.map(checkedId) {
        when (it) {
            R.id.main_content_option_glide -> GLIDE_URL
            R.id.main_content_option_udacity -> GITHUB_URL
            R.id.main_content_option_retrofit -> RETROFIT_URL
            else -> ""
        }
    }
}
