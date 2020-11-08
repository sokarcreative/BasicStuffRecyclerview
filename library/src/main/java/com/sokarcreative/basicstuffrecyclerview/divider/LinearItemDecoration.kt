package com.sokarcreative.basicstuffrecyclerview.divider

import android.graphics.Canvas
import android.graphics.Rect
import android.util.LayoutDirection
import android.view.View
import androidx.core.view.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
        val layoutManager = (parent.layoutManager as? LinearLayoutManager)?.also {
            mOrientation = it.orientation
        } ?: return

        val spanCount = layoutManager.spanCountCompat

        val position: Int = parent.getChildAdapterPosition(view)
        if (position != RecyclerView.NO_POSITION) {
            val spanSize = layoutManager.spanSizeCompat(position)
            fun Decoration.applyOffsetOnSpanSizeLookupEquals1(reverse: Boolean = false) = run {
                if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                    if (parent.layoutDirection == LayoutDirection.LTR) {
                        if (reverse) {
                            outRect.left = width
                        } else {
                            outRect.right = width
                        }
                    } else {
                        if (reverse) {
                            outRect.right = width
                        } else {
                            outRect.left = width
                        }
                    }
                } else if (mOrientation == LinearLayoutManager.VERTICAL) {
                    if (reverse) {
                        outRect.top = height
                    } else {
                        outRect.bottom = height
                    }
                }
            }
            val viewType = adapter.getItemViewType(position)
            val positionOfFirstSameViewType: Int by lazy {
                var p = 0
                for (i in position downTo 0) {
                    val itemViewType = adapter.getItemViewType(i)
                    if (itemViewType != viewType) {
                        p = i + 1
                        break
                    }
                }
                p
            }

            val positionOfLastSameViewType: Int by lazy {
                var p = adapter.itemCount - 1
                for (i in position until adapter.itemCount) {
                    val itemViewType = adapter.getItemViewType(i)
                    if (itemViewType != viewType) {
                        p = i - 1
                        break
                    }
                }
                p
            }

            onDivider(
                    layoutManager,
                    adapter,
                    position,
                    onFirstViewType = { viewType ->
                        mLinearDividersListener.getFirstDecoration(viewType)?.applyOffsetOnSpanSizeLookupEquals1(true)
                    },
                    onPreviousViewType = { viewType, previousViewType ->
                        mLinearDividersListener.getFirstDividerDecoration(viewType, previousViewType)?.applyOffsetOnSpanSizeLookupEquals1(true)
                    },
                    onSameViewType = { viewType, isLast ->
                        if (!(spanCount > 1 && spanSize != spanCount)) {
                            if (!isLast) {
                                val decoration = mLinearDividersListener.getDividerDecoration(viewType = viewType)
                                        ?: return@onDivider
                                decoration.applyOffsetOnSpanSizeLookupEquals1()
                            }
                        } else {
                            val decoration = mLinearDividersListener.getDividerDecoration(viewType = viewType)
                                    ?: return@onDivider
                            val sameTypesCount = positionOfLastSameViewType - positionOfFirstSameViewType
                            val positionInSameViewTypes = position - positionOfFirstSameViewType

                            if (mOrientation == LinearLayoutManager.HORIZONTAL) {

                                val column = positionInSameViewTypes/spanCount
                                if(column < sameTypesCount/spanCount){
                                    if (parent.layoutDirection == LayoutDirection.LTR) {
                                        outRect.right = decoration.width/2
                                    }else{
                                        outRect.left = decoration.width/2
                                    }
                                }
                                if(column > 0){
                                    if (parent.layoutDirection == LayoutDirection.LTR) {
                                        outRect.left = decoration.width/2
                                    }else{
                                        outRect.right = decoration.width/2
                                    }
                                }

                                val row = positionInSameViewTypes%spanCount
                                if (row > 0) {
                                    outRect.bottom = decoration.height
                                }

                            } else if (mOrientation == LinearLayoutManager.VERTICAL) {
                                val column = positionInSameViewTypes%spanCount
                                if(column > 0){
                                    if (parent.layoutDirection == LayoutDirection.LTR) {
                                        outRect.left = decoration.width/2
                                    } else {
                                        outRect.right = decoration.width/2
                                    }
                                }
                                if(column < spanCount-1){
                                    if (parent.layoutDirection == LayoutDirection.LTR) {
                                        outRect.right = decoration.width/2
                                    } else {
                                        outRect.left = decoration.width/2
                                    }
                                }
                                val row = positionInSameViewTypes/spanCount
                                if (row < sameTypesCount/spanCount) {
                                    outRect.bottom = decoration.height
                                }
                            }
                        }
                    },
                    onNextViewType = { viewType, nextViewType ->
                        mLinearDividersListener.getLastDividerDecoration(viewType, nextViewType)?.applyOffsetOnSpanSizeLookupEquals1()
                    },
                    onLastViewType = { viewType ->
                        mLinearDividersListener.getLastDecoration(viewType)?.applyOffsetOnSpanSizeLookupEquals1()
                    },
                    onGridBorderViewType = { viewType ->
                        val borderDecoration = mLinearDividersListener.getGridBorderDecoration(adapter.getItemViewType(position))
                                ?: return@onDivider
                        if (!(spanCount > 1 && spanSize != spanCount)) {
                            if (mOrientation == LinearLayoutManager.VERTICAL) {
                                outRect.left = borderDecoration.width
                                outRect.right = borderDecoration.width
                            } else {
                                outRect.top = borderDecoration.height
                                outRect.bottom = borderDecoration.height
                            }
                        } else {
                            val positionInSameViewTypes = position - positionOfFirstSameViewType
                            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                                if (positionInSameViewTypes % spanCount == 0) {
                                    if (parent.layoutDirection == LayoutDirection.LTR) {
                                        outRect.top = borderDecoration.height
                                    } else {
                                        outRect.bottom = borderDecoration.height
                                    }
                                } else if (positionInSameViewTypes % spanCount == spanCount - 1) {
                                    if (parent.layoutDirection == LayoutDirection.LTR) {
                                        outRect.bottom = borderDecoration.height
                                    } else {
                                        outRect.top = borderDecoration.height
                                    }
                                }
                            } else if (mOrientation == LinearLayoutManager.VERTICAL) {
                                if (positionInSameViewTypes % spanCount == 0) {
                                    if (parent.layoutDirection == LayoutDirection.LTR) {
                                        outRect.left = borderDecoration.width
                                    } else {
                                        outRect.right = borderDecoration.width
                                    }
                                } else if (positionInSameViewTypes % spanCount == spanCount - 1) {
                                    if (parent.layoutDirection == LayoutDirection.LTR) {
                                        outRect.right = borderDecoration.width
                                    } else {
                                        outRect.left = borderDecoration.width
                                    }
                                }
                            }
                        }
                    }
            )
        }/*else {
            if (spanCount > 1) {
                val viewHolder = parent.findContainingViewHolder(view) ?: return
                val borderDecoration = mLinearDividersListener.getGridBorderDecoration(viewHolder.itemViewType)
                        ?: return
                if (mOrientation == LinearLayoutManager.VERTICAL) {
                    outRect.right = borderDecoration.width
                    outRect.left = borderDecoration.width
                } else {
                    outRect.top = borderDecoration.height
                    outRect.bottom = borderDecoration.height
                }
            }
        }*/
    }



    private inline val LinearLayoutManager.spanCountCompat get(): Int = if (this is GridLayoutManager) this.spanCount else 1
    private fun LinearLayoutManager.spanSizeCompat(position: Int): Int = if (this is GridLayoutManager) this.spanSizeLookup.getSpanSize(position) else 1


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
            val layoutManager = (parent.layoutManager as? LinearLayoutManager)?.also {
                mOrientation = it.orientation
            } ?: return
            val spanCount = if (layoutManager is GridLayoutManager) layoutManager.spanCount else 1
            if (viewHolder != null && position != RecyclerView.NO_POSITION) {
                fun Decoration.drawOnSpanSizeLookupEquals1Previous() = run {
                    when (this) {
                        is Decoration.Drawable -> {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                drawable.layoutDirection = parent.layoutDirection
                            }
                            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                                drawable.setBounds(
                                        view.left - view.marginStart - drawable.intrinsicWidth,
                                        0,
                                        view.left - view.marginStart,
                                        view.bottom + view.marginBottom
                                )
                                drawable.draw(canvas)
                            } else {
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

                fun Decoration.drawOnSpanSizeLookupEquals1Next() = run {
                    when (this) {
                        is Decoration.Drawable -> {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                drawable.layoutDirection = parent.layoutDirection
                            }
                            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                                drawable.setBounds(
                                        view.right + view.marginEnd,
                                        0,
                                        view.right + view.marginEnd + drawable.intrinsicWidth,
                                        view.bottom + view.marginBottom
                                )
                                drawable.draw(canvas)
                            } else {
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

                onDivider(
                        layoutManager,
                        adapter,
                        position,
                        onFirstViewType = { viewType ->
                            mLinearDividersListener.getFirstDecoration(viewType)?.drawOnSpanSizeLookupEquals1Previous()
                        },
                        onPreviousViewType = { viewType, previousViewType ->
                            mLinearDividersListener.getFirstDividerDecoration(viewType, previousViewType)?.drawOnSpanSizeLookupEquals1Previous()
                        },
                        onSameViewType = { viewType, isLast ->
                            if (!isLast) {
                                mLinearDividersListener.getDividerDecoration(viewType)?.drawOnSpanSizeLookupEquals1Next()
                            }
                        },
                        onNextViewType = { viewType, nextViewType ->
                            mLinearDividersListener.getLastDividerDecoration(viewType, nextViewType)?.drawOnSpanSizeLookupEquals1Next()
                        },
                        onLastViewType = { viewType ->
                            mLinearDividersListener.getLastDecoration(viewType)?.drawOnSpanSizeLookupEquals1Next()
                        },
                        onGridBorderViewType = { viewType ->
                            // not supported
                        }
                )
            }
        }
    }

    /**
     * @return the next decoration at the given [position].
     */
    private fun onDivider(
            layoutManager: LinearLayoutManager,
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            position: Int,
            onFirstViewType: (viewType: Int) -> Unit,
            onPreviousViewType: (viewType: Int, previousViewType: Int) -> Unit,
            onSameViewType: (viewType: Int, isLast: Boolean) -> Unit,
            onNextViewType: (viewType: Int, nextViewType: Int) -> Unit,
            onLastViewType: (viewType: Int) -> Unit,
            onGridBorderViewType: (viewType: Int) -> Unit,
    ) {

        val spanCount = layoutManager.spanCountCompat
        val viewType = adapter.getItemViewType(position)
        if (position < spanCount) {
            if (spanCount == 1) {
                onFirstViewType(viewType)
            } else {
                val gridLayoutManager: GridLayoutManager = layoutManager as GridLayoutManager
                var spanSizeLookup = 0
                for (i in 0 until position) {
                    spanSizeLookup += gridLayoutManager.spanSizeLookup.getSpanSize(i)
                }
                if (spanSizeLookup < spanCount) {
                    onFirstViewType(viewType)
                }
            }
        }
        if (position > 0) {
            val previousViewType: Int = adapter.getItemViewType(position - 1)
            if (viewType != previousViewType) {
                onPreviousViewType(viewType, previousViewType)
            }
        }
        if (position + 1 < adapter.itemCount) {
            val nextViewType: Int = adapter.getItemViewType(position + 1)
            if (viewType != nextViewType) {
                onSameViewType(viewType, true)
                onNextViewType(viewType, nextViewType)
            } else {
                onSameViewType(viewType, false)
            }
        } else {
            onSameViewType(viewType, true)
        }
        if (position >= adapter.itemCount - spanCount) {
            if (spanCount == 1) {
                onLastViewType(viewType)
            } else {
                val gridLayoutManager: GridLayoutManager = layoutManager as GridLayoutManager
                var spanSizeLookup = 0
                for (i in position until adapter.itemCount) {
                    spanSizeLookup += gridLayoutManager.spanSizeLookup.getSpanSize(i)
                }
                if (spanSizeLookup <= spanCount) {
                    fun positionOfFirstSameViewType(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, position: Int, viewType: Int): Int = run {
                        for (i in position downTo 0) {
                            val itemViewType = adapter.getItemViewType(i)
                            if (itemViewType != viewType) {
                                return@run i + 1
                            }
                        }
                        return@run 0
                    }

                    fun positionOfLastSameViewType(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, position: Int, viewType: Int): Int = run {
                        for (i in position until adapter.itemCount) {
                            val itemViewType = adapter.getItemViewType(i)
                            if (itemViewType != viewType) {
                                return@run i - 1
                            }
                        }
                        return@run adapter.itemCount - 1
                    }
                    val positionOfFirstOfSameViewType = positionOfFirstSameViewType(adapter, position, viewType)
                    val positionOfLastOfSameViewType = positionOfLastSameViewType(adapter, position, viewType)
                    val positionInSameViewTypes = position - positionOfFirstOfSameViewType
                    val sameTypesCount = positionOfLastOfSameViewType - positionOfFirstOfSameViewType
                    if (positionInSameViewTypes/spanCount == sameTypesCount / spanCount) {
                        onLastViewType(viewType)
                    }
                }
            }
        }
        onGridBorderViewType(viewType)
    }


}