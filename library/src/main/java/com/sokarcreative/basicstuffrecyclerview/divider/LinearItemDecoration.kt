package com.sokarcreative.basicstuffrecyclerview.divider

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView


/**
 * Copyright (C) 2017 sokarcreative
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class LinearItemDecoration constructor(var mLinearDividersListener: LinearDividersListener) :
        RecyclerView.ItemDecoration() {

    private var mOrientation: Int = 0

    /**
     * Draw dividers.
     *
     * @param canvas Canvas to draw into.
     * @param parent RecyclerView this ItemDecoration is drawing into.
     * @param state The current state of RecyclerView.
     */
    override fun onDraw(
            canvas: Canvas,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {

        val adapter = parent.adapter ?: return

        canvas.save()
        canvas.clipRect(parent.paddingLeft, parent.paddingTop, parent.width, parent.height)

        if (mOrientation == androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL) {
            drawVerticalDividers(canvas, parent, adapter)
        } else if (mOrientation == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
            drawHorizontalDividers(canvas, parent, adapter)
        }

        canvas.restore()
    }

    /**
     *
     * @param outRect Rect to receive the output.
     * @param view    The child view to decorate
     * @param parent  RecyclerView this ItemDecoration is decorating
     * @param state   The current state of RecyclerView.
     */
    override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapter = parent.adapter ?: return

        val position: Int = parent.getChildAdapterPosition(view)
        if (position != RecyclerView.NO_POSITION) {
            if (parent.layoutManager is androidx.recyclerview.widget.LinearLayoutManager) {
                mOrientation = (parent.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).orientation

                getNextDecoration(adapter, position)?.let {
                    if (mOrientation == androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL) {
                        outRect.right = it.intrinsicWidth
                    } else if (mOrientation == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
                        outRect.bottom = it.intrinsicHeight
                    }
                }

                getPreviousDecoration(adapter, position)?.let {
                    if (mOrientation == androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL) {
                        outRect.left = it.intrinsicWidth

                    } else if (mOrientation == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
                        outRect.top = it.intrinsicHeight
                    }
                }
            }
        }
    }


    /**
     * Draw horizontal dividers.
     *
     * @param canvas Canvas to draw into.
     * @param parent RecyclerView this ItemDecoration is drawing into.
     */
    private fun drawVerticalDividers(
            canvas: Canvas,
            parent: RecyclerView,
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val viewHolder = parent.getChildViewHolder(view)
            val position: Int = parent.getChildAdapterPosition(view)
            if (viewHolder != null && position != RecyclerView.NO_POSITION) {
                getPreviousDecoration(adapter, position)?.let {
                    it.setBounds(
                            view.left - view.marginStart - it.intrinsicWidth,
                            0,
                            view.left - view.marginStart,
                            view.bottom + view.marginBottom
                    )
                    it.draw(canvas)
                }
                getNextDecoration(adapter, position)?.let {
                    it.setBounds(
                            view.right + view.marginRight,
                            0,
                            view.right + view.marginRight + it.intrinsicWidth,
                            view.bottom + view.marginBottom
                    )
                    it.draw(canvas)
                }
            }
        }
    }

    /**
     * Draw vertical dividers.
     *
     * @param canvas Canvas to draw into.
     * @param parent RecyclerView this ItemDecoration is drawing into.
     */
    private fun drawHorizontalDividers(
            canvas: Canvas,
            parent: RecyclerView,
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val viewHolder = parent.getChildViewHolder(view)
            val position: Int = parent.getChildAdapterPosition(view)
            if (viewHolder != null && position != RecyclerView.NO_POSITION) {
                getPreviousDecoration(adapter, position)?.let {
                    it.setBounds(
                            0,
                            view.top - view.marginTop - it.intrinsicHeight,
                            view.right + view.marginEnd,
                            view.top - view.marginTop
                    )
                    it.draw(canvas)
                }
                getNextDecoration(adapter, position)?.let {
                    it.setBounds(
                            0,
                            view.bottom + view.marginBottom,
                            view.right + view.marginEnd,
                            view.bottom + view.marginBottom + it.intrinsicHeight
                    )
                    it.draw(canvas)
                }
            }
        }
    }

    /**
     * @return the next decoration at the given [position].
     */
    fun getNextDecoration(
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            position: Int,
    ): Drawable? {
        val viewType = adapter.getItemViewType(position)
        return if (position + 1 < adapter.itemCount) {
            val nextViewType: Int = adapter.getItemViewType(position + 1)
            if (viewType != nextViewType) {
                mLinearDividersListener.getLastDividerDecoration(viewType, nextViewType)
            } else {
                mLinearDividersListener.getDividerDecoration(viewType)
            }
        } else {
            mLinearDividersListener.getLastDecoration(viewType)
        }
    }

    /**
     * @return the previous decoration at the given [position].
     */
    private fun getPreviousDecoration(
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            position: Int,
    ): Drawable? {
        val viewType = adapter.getItemViewType(position)
        return if (position > 0) {
            val previousViewType: Int = adapter.getItemViewType(position - 1)
            if (viewType != previousViewType) {
                mLinearDividersListener.getFirstDividerDecoration(viewType, previousViewType)
            } else {
                null
            }
        } else {
            mLinearDividersListener.getFirstDecoration(viewType)
        }
    }

}