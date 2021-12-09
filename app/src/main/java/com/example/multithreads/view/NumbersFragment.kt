package com.example.multithreads.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock.sleep
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.multithreads.R
import com.example.multithreads.databinding.FragmentItemListBinding
import com.example.multithreads.services.NumbersAdapter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class NumbersFragment : Fragment() {

    private lateinit var binding: FragmentItemListBinding
    private var numbersAdapter = NumbersAdapter()
    private val randomNumber = MutableLiveData<Int>()

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
        val f = flow {
            for (i in 1 until 10) {
                val number = Random.nextInt(20)
                delay(Random.nextLong(2000))
                this.emit(number)
            }
        }.flowOn(Dispatchers.Main)
        lifecycleScope.launch {
            f.collect {
                numbersAdapter.submitList(numbersAdapter.currentList + it)
            }
        }
    }

    private fun startThread() {
        randomNumber.observe(viewLifecycleOwner, {
            it.let {
                numbersAdapter.submitList(numbersAdapter.currentList + it)
            }
        })
        Thread {
            for (i in 1 until 10) {
                val number = Random.nextInt(20)
                sleep(Random.nextLong(2000))
                randomNumber.postValue(number)
            }
        }.start()
    }

    @SuppressLint("CheckResult")
    private fun startRx() {
        Flowable.interval(Random.nextLong(2000), TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it <= 10) {
                    val number = Random.nextInt(20)
                    numbersAdapter.submitList(numbersAdapter.currentList + number)
                }
            }, {
                Log.d("test", "$it")
            })
    }
}
