package com.me.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.me.coroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.parallelRunBlockingBtn.setOnClickListener {
            mainViewModel.parallelInRunBlocking()
        }

        binding.parallelLaunchBtn.setOnClickListener {
            mainViewModel.parallelInLaunch { firstWord, secondWord ->
                //Get call back from parallel call
                binding.parallelLaunch.text = "$firstWord $secondWord"
            }
        }

        binding.sequentialBtn.setOnClickListener {
            mainViewModel.sequential()
        }

        binding.joinChildrenBtn.setOnClickListener {
            mainViewModel.joinChildrenWords()
        }

        binding.oddEvenBtn.setOnClickListener {
            mainViewModel.startOddEvenCoroutines()
        }

        this.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    mainViewModel.parallelRunBlocking.collect{
                        binding.parallelRunBlocking.text = it
                    }
                }

                launch {
                    mainViewModel.sequential.collect{
                        binding.sequential.text = it
                    }
                }

                launch {
                    mainViewModel.joinChildren.collect{
                        binding.joinChildren.text = it
                    }
                }

                launch {
                    mainViewModel.oddNumber.collect{
                        binding.odd.text = it
                    }
                }

                launch {
                    mainViewModel.evenNumber.collect{
                        binding.even.text = it
                    }
                }
            }
        }


    }
}