package com.example.trafa.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RiwayatViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is RiwayatFragment"
    }
    val text: LiveData<String> = _text
}