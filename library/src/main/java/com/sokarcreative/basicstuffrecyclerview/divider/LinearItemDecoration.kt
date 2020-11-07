package com.sokarcreative.basicstuffrecyclerview.divider

import android.graphics.Canvas
import android.graphics.Rect
import android.util.LayoutDirection
import android.view.View
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import com.sokarcreative.basicstuffrecyclerview.Decoration


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
            state: RecyclerView.State,
    ) {

        val adapter = parent.adapter ?: return

        canvas.save()
        canvas.clipRect(parent.paddingStart, parent.paddingTop, parent.width, parent.height)

        drawDividers(canvas, parent, adapter)

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
            state: RecyclerView.State,
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapter = parent.adapter ?: return

        val position: Int = parent.getChildAdapterPosition(view)
        if (position != RecyclerView.NO_POSITION) {
            if (parent.layoutManager is androidx.recyclerview.widget.LinearLayoutManager) {
                mOrientation = (parent.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).orientation

                getPreviousDivider(
                        adapter,
                        position,
                        onFirstViewType = mLinearDividersListener::getFirstDecoration,
                        onPreviousViewType = mLinearDividersListener::getFirstDividerDecoration
                )?.run {
                    if (mOrientation == androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL) {
                        if (parent.layoutDirection == LayoutDirection.LTR) {
                            outRect.left = width
                        } else {
                            outRect.right = width
                        }
                    } else if (mOrientation == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
                        outRect.top = height
                    }
                }

                getNextDivider(
                        adapter,
                        position,
                        onSameViewType = mLinearDividersListener::getDividerDecoration,
                        onNextViewType = mLinearDividersListener::getLastDividerDecoration,
                        onLastViewType = mLinearDividersListener::getLastDecoration
                )?.run {
                    if (mOrientation == androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL) {
                        if (parent.layoutDirection == LayoutDirection.LTR) {
                            outRect.right = width
                        } else {
                            outRect.left = width
                        }
                    } else if (mOrientation == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
                        outRect.bottom = height
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
    private fun drawDividers(
            canvas: Canvas,
            parent: RecyclerView,
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    ) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val viewHolder = parent.getChildViewHolder(view)
            val position: Int = parent.getChildAdapterPosition(view)
            if (viewHolder != null && position != RecyclerView.NO_POSITION) {
                getPreviousDivider(
                        adapter,
                        position,
                        onPreviousViewType = mLinearDividersListener::getFirstDividerDecoration,
                        onFirstViewType = mLinearDividersListener::getFirstDecoration
                )?.run {
                    when(this){
                        is Decoration.Drawable -> {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                drawable.layoutDirection = parent.layoutDirection
                            }
                            if(mOrientation == androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL){
                                drawable.setBounds(
                                        view.left - view.marginStart - drawable.intrinsicWidth,
                                        0,
                                        view.left - view.marginStart,
                                        view.bottom + view.marginBottom
                                )
                                drawable.draw(canvas)
                            }else{
                                drawable.setBounds(
                                        0,
                                        view.top - view.marginTop - drawable.intrinsicHeight,
                                        view.right + view.marginEnd,
                                        view.top - view.marginTop
                                )
                                drawable.draw(canvas)
                            }
                        }
                    }
                }
                getNextDivider(
                        adapter,
                        position,
                        onSameViewType = mLinearDividersListener::getDividerDecoration,
                        onNextViewType = mLinearDividersListener::getLastDividerDecoration,
                        onLastViewType = mLinearDividersListener::getLastDecoration
                )?.run {
                    when(this){
                        is Decoration.Drawable -> {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                drawable.layoutDirection = parent.layoutDirection
                            }
                            if(mOrientation == androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL){
                                drawable.setBounds(
                                        view.right + view.marginEnd,
                                        0,
                                        view.right + view.marginEnd + drawable.intrinsicWidth,
                                        view.bottom + view.marginBottom
                                )
                                drawable.draw(canvas)
                            }else{
                                drawable.setBounds(
                                        0,
                                        view.bottom + view.marginBottom,
                                        view.right + view.marginEnd,
                                        view.bottom + view.marginBottom + drawable.intrinsicHeight
                                )
                                drawable.draw(canvas)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @return the next decoration at the given [position].
     */
    private fun <T> getNextDivider(
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            position: Int,
            onSameViewType: (viewType: Int) -> T?,
            onNextViewType: (viewType: Int, nextViewType: Int) -> T?,
            onLastViewType: (viewType: Int) -> T?,
    ): T? {
        val viewType = adapter.getItemViewType(position)
        return if (position + 1 < adapter.itemCount) {
            val nextViewType: Int = adapter.getItemViewType(position + 1)
            if (viewType != nextViewType) {
                onNextViewType(viewType, nextViewType)
            } else {
                onSameViewType(viewType)
            }
        } else {
            onLastViewType(viewType)
        }
    }


    /**
     * @return the previous decoration at the given [position].
     */
    private fun <T> getPreviousDivider(
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            position: Int,
            onPreviousViewType: (viewType: Int, previousViewType: Int) -> T?,
            onFirstViewType: (viewType: Int) -> T?,
    ): T? {
        val viewType = adapter.getItemViewType(position)
        return if (position > 0) {
            val previousViewType: Int = adapter.getItemViewType(position - 1)
            if (viewType != previousViewType) {
                onPreviousViewType(viewType, previousViewType)
            } else {
                null
            }
        } else {
            onFirstViewType(viewType)
        }
    }



}