package com.me.coroutines

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

const val TAG = "MY_TASK"

class MainViewModel: ViewModel() {

    private val parallelRunBlockingFlow by lazy { MutableStateFlow("") }
    val parallelRunBlocking get() = parallelRunBlockingFlow.asStateFlow()

    private val sequentialFlow by lazy { MutableStateFlow("") }
    val sequential get() = sequentialFlow.asStateFlow()

    private val joinChildrenFlow by lazy { MutableStateFlow("") }
    val joinChildren get() = joinChildrenFlow.asStateFlow()

    init {
       // startCoroutines()
    }

    private fun startCoroutines(){
        multipleThreadTasks()
        Log.d(TAG, "==========================")
        parentChildTasks()
    }


    private fun multipleThreadTasks() = viewModelScope.launch {
        launch{
            //main
            Log.d(TAG, "Coroutine 1: scope - $this, thread - ${Thread.currentThread().name}")
        }
        launch(IO) {
            //backGround
            Log.d(TAG, "Coroutine 2: scope - $this, thread - ${Thread.currentThread().name}")
        }

        launch(Default) {
            //backGround
            Log.d(TAG, "Coroutine 3: scope - $this, thread - ${Thread.currentThread().name}")
        }

        launch(Unconfined) {
            //main
            Log.d(TAG, "Coroutine 4: scope - $this, thread - ${Thread.currentThread().name}")
        }
    }

    private fun parentChildTasks() = viewModelScope.launch(IO) {
        launch(coroutineContext){
            //main
            delay(1000)
            Log.d(TAG, "Coroutine 1 child: scope - $this, thread - ${Thread.currentThread().name}")
        }
        launch(coroutineContext) {
            //backGround
            delay(2000)
            Log.d(TAG, "Coroutine 2 child: scope - $this, thread - ${Thread.currentThread().name}")
        }

       launch(coroutineContext) {
            //backGround
            delay(4000)
            Log.d(TAG, "Coroutine 3 child: scope - $this, thread - ${Thread.currentThread().name}")
        }

        launch(coroutineContext) {
            //main
            delay(3000)
            Log.d(TAG, "Coroutine 4 child: scope - $this, thread - ${Thread.currentThread().name}")
        }
    }

    fun parallelInRunBlocking() = runBlocking {
//        val p1: Deferred<String> = async {
//            Log.d(TAG, "Hello, thread - ${Thread.currentThread().name}")
//            "Hello"
//        }
//        val p2: Deferred<String> = async {
//            Log.d(TAG, "World, thread - ${Thread.currentThread().name}")
//            "World"
//        }
//
//        parallelRunBlockingFlow.emit(p1.await() + " " + p2.await())

        //Or
        val job1 = launch {
            delay(1000)

            val str = parallelRunBlocking.value
            parallelRunBlockingFlow.emit("$str Hello")

        }
        val job2 = launch {
            delay(2000)
            val str = parallelRunBlocking.value
            parallelRunBlockingFlow.emit("$str World")
        }

        job1.join()
        job2.join()
    }

    fun parallelInLaunch(result: (firstWord: String, secondWord: String) -> Unit) = viewModelScope.launch(IO) {
        val p1: Deferred<String> = async {
            Log.d(TAG, "Hello, thread - ${Thread.currentThread().name}")
            delay(3000)
            "Hello"
        }
        val p2: Deferred<String> = async {
            Log.d(TAG, "World, thread - ${Thread.currentThread().name}")
            "World"
        }

        result(p1.await(), p2.await())
    }


    fun sequential() = viewModelScope.launch(IO) {
        val firstWord = async {
            getHello()
        }

        val first = firstWord.await()

        val result = async {
            try {
                getHelloWorld(first)
            } catch (e: CancellationException){
                "no result"
            }
        }

        val res = result.await()

        sequentialFlow.emit(res)
    }

    private suspend fun getHello(): String{
        delay(2000)
        return "Hello"
    }

    private suspend fun getHelloWorld(first: String): String{
        delay(3000)
        return "$first World"
    }

    fun joinChildrenWords() {

        viewModelScope.launch(IO) {
            val job = Job()
           val ch1 = launch(job) {
                delay(1000)
               "Hello"

               val str = joinChildren.value
               joinChildrenFlow.emit("$str Hello")
            }

            val ch2 = launch(job) {
                delay(3000)
                "World"
               val str = joinChildren.value
               joinChildrenFlow.emit("$str World")
            }

            val ch3 = launch(job) {
                delay(5000)
                "Done"

                val str = joinChildren.value
                joinChildrenFlow.emit("$str Done")
            }

            job.children.forEach {
                it.join()
            }
        }
    }

    //Suspend functions: sequential, concurrent(parallel) and lazy execution

    private fun startCounter(max: Int, flow: MutableStateFlow<Int>): Int {
        var counter = 0
        while (counter < max){
            counter += 1
            flow.value = counter
        }

       return counter
    }

}