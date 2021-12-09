package com.example.multithreads.view

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
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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
    private val disposables = CompositeDisposable()
    private var thread: Thread? = null

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

        startThread()
//        startCoroutineScope()
//        startRx()
    }

    private fun startCoroutineScope() {
        val f = flow {
            for (i in 0 until 10) {
                val number = Random.nextInt(20)
                delay(1000)
                this.emit(number)
            }
        }.flowOn(Dispatchers.IO)

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
        thread = Thread {
            for (i in 0 until 10) {
                val number = Random.nextInt(20)
                sleep(1000)
                randomNumber.postValue(number)
            }
        }
        thread?.start()
    }

    private fun startRx() {
        val disposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .take(10)
            .subscribeOn(Schedulers.io())
            .map {
                Random.nextInt(20)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                numbersAdapter.submitList(numbersAdapter.currentList + it)
            }, {
                Log.d("test", "$it")
            })
        disposables.add(disposable)
    }

    override fun onStop() {
        disposables.dispose()
        thread?.interrupt()
        super.onStop()
    }
}
