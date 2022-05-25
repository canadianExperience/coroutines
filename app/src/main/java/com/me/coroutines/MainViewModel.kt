package com.me.coroutines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val p1Flow by lazy { MutableStateFlow(0) }
    private val p2Flow by lazy { MutableStateFlow(0) }
    private val p3Flow by lazy { MutableStateFlow(0) }


    fun startParallelTasks() = viewModelScope.launch {

    }
}