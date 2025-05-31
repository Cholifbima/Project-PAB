package com.example.pabproject.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter.asStateFlow()

    fun incrementCounter() {
        _counter.value++
    }

    fun decrementCounter() {
        if (_counter.value > 0) {
            _counter.value--
        }
    }
} 