package com.icmen.flows

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.icmen.flows.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    /**
     * To see the difference between observables:
     *  Change device orientation after clicking buttons
     *  Click multiple times to buttons
     */
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initObservers()
    }

    private fun initViews() {
        binding.apply {
            liveDataButton.setOnClickListener { viewModel.triggerLiveData() }
            stateFlowButton.setOnClickListener { viewModel.triggerStateFlow() }
            flowButton.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.triggerFlow().collectLatest {
                        flowTextView.text = it
                    }
                }
            }
            sharedFlowButton.setOnClickListener { viewModel.triggerSharedFlow() }
        }
    }

    private fun initObservers() {
        viewModel.liveData.observe(this) {
            Log.i("mstf", "liveData state changed: $it")

            binding.liveDataTextView.text = it
        }
        lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collectLatest {
                Log.i("mstf", "stateFlow state changed: $it")
                binding.stateFlowTextView.text = it
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.sharedFlow.collectLatest {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
