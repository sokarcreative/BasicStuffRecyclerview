package com.sokarcreative.basicstuffrecyclerview

import android.graphics.Rect
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sokarcreative.basicstuffrecyclerview.divider.LinearItemDecoration

object OffsetTestBuilder {
    fun build(
            situation: Situation,
            onResult: (resultsInVerticalLtr: Array<Rect>, resultsInHorizontalLtr: Array<Rect>, resultsInVerticalRtl: Array<Rect>, resultsInHorizontalRtl: Array<Rect>) -> Unit,
    ) {
        System.out.println(situation.toString())
        val adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun getItemViewType(position: Int): Int = situation.getItemViewType(position)
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                TODO("Not yet implemented")
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                TODO("Not yet implemented")
            }

            override fun getItemCount(): Int = situation.items.size
        }


        val linearItemDecoration = LinearItemDecoration(situation.linearDividersListener)
        val getSpanSize: (Int) -> Int = { position -> situation.getSpanSizeOnViewType(situation.getItemViewType(position)) }
        return onResult(
                Array(situation.items.size) { position ->
                    linearItemDecoration.getItemOffsets(
                            outRect = MockitoRect(),
                            adapter = adapter,
                            spanCount = situation.spanCount,
                            position = position,
                            spanSize = situation.getSpanSizeOnViewType(situation.getItemViewType(position)),
                            isOrientationVertical = true,
                            isLtr = true,
                            getSpanSize = getSpanSize
                    ).withInjectedMockitoMethods()
                },
                Array(situation.items.size) { position ->
                    linearItemDecoration.getItemOffsets(
                            outRect = MockitoRect(),
                            adapter = adapter,
                            spanCount = situation.spanCount,
                            position = position,
                            spanSize = situation.getSpanSizeOnViewType(situation.getItemViewType(position)),
                            isOrientationVertical = false,
                            isLtr = true,
                            getSpanSize = getSpanSize
                    ).withInjectedMockitoMethods()
                },
                Array(situation.items.size) { position ->
                    linearItemDecoration.getItemOffsets(
                            outRect = MockitoRect(),
                            adapter = adapter,
                            spanCount = situation.spanCount,
                            position = position,
                            spanSize = situation.getSpanSizeOnViewType(situation.getItemViewType(position)),
                            isOrientationVertical = true,
                            isLtr = false,
                            getSpanSize = getSpanSize
                    ).withInjectedMockitoMethods()
                },
                Array(situation.items.size) { position ->
                    linearItemDecoration.getItemOffsets(
                            outRect = MockitoRect(),
                            adapter = adapter,
                            spanCount = situation.spanCount,
                            position = position,
                            spanSize = situation.getSpanSizeOnViewType(situation.getItemViewType(position)),
                            isOrientationVertical = false,
                            isLtr = false,
                            getSpanSize = getSpanSize
                    ).withInjectedMockitoMethods()
                }
        )
    }
}