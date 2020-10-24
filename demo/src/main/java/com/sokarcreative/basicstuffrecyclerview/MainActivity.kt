package com.sokarcreative.basicstuffrecyclerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.SimpleItemAnimator
import com.sokarcreative.library.divider.LinearItemDecoration
import com.sokarcreative.library.stickyheader.StickyHeaderLinearItemDecoration
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.adapter = DemoAdapter(context = this, addOrRemove = mainViewModel::addOrRemove, addOrRemoveAllMovies = mainViewModel::addOrRemoveAllMovies, scrollToPosition = { position ->
            recyclerView.scrollToPosition(position)
        }).also {
            val linearItemDecoration = LinearItemDecoration(it)
            recyclerView.addItemDecoration(linearItemDecoration)
            recyclerView.addItemDecoration(StickyHeaderLinearItemDecoration(it).also {
                recyclerView.addOnItemTouchListener(it)
            })
        }

        recyclerView.itemAnimator.takeIf { it is SimpleItemAnimator }?.let {
            (it as SimpleItemAnimator).supportsChangeAnimations = false
        }

        mainViewModel.getItemsLiveData().observe(this, Observer {
            (recyclerView.adapter as DemoAdapter).refresh(it)
            recyclerView.invalidateItemDecorations()
        })
    }

}
