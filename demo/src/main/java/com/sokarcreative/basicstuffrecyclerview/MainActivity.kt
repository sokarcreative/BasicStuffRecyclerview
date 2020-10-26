package com.sokarcreative.basicstuffrecyclerview

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.sokarcreative.library.divider.LinearDividersListener
import com.sokarcreative.library.divider.LinearItemDecoration
import com.sokarcreative.library.stickyheader.LinearStickyHeadersListener
import com.sokarcreative.library.stickyheader.StickyHeaderLinearItemDecoration
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        with(navigationView.menu) {
            val isDividerFeatureEnabled = mainViewModel.isDividerFeatureEnabledLiveData().value!!
            with(findItem(R.id.blankMenu)){
                isVisible = isDividerFeatureEnabled
            }
            with(findItem(R.id.dividersState).actionView.findViewById<Switch>(R.id.switchEnable)) {
                isChecked = isDividerFeatureEnabled
                setOnCheckedChangeListener { _, isChecked ->
                    findItem(R.id.menuDividers).isVisible = isChecked
                    navigationView.menu.findItem(R.id.blankMenu).isVisible = isChecked
                    mainViewModel.setIsDividerFeatureEnabled(isChecked)
                }
            }
            with(findItem(R.id.menuDividers)) {
                isVisible = isDividerFeatureEnabled
                val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    with(navigationView.menu.findItem(R.id.menuDividers).subMenu) {
                        val isFirstLastDecorationEnabled = findItem(R.id.firstLast).actionView.findViewById<Switch>(R.id.switchEnable).isChecked
                        val isFirstDividerDecorationEnabled = findItem(R.id.beforeFirst).actionView.findViewById<Switch>(R.id.switchEnable).isChecked
                        val isDividerDecorationEnabled = findItem(R.id.between).actionView.findViewById<Switch>(R.id.switchEnable).isChecked
                        val isLastDividerDecorationEnabled = findItem(R.id.afterLast).actionView.findViewById<Switch>(R.id.switchEnable).isChecked
                        mainViewModel.setDividersEnabled(MainViewModel.DividersEnabled(isFirstLastDecorationEnabled = isFirstLastDecorationEnabled, isFirstDividerDecorationEnabled = isFirstDividerDecorationEnabled, isDividerDecorationEnabled = isDividerDecorationEnabled, isLastDividerDecorationEnabled = isLastDividerDecorationEnabled))
                    }
                }
                val dividersEnabled = mainViewModel.getDividersEnabledLiveData().value!!
                with(subMenu.findItem(R.id.firstLast).actionView.findViewById<Switch>(R.id.switchEnable)) {
                    isChecked = dividersEnabled.isFirstLastDecorationEnabled
                    setOnCheckedChangeListener(onCheckedChangeListener)
                }
                with(subMenu.findItem(R.id.beforeFirst).actionView.findViewById<Switch>(R.id.switchEnable)) {
                    isChecked = dividersEnabled.isFirstDividerDecorationEnabled
                    setOnCheckedChangeListener(onCheckedChangeListener)
                }
                with(subMenu.findItem(R.id.between).actionView.findViewById<Switch>(R.id.switchEnable)) {
                    isChecked = dividersEnabled.isDividerDecorationEnabled
                    setOnCheckedChangeListener(onCheckedChangeListener)
                }
                with(subMenu.findItem(R.id.afterLast).actionView.findViewById<Switch>(R.id.switchEnable)) {
                    isChecked = dividersEnabled.isLastDividerDecorationEnabled
                    setOnCheckedChangeListener(onCheckedChangeListener)
                }
            }
            val isStickyHeaderFeatureEnabled = mainViewModel.isStickyHeaderFeatureEnabledLiveData().value!!
            with(findItem(R.id.stickyHeaderState).actionView.findViewById<Switch>(R.id.switchEnable)) {
                isChecked = isStickyHeaderFeatureEnabled
                setOnCheckedChangeListener { _, isChecked ->
                    mainViewModel.setIsStickyHeaderFeatureEnabledLiveData(isChecked)
                    findItem(R.id.menuStickyHeader).isVisible = isChecked
                }
            }
            with(findItem(R.id.menuStickyHeader)) {
                isVisible = isStickyHeaderFeatureEnabled
                val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    with(navigationView.menu.findItem(R.id.menuStickyHeader).subMenu) {
                        val isHeaderEnabled = findItem(R.id.headerStickyHeader).actionView.findViewById<Switch>(R.id.switchEnable).isChecked
                        val isCategoryEnabled = findItem(R.id.categoryStickyHeader).actionView.findViewById<Switch>(R.id.switchEnable).isChecked
                        val isMovieEnabled = findItem(R.id.movieStickyHeader).actionView.findViewById<Switch>(R.id.switchEnable).isChecked
                        mainViewModel.setStickyHeadersEnabled(Triple(isHeaderEnabled, isCategoryEnabled, isMovieEnabled))
                    }
                }
                val stickyHeadersEnabled = mainViewModel.getStickyHeadersEnabledLiveData().value!!
                with(subMenu.findItem(R.id.headerStickyHeader).actionView.findViewById<Switch>(R.id.switchEnable)) {
                    isChecked = stickyHeadersEnabled.first
                    setOnCheckedChangeListener(onCheckedChangeListener)
                }
                with(subMenu.findItem(R.id.categoryStickyHeader).actionView.findViewById<Switch>(R.id.switchEnable)) {
                    isChecked = stickyHeadersEnabled.second
                    setOnCheckedChangeListener(onCheckedChangeListener)
                }
                with(subMenu.findItem(R.id.movieStickyHeader).actionView.findViewById<Switch>(R.id.switchEnable)) {
                    isChecked = stickyHeadersEnabled.third
                    setOnCheckedChangeListener(onCheckedChangeListener)
                }
            }
        }

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_menu)!!.apply {
                colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(Color.WHITE, BlendModeCompat.SRC_ATOP)
            })
        }

        drawerLayout.setOnClickListener {
            drawerLayout.openDrawer(drawerLayout)
        }

        recyclerView.adapter = DemoAdapter(
                context = this,
                addOrRemove = mainViewModel::addOrRemove,
                addOrRemoveAllMovies = mainViewModel::addOrRemoveAllMovies,
                scrollToPosition = { position ->
                    recyclerView.scrollToPosition(position)
                },
                stickyHeadersEnabled = mainViewModel.getStickyHeadersEnabledLiveData().value!!,
                dividersEnabled = mainViewModel.getDividersEnabledLiveData().value!!
        )

        recyclerView.itemAnimator.takeIf { it is SimpleItemAnimator }?.let {
            (it as SimpleItemAnimator).supportsChangeAnimations = false
        }

        mainViewModel.getItemsLiveData().observe(this, Observer {
            (recyclerView.adapter as DemoAdapter).refresh(it)
            recyclerView.invalidateItemDecorations()
        })

        mainViewModel.isDividerFeatureEnabledLiveData().observe(this, Observer { isDividerFeatureEnabled ->
            recyclerView.itemDecorations().filterIsInstance<LinearItemDecoration>().forEach {
                recyclerView.removeItemDecoration(it)
            }
            if (isDividerFeatureEnabled) {
                recyclerView.addItemDecoration(LinearItemDecoration(recyclerView.adapter as LinearDividersListener))
            }
            recyclerView.invalidateItemDecorations()
        })

        mainViewModel.isStickyHeaderFeatureEnabledLiveData().observe(this, Observer { isStickyHeaderFeatureEnabled ->
            recyclerView.itemDecorations().filterIsInstance<StickyHeaderLinearItemDecoration>().forEach {
                recyclerView.removeOnItemTouchListener(it)
                recyclerView.removeItemDecoration(it)
            }
            if (isStickyHeaderFeatureEnabled) {
                recyclerView.addItemDecoration(StickyHeaderLinearItemDecoration(recyclerView.adapter as LinearStickyHeadersListener).also {
                    recyclerView.addOnItemTouchListener(it)
                })
            }
            recyclerView.invalidateItemDecorations()
        })

        mainViewModel.getStickyHeadersEnabledLiveData().observe(this, Observer {
            (recyclerView.adapter as DemoAdapter).stickyHeadersEnabled = it
            recyclerView.invalidateItemDecorations()
        })
        mainViewModel.getDividersEnabledLiveData().observe(this, Observer {
            (recyclerView.adapter as DemoAdapter).dividersEnabled = it
            recyclerView.invalidateItemDecorations()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout?.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun RecyclerView.itemDecorations(): Iterable<RecyclerView.ItemDecoration> {
        val itemDecorations = arrayListOf<RecyclerView.ItemDecoration>()
        for (i in 0 until itemDecorationCount) {
            itemDecorations.add(getItemDecorationAt(i))
        }
        return itemDecorations
    }


}
