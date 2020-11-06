package com.sokarcreative.demo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.SimpleItemAnimator
import com.sokarcreative.basicstuffrecyclerview.divider.LinearDividersListener
import com.sokarcreative.basicstuffrecyclerview.divider.LinearItemDecoration
import com.sokarcreative.basicstuffrecyclerview.stickyheader.LinearStickyHeadersListener
import com.sokarcreative.basicstuffrecyclerview.stickyheader.StickyHeaderLinearItemDecoration
import com.sokarcreative.demo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding.navigationView.menu) {
            val isDividerFeatureEnabled = mainViewModel.isDividerFeatureEnabledLiveData().value!!
            with(findItem(R.id.blankMenu)){
                isVisible = isDividerFeatureEnabled
            }
            with(findItem(R.id.dividersState).actionView.findViewById<SwitchCompat>(R.id.switchEnable)) {
                isChecked = isDividerFeatureEnabled
                setOnCheckedChangeListener { _, isChecked ->
                    findItem(R.id.menuDividers).isVisible = isChecked
                    binding.navigationView.menu.findItem(R.id.blankMenu).isVisible = isChecked
                    mainViewModel.setIsDividerFeatureEnabled(isChecked)
                }
            }
            with(findItem(R.id.menuDividers)) {
                isVisible = isDividerFeatureEnabled
                val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    with(binding.navigationView.menu.findItem(R.id.menuDividers).subMenu) {
                        val isFirstLastDecorationEnabled = findItem(R.id.firstLast).actionView.findViewById<SwitchCompat>(R.id.switchEnable).isChecked
                        val isFirstDividerDecorationEnabled = findItem(R.id.beforeFirst).actionView.findViewById<SwitchCompat>(R.id.switchEnable).isChecked
                        val isDividerDecorationEnabled = findItem(R.id.between).actionView.findViewById<SwitchCompat>(R.id.switchEnable).isChecked
                        val isLastDividerDecorationEnabled = findItem(R.id.afterLast).actionView.findViewById<SwitchCompat>(R.id.switchEnable).isChecked
                        mainViewModel.setDividersEnabled(MainViewModel.DividersEnabled(isFirstLastDecorationEnabled = isFirstLastDecorationEnabled, isFirstDividerDecorationEnabled = isFirstDividerDecorationEnabled, isDividerDecorationEnabled = isDividerDecorationEnabled, isLastDividerDecorationEnabled = isLastDividerDecorationEnabled))
                    }
                }
                val dividersEnabled = mainViewModel.getDividersEnabledLiveData().value!!
                with(subMenu.findItem(R.id.firstLast).actionView.findViewById<SwitchCompat>(R.id.switchEnable)) {
                    isChecked = dividersEnabled.isFirstLastDecorationEnabled
                    setOnCheckedChangeListener(onCheckedChangeListener)
                }
                with(subMenu.findItem(R.id.beforeFirst).actionView.findViewById<SwitchCompat>(R.id.switchEnable)) {
                    isChecked = dividersEnabled.isFirstDividerDecorationEnabled
                    setOnCheckedChangeListener(onCheckedChangeListener)
                }
                with(subMenu.findItem(R.id.between).actionView.findViewById<SwitchCompat>(R.id.switchEnable)) {
                    isChecked = dividersEnabled.isDividerDecorationEnabled
                    setOnCheckedChangeListener(onCheckedChangeListener)
                }
                with(subMenu.findItem(R.id.afterLast).actionView.findViewById<SwitchCompat>(R.id.switchEnable)) {
                    isChecked = dividersEnabled.isLastDividerDecorationEnabled
                    setOnCheckedChangeListener(onCheckedChangeListener)
                }
            }
            val isStickyHeaderFeatureEnabled = mainViewModel.isStickyHeaderFeatureEnabledLiveData().value!!
            with(findItem(R.id.stickyHeaderState).actionView.findViewById<SwitchCompat>(R.id.switchEnable)) {
                isChecked = isStickyHeaderFeatureEnabled
                setOnCheckedChangeListener { _, isChecked ->
                    mainViewModel.setIsStickyHeaderFeatureEnabledLiveData(isChecked)
                    findItem(R.id.menuStickyHeader).isVisible = isChecked
                }
            }
            with(findItem(R.id.menuStickyHeader)) {
                isVisible = isStickyHeaderFeatureEnabled
                val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    with(binding.navigationView.menu.findItem(R.id.menuStickyHeader).subMenu) {
                        val isHeaderEnabled = findItem(R.id.headerStickyHeader).actionView.findViewById<SwitchCompat>(R.id.switchEnable).isChecked
                        val isCategoryEnabled = findItem(R.id.categoryStickyHeader).actionView.findViewById<SwitchCompat>(R.id.switchEnable).isChecked
                        val isMovieEnabled = findItem(R.id.movieStickyHeader).actionView.findViewById<SwitchCompat>(R.id.switchEnable).isChecked
                        mainViewModel.setStickyHeadersEnabled(Triple(isHeaderEnabled, isCategoryEnabled, isMovieEnabled))
                    }
                }
                val stickyHeadersEnabled = mainViewModel.getStickyHeadersEnabledLiveData().value!!
                with(subMenu.findItem(R.id.headerStickyHeader).actionView.findViewById<SwitchCompat>(R.id.switchEnable)) {
                    isChecked = stickyHeadersEnabled.first
                    setOnCheckedChangeListener(onCheckedChangeListener)
                }
                with(subMenu.findItem(R.id.categoryStickyHeader).actionView.findViewById<SwitchCompat>(R.id.switchEnable)) {
                    isChecked = stickyHeadersEnabled.second
                    setOnCheckedChangeListener(onCheckedChangeListener)
                }
                with(subMenu.findItem(R.id.movieStickyHeader).actionView.findViewById<SwitchCompat>(R.id.switchEnable)) {
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

        binding.recyclerView.adapter = DemoAdapter(
                context = this,
                addMovie = mainViewModel::addMovie,
                removeMovie = mainViewModel::removeMovie,
                addOrRemoveAllMovies = mainViewModel::addOrRemoveAllMovies,
                scrollToPosition = { position ->
                    binding.recyclerView.scrollToPosition(position)
                },
                stickyHeadersEnabled = mainViewModel.getStickyHeadersEnabledLiveData().value!!,
                dividersEnabled = mainViewModel.getDividersEnabledLiveData().value!!,
                onActorsOrientationChanged = { isActorsOrientationHorizontal ->
                    mainViewModel.setIsActorsOrientationHorizontalLiveData(isActorsOrientationHorizontal = isActorsOrientationHorizontal)
                },
                isActorsOrientationHorizontal = {
                    mainViewModel.isActorsOrientationHorizontalLiveData().value!!
                },
                isDividerFeatureEnabled = {
                    mainViewModel.isDividerFeatureEnabledLiveData().value!!
                }
        )

        binding.recyclerView.itemAnimator.takeIf { it is SimpleItemAnimator }?.let {
            (it as SimpleItemAnimator).supportsChangeAnimations = false
        }

        mainViewModel.getItemsLiveData().observe(this, Observer { items ->
            (binding.recyclerView.adapter as DemoAdapter).refresh(items)
            binding.recyclerView.invalidateItemDecorations()
        })

        mainViewModel.isDividerFeatureEnabledLiveData().observe(this, Observer { isDividerFeatureEnabled ->
            binding.recyclerView.removeAllLinearItemDecorations()
            if (isDividerFeatureEnabled) {
                binding.recyclerView.addLinearItemDecoration(LinearItemDecoration(binding.recyclerView.adapter as LinearDividersListener))
            }
            (binding.recyclerView.adapter as DemoAdapter).items.indexOfFirst { it is MainViewModel.Actors }.takeIf { it != -1 }?.let {
                (binding.recyclerView.findViewHolderForAdapterPosition(it) as? DemoAdapter.ActorsViewModel)?.notifyDividersChanged()
            }
        })

        mainViewModel.isStickyHeaderFeatureEnabledLiveData().observe(this, Observer { isStickyHeaderFeatureEnabled ->
            binding.recyclerView.removeAllStickyHeaderItemDecorations()
            if (isStickyHeaderFeatureEnabled) {
                binding.recyclerView.addStickyHeaderItemDecoration(StickyHeaderLinearItemDecoration(binding.recyclerView.adapter as LinearStickyHeadersListener))
            }
        })

        mainViewModel.getStickyHeadersEnabledLiveData().observe(this, Observer {
            (binding.recyclerView.adapter as DemoAdapter).stickyHeadersEnabled = it
            binding.recyclerView.invalidateItemDecorations()
        })

        mainViewModel.getDividersEnabledLiveData().observe(this, Observer {
            (binding.recyclerView.adapter as DemoAdapter).dividersEnabled = it
            (binding.recyclerView.adapter as DemoAdapter).items.indexOfFirst { it is MainViewModel.Actors }.takeIf { it != -1 }?.let {
                (binding.recyclerView.findViewHolderForAdapterPosition(it) as? DemoAdapter.ActorsViewModel)?.notifyDividersChanged()
            }
            binding.recyclerView.invalidateItemDecorations()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }else{
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
