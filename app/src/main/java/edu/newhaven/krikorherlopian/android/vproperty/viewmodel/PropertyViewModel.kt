package edu.newhaven.krikorherlopian.android.vproperty.viewmodel

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PropertyViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                value = "this is prop"
            }
        }, 10000)
    }
    val text: LiveData<String> = _text
}