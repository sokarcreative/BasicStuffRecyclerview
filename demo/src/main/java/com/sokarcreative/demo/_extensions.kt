package com.sokarcreative.demo

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.sokarcreative.basicstuffrecyclerview.divider.LinearDividersListener
import com.sokarcreative.basicstuffrecyclerview.divider.LinearItemDecoration
import com.sokarcreative.basicstuffrecyclerview.stickyheader.StickyHeaderLinearItemDecoration
import kotlinx.android.synthetic.main.activity_main.*

fun Context.convertDpToPixel(dp: Float): Int = (dp * (resources.displayMetrics.densityDpi / 160f)).toInt()

private fun RecyclerView.itemDecorations(): Iterable<RecyclerView.ItemDecoration> {
    val itemDecorations = arrayListOf<RecyclerView.ItemDecoration>()
    for (i in 0 until itemDecorationCount) {
        itemDecorations.add(getItemDecorationAt(i))
    }
    return itemDecorations
}

fun RecyclerView.removeAllLinearItemDecorations() {
    itemDecorations().filterIsInstance<LinearItemDecoration>().forEach {
        removeItemDecoration(it)
    }
}

fun RecyclerView.addLinearItemDecoration(linearItemDecoration: LinearItemDecoration) {
    addItemDecoration(linearItemDecoration)
}

fun RecyclerView.removeAllStickyHeaderItemDecorations() {
    itemDecorations().filterIsInstance<StickyHeaderLinearItemDecoration>().forEach {
        removeOnItemTouchListener(it)
        removeItemDecoration(it)
    }
}

fun RecyclerView.addStickyHeaderItemDecoration(stickyHeaderLinearItemDecoration: StickyHeaderLinearItemDecoration) {
    addItemDecoration(stickyHeaderLinearItemDecoration.also {
        addOnItemTouchListener(it)
    })
}

