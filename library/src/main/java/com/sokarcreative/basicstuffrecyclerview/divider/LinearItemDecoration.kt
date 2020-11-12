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


    private var positionInSameGridSet: Int = 0
    private var gridSetCount: Int = 0
    private var mPositionOfFirstOfSameViewTypeOrSameGridSet: Int? = null
    private var mPositionOfLastOfSameViewTypeOrSameGridSet: Int? = null
    private var mPosition = 0
    private var mPreviousViewType = 0
    private var mViewType = 0
    private var mNextViewType = 0
    private var mSpanCount = 0
    private var mSpanSize = 0
    private var mIsOrientationVertical: Boolean = false
    private var mIsLtr: Boolean = true
    private var mAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    private val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder> get() = mAdapter!!
    private var mView: View? = null
    private val view: View get() = mView!!
    private var mDecoration: Decoration? = null
    private val decoration: Decoration get() = mDecoration!!
    private var column: Int = 0
    private var row: Int = 0
    private var mGetSpanSize: ((Int) -> Int)? = null
    private val getSpanSize: ((Int) -> Int) get() = mGetSpanSize!!
    private var rows: Int = 0
    private var columns: Int = 0
    private var offsetLeft: Int = 0
    private var offsetTop: Int = 0
    private var offsetRight: Int = 0
    private var offsetBottom: Int = 0


    private val positionOfFirstOfSameViewTypeOrSameGridSet: Int
        get() = mPositionOfFirstOfSameViewTypeOrSameGridSet ?: let {
            mPositionOfFirstOfSameViewTypeOrSameGridSet = if (mSpanSize == mSpanCount) {
                positionOfFirstSameViewType()
            } else {
                positionOfFirstSpanSizeNotEqualsSpanCount()
            }
            mPositionOfFirstOfSameViewTypeOrSameGridSet!!
        }

    private val positionOfLastOfSameViewTypeOrSameGridSet: Int
        get() = mPositionOfLastOfSameViewTypeOrSameGridSet ?: let {
            mPositionOfLastOfSameViewTypeOrSameGridSet = if (mSpanSize == mSpanCount) {
                positionOfLastSameViewType()
            } else {
                positionOfLastSpanSizeNotEqualsSpanCount()
            }
            mPositionOfLastOfSameViewTypeOrSameGridSet!!
        }

    private fun hasGlobalSetupSucceed(parent: RecyclerView): Boolean {
        mAdapter = parent.adapter ?: return false

        (parent.layoutManager as? LinearLayoutManager)?.also {
            mGetSpanSize = if (it is GridLayoutManager) it.spanSizeLookup::getSpanSize else { postion -> 1 }
            mSpanCount = if (it is GridLayoutManager) it.spanCount else 1
            mIsOrientationVertical = it.orientation == LinearLayoutManager.VERTICAL
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                mIsLtr = parent.layoutDirection == LayoutDirection.LTR
            }
        } ?: return false

        return true
    }

    private fun cleanupGlobalResources() {
        mGetSpanSize = null
        mAdapter = null
        mView = null
        mDecoration = null
        mPositionOfFirstOfSameViewTypeOrSameGridSet = null
        mPositionOfLastOfSameViewTypeOrSameGridSet = null
    }

    private fun hasViewSetupSucceed(parent: RecyclerView, view: View): Boolean {
        mView = view
        mPosition = parent.getChildAdapterPosition(view).takeIf { it != RecyclerView.NO_POSITION }
                ?: return false
        mSpanSize = getSpanSize(mPosition)
        mViewType = adapter.getItemViewType(mPosition)
        mPositionOfFirstOfSameViewTypeOrSameGridSet = null
        mPositionOfLastOfSameViewTypeOrSameGridSet = null
        mDecoration = null
        return true
    }


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

        if (!hasGlobalSetupSucceed(parent)) {
            cleanupGlobalResources()
            return
        }

        canvas.save()
        canvas.clipRect(parent.paddingStart, parent.paddingTop, parent.width, parent.height)

        drawDividers(canvas, parent)

        canvas.restore()
        cleanupGlobalResources()
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

        if (!hasGlobalSetupSucceed(parent) || !hasViewSetupSucceed(parent, view)) {
            cleanupGlobalResources()
            return
        }

        getItemOffsets(outRect, adapter, mSpanCount, mPosition, mSpanSize, mIsOrientationVertical, mIsLtr, getSpanSize)
    }

    internal fun getItemOffsets(outRect: Rect, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, spanCount: Int, position: Int, spanSize: Int, isOrientationVertical: Boolean, isLtr: Boolean, getSpanSize: (Int) -> Int): Rect {
        mAdapter = adapter
        mSpanCount = spanCount
        mPosition = position
        mSpanSize = spanSize
        mIsOrientationVertical = isOrientationVertical
        mIsLtr = isLtr
        mViewType = adapter.getItemViewType(position)
        mPositionOfFirstOfSameViewTypeOrSameGridSet = null
        mPositionOfLastOfSameViewTypeOrSameGridSet = null
        mGetSpanSize = getSpanSize
        onDivider(
                onFirstViewType = {
                    mLinearDividersListener.getFirstDecoration(mViewType)?.applyOffsetOnSpanSizeLookupEquals1(outRect, true)
                },
                onPreviousViewType = {
                    mLinearDividersListener.getFirstDividerDecoration(mViewType, mPreviousViewType)?.applyOffsetOnSpanSizeLookupEquals1(outRect, true)
                },
                onSameViewType = {
                    if (mSpanCount == 1 || mSpanSize == mSpanCount) {
                        mLinearDividersListener.getDividerDecoration(viewType = mViewType)?.applyOffsetOnSpanSizeLookupEquals1(outRect)
                    } else {
                        mDecoration = mLinearDividersListener.getDividerDecoration(viewType = mViewType)
                                ?: return@onDivider
                        gridSetCount = positionOfLastOfSameViewTypeOrSameGridSet - positionOfFirstOfSameViewTypeOrSameGridSet
                        positionInSameGridSet = mPosition - positionOfFirstOfSameViewTypeOrSameGridSet

                        if (mIsOrientationVertical) {
                            row = positionInSameGridSet / mSpanCount
                            column = positionInSameGridSet % mSpanCount
                            rows = (positionOfLastOfSameViewTypeOrSameGridSet - positionOfFirstOfSameViewTypeOrSameGridSet) / spanCount
                            columns = (positionOfLastOfSameViewTypeOrSameGridSet - positionOfFirstOfSameViewTypeOrSameGridSet) % spanCount
                            if (isLtr) {
                                offsetLeft = if (column > 0) decoration.width / 2 else 0
                                offsetRight = if (column < columns) decoration.width / 2 else 0
                                offsetBottom = if (row < rows) decoration.width / 2 else 0
                                offsetTop = if (row > 0) decoration.width / 2 else 0
                            }else{
                                offsetRight = if (column > 0) decoration.width / 2 else 0
                                offsetLeft = if (column < columns) decoration.width / 2 else 0
                                offsetBottom = if (row < rows) decoration.width / 2 else 0
                                offsetTop = if (row > 0) decoration.width / 2 else 0
                            }
                            outRect.left = offsetLeft
                            outRect.top = offsetTop
                            outRect.right = offsetRight
                            outRect.bottom = offsetBottom

               /*
                            if (column > 0) {
                                if (mIsLtr) {
                                    outRect.left = decoration.width / 2
                                } else {
                                    outRect.right = decoration.width / 2
                                }
                            }
                            if (column < mSpanCount - 1) {
                                if (mIsLtr) {
                                    outRect.right = decoration.width / 2
                                } else {
                                    outRect.left = decoration.width / 2
                                }
                            }


                            row = positionInSameGridSet / mSpanCount
                            if (row < gridSetCount / mSpanCount) {
                                outRect.bottom = decoration.height
                            }*/
                        } else {
                            column = positionInSameGridSet / mSpanCount
                            if (column < gridSetCount / mSpanCount) {
                                if (mIsLtr) {
                                    outRect.right = decoration.width / 2
                                } else {
                                    outRect.left = decoration.width / 2
                                }
                            }
                            if (column > 0) {
                                if (mIsLtr) {
                                    outRect.left = decoration.width / 2
                                } else {
                                    outRect.right = decoration.width / 2
                                }
                            }
                            row = positionInSameGridSet % mSpanCount
                            if (row > 0) {
                                outRect.bottom = decoration.height
                            }

                        }
                    }
                },
                onNextViewType = {
                    mLinearDividersListener.getLastDividerDecoration(mViewType, mNextViewType)?.applyOffsetOnSpanSizeLookupEquals1(outRect)
                },
                onLastViewType = {
                    mLinearDividersListener.getLastDecoration(mViewType)?.applyOffsetOnSpanSizeLookupEquals1(outRect)
                },
                onGridBorderViewType = { viewType ->
                    mDecoration = mLinearDividersListener.getGridSideBorderDecoration(mViewType)
                            ?: return@onDivider
                    if (!(mSpanCount > 1 && mSpanSize != mSpanCount)) {
                        if (mIsOrientationVertical) {
                            outRect.left = decoration.width
                            outRect.right = decoration.width
                        } else {
                            outRect.top = decoration.height
                            outRect.bottom = decoration.height
                        }
                    } else {
                        positionInSameGridSet = mPosition - positionOfFirstOfSameViewTypeOrSameGridSet
                        if (mIsOrientationVertical) {
                            if (positionInSameGridSet % mSpanCount == 0) {
                                if (mIsLtr) {
                                    outRect.left = decoration.width
                                } else {
                                    outRect.right = decoration.width
                                }
                            } else if (positionInSameGridSet % mSpanCount == mSpanCount - 1) {
                                if (mIsLtr) {
                                    outRect.right = decoration.width
                                } else {
                                    outRect.left = decoration.width
                                }
                            }
                        } else {
                            if (positionInSameGridSet % mSpanCount == 0) {
                                if (mIsLtr) {
                                    outRect.top = decoration.height
                                } else {
                                    outRect.bottom = decoration.height
                                }
                            } else if (positionInSameGridSet % mSpanCount == mSpanCount - 1) {
                                if (mIsLtr) {
                                    outRect.bottom = decoration.height
                                } else {
                                    outRect.top = decoration.height
                                }
                            }
                        }
                    }
                }
        )
        cleanupGlobalResources()
        return outRect
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
    ) {
        for (i in 0 until parent.childCount) {
            if (!hasViewSetupSucceed(parent, parent.getChildAt(i))) {
                continue
            }

            onDivider(
                    onFirstViewType = {
                        mLinearDividersListener.getFirstDecoration(mViewType)?.drawOnSpanSizeLookupEquals1Previous(parent, canvas)
                    },
                    onPreviousViewType = {
                        mLinearDividersListener.getFirstDividerDecoration(mViewType, mPreviousViewType)?.drawOnSpanSizeLookupEquals1Previous(parent, canvas)
                    },
                    onSameViewType = {
                        mLinearDividersListener.getDividerDecoration(mViewType)?.drawOnSpanSizeLookupEquals1Next(parent, canvas)
                    },
                    onNextViewType = {
                        mLinearDividersListener.getLastDividerDecoration(mViewType, mNextViewType)?.drawOnSpanSizeLookupEquals1Next(parent, canvas)
                    },
                    onLastViewType = {
                        mLinearDividersListener.getLastDecoration(mViewType)?.drawOnSpanSizeLookupEquals1Next(parent, canvas)
                    },
                    onGridBorderViewType = { viewType ->
                        // not supported
                    }
            )
        }
    }

    /**
     * @return the next decoration at the given [position].
     */
    private fun onDivider(
            onFirstViewType: () -> Unit,
            onPreviousViewType: () -> Unit,
            onSameViewType: () -> Unit,
            onNextViewType: () -> Unit,
            onLastViewType: () -> Unit,
            onGridBorderViewType: (viewType: Int) -> Unit,
    ) {
        if (mSpanCount == 1) {
            if (mPosition == 0) {
                onFirstViewType()
            } else {
                mPreviousViewType = adapter.getItemViewType(mPosition - 1)
                if (mViewType != mPreviousViewType) {
                    onPreviousViewType()
                }
            }
            if (mPosition < adapter.itemCount - 1) {
                mNextViewType = adapter.getItemViewType(mPosition + 1)
                if (mViewType != mNextViewType) {
                    onNextViewType()
                } else {
                    onSameViewType()
                }
            }
            if (mPosition == adapter.itemCount - 1) {
                onLastViewType()
            }
        } else {
            if (mPosition < mSpanCount) {
                if (mPosition == 0) {
                    onFirstViewType()
                } else {
                    if ((0..mPosition).fold(0) { acc, i -> acc + getSpanSize(i) } <= mSpanCount) {
                        onFirstViewType()
                    }
                }
            }
            if (mPosition > 0) {

                if (mSpanSize == mSpanCount) {
                    mPreviousViewType = adapter.getItemViewType(mPosition - 1)
                    if (mViewType != mPreviousViewType) {
                        onPreviousViewType()
                    }
                } else {
                    if ((mPosition - positionOfFirstOfSameViewTypeOrSameGridSet) < mSpanCount && positionOfFirstOfSameViewTypeOrSameGridSet > 0) {
                        mPreviousViewType = adapter.getItemViewType(positionOfFirstOfSameViewTypeOrSameGridSet - 1)
                        onPreviousViewType()
                    }
                }
            }
            if (mPosition < adapter.itemCount - 1) {

                if (mSpanSize == mSpanCount) {
                    mNextViewType = adapter.getItemViewType(mPosition + 1)
                    if (mViewType != mNextViewType) {
                        onNextViewType()
                    } else {
                        onSameViewType()
                    }
                } else {
                    onSameViewType()
                    if (positionOfLastOfSameViewTypeOrSameGridSet < adapter.itemCount - 1 && (mPosition - positionOfFirstOfSameViewTypeOrSameGridSet) / mSpanCount == (positionOfLastOfSameViewTypeOrSameGridSet - positionOfFirstOfSameViewTypeOrSameGridSet) / mSpanCount) {
                        mNextViewType = adapter.getItemViewType(positionOfLastOfSameViewTypeOrSameGridSet + 1)
                        onNextViewType()
                    }
                }
            } else {
                if (mSpanSize < mSpanCount) {
                    onSameViewType()
                }
            }

            if (mPosition >= adapter.itemCount - mSpanCount) {
                if (mPosition == adapter.itemCount - 1) {
                    onLastViewType()
                } else if ((mPosition until adapter.itemCount).fold(0) { acc, i -> acc + getSpanSize(i) } <= mSpanCount) {
                    if ((mPosition - positionOfFirstOfSameViewTypeOrSameGridSet) / mSpanCount == (positionOfLastOfSameViewTypeOrSameGridSet - positionOfFirstOfSameViewTypeOrSameGridSet) / mSpanCount) {
                        onLastViewType()
                    }
                }
            }
            onGridBorderViewType(mViewType)
        }
    }

    /**
     * Draw drawable above the View
     */
    private fun Decoration.drawOnSpanSizeLookupEquals1Previous(parent: RecyclerView, canvas: Canvas) {
        when (this) {
            is Decoration.Drawable -> {
                // if rtl, we support it
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    drawable.layoutDirection = parent.layoutDirection
                }
                if (mIsOrientationVertical) {
                    /*
                     *
                     * ----------------------
                     *      Decoration
                     * ----------------------
                     * //////////////////////
                     *        View
                     * //////////////////////
                     *
                     */
                    drawable.setBounds(
                            0,
                            view.top - view.marginTop - drawable.intrinsicHeight,
                            view.right + view.marginEnd,
                            view.top - view.marginTop
                    )
                    drawable.draw(canvas)
                } else {
                    /*
                     *  _____________  _______
                     * |             |\       \
                     * |             |\       \
                     * | Decoration  |\ View  \
                     * |             |\       \
                     * |             |\       \
                     * |_____________|\_______\
                     *
                     */
                    drawable.setBounds(
                            view.left - view.marginStart - drawable.intrinsicWidth,
                            0,
                            view.left - view.marginStart,
                            view.bottom + view.marginBottom
                    )
                    drawable.draw(canvas)
                }
            }
        }
    }

    private fun Decoration.drawOnSpanSizeLookupEquals1Next(parent: RecyclerView, canvas: Canvas) {
        when (this) {
            is Decoration.Drawable -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    drawable.layoutDirection = parent.layoutDirection
                }
                if (mIsOrientationVertical) {
                    /*
                    * //////////////////////
                    *        View
                    * //////////////////////
                    * ----------------------
                    *      Decoration
                    * ----------------------
                    *
                    */
                    drawable.setBounds(
                            0,
                            view.bottom + view.marginBottom,
                            view.right + view.marginEnd,
                            view.bottom + view.marginBottom + drawable.intrinsicHeight
                    )
                    drawable.draw(canvas)
                } else {
                    /*
                     *  _______  _____________
                     * \       \|             |
                     * \       \|             |
                     * \ View  \| Decoration  |
                     * \       \|             |
                     * \       \|             |
                     * \_______\|_____________|
                     */
                    drawable.setBounds(
                            view.right + view.marginEnd,
                            0,
                            view.right + view.marginEnd + drawable.intrinsicWidth,
                            view.bottom + view.marginBottom
                    )
                    drawable.draw(canvas)
                }
            }
        }
    }

    private fun Decoration.applyOffsetOnSpanSizeLookupEquals1(outRect: Rect, reverse: Boolean = false) = run {
        if (mIsOrientationVertical) {
            if (reverse) {
                outRect.top = height
            } else {
                outRect.bottom = height
            }
        } else {
            if (mIsLtr) {
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
        }
    }

    private fun positionOfFirstSpanSizeNotEqualsSpanCount(): Int = (mPosition downTo 0).firstOrNull { i -> getSpanSize(i) == mSpanCount }?.let { it + 1 }
            ?: 0

    private fun positionOfLastSpanSizeNotEqualsSpanCount(): Int = (mPosition until adapter.itemCount).firstOrNull { i -> getSpanSize(i) == mSpanCount }?.let { it - 1 }
            ?: (adapter.itemCount - 1)

    private fun positionOfFirstSameViewType(): Int = (mPosition downTo 0).firstOrNull { i -> adapter.getItemViewType(i) != mViewType }?.let { it + 1 }
            ?: 0

    private fun positionOfLastSameViewType(): Int = (mPosition until adapter.itemCount).firstOrNull { i -> adapter.getItemViewType(i) != mViewType }?.let { it - 1 }
            ?: (adapter.itemCount - 1)

}