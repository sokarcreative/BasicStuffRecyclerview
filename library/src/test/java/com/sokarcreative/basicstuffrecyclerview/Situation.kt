package com.sokarcreative.basicstuffrecyclerview

import com.sokarcreative.basicstuffrecyclerview.divider.LinearDividersListener

data class Situation(
        val spanCount: Int,
        val items: List<Any?>,
        val getItemViewType: (position: Int) -> Int,
        val getSpanSizeOnViewType: (viewType: Int) -> Int,
        val linearDividersListener: LinearDividersListener,
)