package com.example.multithreads.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock.sleep
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.multithreads.R
import com.example.multithreads.databinding.FragmentItemListBinding
import com.example.multithreads.services.NumbersAdapter
import io.reactivex.Observable.interval
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class NumbersFragment : Fragment() {

    private lateinit var binding: FragmentItemListBinding
    private var numbersAdapter = NumbersAdapter()
    private val randomNumber = MutableLiveData<Int>()
    private var currentList = mutableListOf<Int>()
    private val stateFlow = MutableStateFlow(0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemListBinding.bind(
            inflater.inflate(
                R.layout.fragment_item_list,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.adapter = numbersAdapter

//        startThread()
//        startCoroutineScope()
        startRx()
    }

    private fun startCoroutineScope() {
        lifecycleScope.launch {
            for (i in 1..10) {
                val number = (1..10).random()
                delay(1000)
                stateFlow.value = number
            }
        }

        lifecycleScope.launch {
            stateFlow.collect {
                numbersAdapter.submitList(currentList + it)
                currentList.add(it)
            }
        }
    }

    private fun startThread() {
        Thread {
            for (i in 1..10) {
                val number = (1..10).random()
                sleep(1000)
                randomNumber.postValue(number)
            }
        }.start()

        randomNumber.observe(viewLifecycleOwner, {
            it.let {
                numbersAdapter.submitList(currentList + it)
                currentList.add(it)
            }
        })
    }

    @SuppressLint("CheckResult")
    private fun startRx() {
        numbersAdapter.submitList(currentList)

        interval(1000, TimeUnit.MILLISECONDS)
            .timeInterval()
            .subscribe {
                if (it.value() < 10) {
                    val number = (1..10).random()
                    numbersAdapter.submitList(currentList + number)
                    currentList.add(number)
                }
            }
    }
}
