package com.sokarcreative.library

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
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
class BasicStuffItemDecoration(adapter: BasicStuffAdapter) : RecyclerView.ItemDecoration(), RecyclerView.OnItemTouchListener {

    private var mStickyHeaderInfo: StickyHeaderInfo? = null
    private var mCurrentWidthSpec = 0

    private var mOrientation: Int = 0

    private var pressStartTime: Long = 0
    private var stickyPosition: Int = 0
    private var prepareToTouchEvent: Boolean = false


    init {
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                mStickyHeaderInfo = null
            }
        })
    }

    /**
     * Draw a sticky view into the Canvas at the top of a RecyclerView.
     *
     * @param canvas Canvas to draw into
     * @param parent RecyclerView this ItemDecoration is drawing into
     * @param state The current state of RecyclerView.
     */
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        val adapter = parent.adapter
        if (adapter == null || adapter !is BasicStuffAdapter) {
            Log.e(TAG, "BasicStuffAdapter is missing")
            return
        }
        if (parent.layoutManager !is LinearLayoutManager) {
            Log.e(TAG, "LinearLayoutManager is missing")
            return
        }
        if ((parent.layoutManager as LinearLayoutManager).orientation != LinearLayoutManager.VERTICAL) {
            return
        }

        val currentTopView = parent.getChildAt(0) ?: return
        val currentPosition = parent.getChildAdapterPosition(currentTopView)
        if (currentPosition == RecyclerView.NO_POSITION) {
            return
        }

        if (currentPosition == 0 && adapter.isStickyHeader(adapter.getItemViewType(currentPosition)) && currentTopView.top > 0) {
            return
        }
        if (mStickyHeaderInfo == null) {
            val positionPreviousStickyChild = findTopHeader(parent, currentPosition)
            if (positionPreviousStickyChild != RecyclerView.NO_POSITION) {
                generateNewStickyHeader(parent, positionPreviousStickyChild)
            }
        }
        if (mStickyHeaderInfo != null) {
            val topStickyViewContactPoint = mStickyHeaderInfo!!.stickyView.top
            val bottomStickyViewContactPoint = mStickyHeaderInfo!!.stickyView.bottom + mStickyHeaderInfo!!.dividerHeight

            var nextStickyChild: View? = null
            var positionNextStickyChild = RecyclerView.NO_POSITION
            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                if (child.top > topStickyViewContactPoint && child.top < bottomStickyViewContactPoint) {
                    val positionViewChildBehindStickyView = parent.getChildAdapterPosition(child)
                    if (positionViewChildBehindStickyView != RecyclerView.NO_POSITION && parent.getChildViewHolder(child) != null && adapter.isStickyHeader(adapter.getItemViewType(positionViewChildBehindStickyView))) {
                        nextStickyChild = child
                        positionNextStickyChild = positionViewChildBehindStickyView
                        break
                    }
                }

                if (child.top > bottomStickyViewContactPoint) {
                    break
                }
            }

            if (nextStickyChild != null) {
                if (positionNextStickyChild == currentPosition) {
                    val nextStickyChildDrawable = getNextDecoration(parent, adapter.getItemViewType(positionNextStickyChild))
                    val nextStickyChildDrawableHeight = nextStickyChildDrawable?.intrinsicHeight ?: 0
                    if (nextStickyChild.bottom >= currentTopView.bottom + nextStickyChildDrawableHeight) {
                        generateNewStickyHeader(parent, positionNextStickyChild)
                        drawStickyView(canvas, parent)
                    } else {
                        moveStickyView(canvas, parent, nextStickyChild)
                    }
                } else {
                    val positionPreviousStickyChild = findTopHeader(parent, currentPosition)
                    if (positionPreviousStickyChild != RecyclerView.NO_POSITION) {
                        generateNewStickyHeader(parent, positionPreviousStickyChild)
                        moveStickyView(canvas, parent, nextStickyChild)
                    } else {
                        mStickyHeaderInfo = null
                    }
                }
            } else {
                val positionPreviousStickyChild = findTopHeader(parent, currentPosition)
                if (positionPreviousStickyChild != RecyclerView.NO_POSITION) {
                    generateNewStickyHeader(parent, positionPreviousStickyChild)
                    drawStickyView(canvas, parent)
                } else {
                    mStickyHeaderInfo = null
                }
            }
        }
    }


    /**
     * Called when the view at the top of the recyclerview is not a sticky view.
     *
     * @param currentPositionInAdapter the current position in the adapter we get at the top of the recyclerview.
     * @return the previous sticky header position in adapter.
     */
    private fun findTopHeader(parent: RecyclerView, currentPositionInAdapter: Int): Int {
        val adapter = parent.adapter as? BasicStuffAdapter

        return adapter?.let { (currentPositionInAdapter downTo 0).firstOrNull { position -> adapter.isStickyHeader(adapter.getItemViewType(position)) } ?: RecyclerView.NO_POSITION } ?: RecyclerView.NO_POSITION
    }

    /**
     * Called when the top of a potential sticky view is touching the top of the recyclerview.
     *
     * @param parent RecyclerView this ItemDecoration is drawing into.
     * @param position Adapter position of the new sticky view.
     */
    private fun generateNewStickyHeader(parent: RecyclerView, position: Int) {
        if(mCurrentWidthSpec != View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY) || mStickyHeaderInfo == null || position != mStickyHeaderInfo!!.position){
            val stickyView = (parent.adapter as BasicStuffAdapter).onCreateAndBindStickyView(parent, position)

            resizeStickyHeader(parent, stickyView)

            val divider = getNextDecoration(parent, position)

            var dividerHeight = 0
            if (divider != null) {
                dividerHeight = divider.intrinsicHeight
            }

            mStickyHeaderInfo = StickyHeaderInfo(position, stickyView, divider, dividerHeight)
        }
    }

    /**
     * Move the current sticky according to the next potential futur sticky view.
     *
     * @param canvas Canvas to draw into.
     * @param parent RecyclerView this ItemDecoration is drawing into.
     * @param nextFuturStickyHeader next potential futur sticky view.
     */
    private fun moveStickyView(canvas: Canvas, parent: RecyclerView, nextFuturStickyHeader: View) {
        mStickyHeaderInfo?.let {
            canvas.save()
            canvas.translate(0f, (nextFuturStickyHeader.top - (it.stickyView.height + it.dividerHeight)).toFloat())
            it.stickyView.draw(canvas)
            it.visibleHeight = nextFuturStickyHeader.top
            val divider = it.divider
            if (divider != null) {
                val parentLeft = parent.paddingLeft
                val parentRight = parent.width - parent.paddingRight
                drawVerticalDivider(canvas, it.stickyView, divider, parentLeft, parentRight)
            }
            canvas.restore()
        }
    }

    /**
     * Draw the sticky view at the top of the recyclerview.
     *
     * @param canvas Canvas to draw into.
     * @param parent RecyclerView this ItemDecoration is drawing into.
     */
    private fun drawStickyView(canvas: Canvas, parent: RecyclerView) {
        mStickyHeaderInfo?.let {
            canvas.save()
            canvas.translate(0f, 0f)
            it.stickyView.draw(canvas)
            it.visibleHeight = it.stickyView.height
            val divider = it.divider
            if (divider != null) {
                val parentLeft = parent.paddingLeft
                val parentRight = parent.width - parent.paddingRight
                drawVerticalDivider(canvas, it.stickyView, divider, parentLeft, parentRight)
            }
            canvas.restore()
        }
    }

    /**
     * Resize real width/height of the sticky view.
     *
     * @param parent the current recyclerview.
     * @param view the sticky view.
     */
    private fun resizeStickyHeader(parent: ViewGroup, view: View) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        mCurrentWidthSpec = widthSpec
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
        val childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, parent.paddingTop + parent.paddingBottom, view.layoutParams.height)

        view.measure(childWidthSpec, childHeightSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    /**
     * Draw dividers.
     *
     * @param canvas Canvas to draw into.
     * @param parent RecyclerView this ItemDecoration is drawing into.
     * @param state The current state of RecyclerView.
     */
    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.adapter == null || parent.adapter !is BasicStuffAdapter) {
            Log.e(TAG, "BasicStuffAdapter is missing")
            return
        }
        if (parent.layoutManager !is LinearLayoutManager) {
            Log.e(TAG, "LinearLayoutManager is missing")
            return
        }

        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            drawHorizontalDividers(canvas, parent)
        } else if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVerticalDividers(canvas, parent)
        }
    }

    /**
     *
     * @param outRect Rect to receive the output.
     * @param view    The child view to decorate
     * @param parent  RecyclerView this ItemDecoration is decorating
     * @param state   The current state of RecyclerView.
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.adapter == null || parent.adapter !is BasicStuffAdapter) {
            Log.e(TAG, "BasicStuffAdapter is missing")
            return
        }
        if (parent.layoutManager !is LinearLayoutManager) {
            Log.e(TAG, "LinearLayoutManager is missing")
            return
        }

        val viewHolder = parent.getChildViewHolder(view)
        val position: Int = parent.getChildAdapterPosition(view)
        if (viewHolder != null && position != RecyclerView.NO_POSITION) {
            if (parent.layoutManager is LinearLayoutManager) {
                mOrientation = (parent.layoutManager as LinearLayoutManager).orientation

                val divider = getNextDecoration(parent, position)
                if (divider != null) {
                    if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                        outRect.right = divider.intrinsicWidth
                    } else if (mOrientation == LinearLayoutManager.VERTICAL) {
                        outRect.bottom = divider.intrinsicHeight
                    }
                }

                val drawableFirstDecoration = getPreviousDecoration(parent, position)
                if (drawableFirstDecoration != null) {
                    if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                        outRect.left = drawableFirstDecoration.intrinsicWidth

                    } else if (mOrientation == LinearLayoutManager.VERTICAL) {
                        outRect.top = drawableFirstDecoration.intrinsicHeight
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
    private fun drawHorizontalDividers(canvas: Canvas, parent: RecyclerView) {
        val parentTop = parent.paddingTop
        val parentBottom = parent.height - parent.paddingBottom

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val viewHolder = parent.getChildViewHolder(view)
            val position: Int = parent.getChildAdapterPosition(view)
            if (viewHolder != null && position != RecyclerView.NO_POSITION) {
                val divider = getNextDecoration(parent, position)
                if (divider != null) drawHorizontalDivider(canvas, view, divider, parentTop, parentBottom)
                val drawableFirstDecoration = getPreviousDecoration(parent, position)
                if (drawableFirstDecoration != null) drawHorizontalFirstDecoration(canvas, view, drawableFirstDecoration, parentTop, parentBottom)
            }
        }
    }

    /**
     * Draw vertical dividers.
     *
     * @param canvas Canvas to draw into.
     * @param parent RecyclerView this ItemDecoration is drawing into.
     */
    private fun drawVerticalDividers(canvas: Canvas, parent: RecyclerView) {
        val parentLeft = parent.paddingLeft
        val parentRight = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val viewHolder = parent.getChildViewHolder(view)
            val position: Int = parent.getChildAdapterPosition(view)
            if (viewHolder != null && position != RecyclerView.NO_POSITION) {
                val divider = getNextDecoration(parent, position)
                if (divider != null) drawVerticalDivider(canvas, view, divider, parentLeft, parentRight)
                val drawableFirstDecoration = getPreviousDecoration(parent, position)
                if (drawableFirstDecoration != null) drawVerticalFirstDecoration(canvas, view, drawableFirstDecoration, parentLeft, parentRight)
            }
        }
    }

    override fun onInterceptTouchEvent(parent: RecyclerView, e: MotionEvent): Boolean {
        if (mStickyHeaderInfo != null) {
            if (e.y <= mStickyHeaderInfo!!.visibleHeight && e.y > 0) {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        pressStartTime = System.currentTimeMillis()
                        prepareToTouchEvent = true
                        stickyPosition = mStickyHeaderInfo!!.position
                    }
                    MotionEvent.ACTION_UP -> {
                        val pressDuration = System.currentTimeMillis() - pressStartTime
                        if (prepareToTouchEvent && pressDuration < MAX_CLICK_DURATION && stickyPosition == mStickyHeaderInfo!!.position) {
                            (parent.adapter as BasicStuffAdapter).onStickyViewClick(parent, mStickyHeaderInfo!!.position)
                            return true
                        }
                    }
                }
            } else {
                prepareToTouchEvent = false
            }
        }
        return false
    }

    /**
     * Process a touch event as part of a gesture that was claimed by returning true from
     * a previous call to [.onInterceptTouchEvent].
     *
     * @param rv
     * @param e  MotionEvent describing the touch event. All coordinates are in
     */
    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }

    /**
     * Called when a child of RecyclerView does not want RecyclerView and its ancestors to
     * intercept touch events with
     * [ViewGroup.onInterceptTouchEvent].
     *
     * @param disallowIntercept True if the child does not want the parent to
     * intercept touch events.
     */
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }

    inner class StickyHeaderInfo internal constructor(val position: Int, internal val stickyView: View, internal val divider: Drawable?, internal val dividerHeight: Int) {
        var visibleHeight: Int = 0
            internal set

        init {
            visibleHeight = stickyView.bottom
        }
    }

    /**
     * @return the next decoration at the given [position].
     */
    private fun getNextDecoration(parent: RecyclerView, position: Int): Drawable? {
        val viewType = parent.adapter!!.getItemViewType(position)
        return if (position + 1 < parent.adapter!!.itemCount) {
            val nextViewType: Int = parent.adapter!!.getItemViewType(position + 1)
            if (viewType != nextViewType) {
                (parent.adapter as BasicStuffAdapter).getLastDividerDecoration(viewType, nextViewType)
            } else {
                (parent.adapter as BasicStuffAdapter).getDividerDecoration(viewType)
            }
        } else {
            (parent.adapter as BasicStuffAdapter).getLastDecoration(viewType)
        }
    }

    /**
     * @return the previous decoration at the given [position].
     */
    private fun getPreviousDecoration(parent: RecyclerView, position: Int): Drawable? {
        val viewType = parent.adapter!!.getItemViewType(position)
        return if (position > 0) {
            val previousViewType: Int = parent.adapter!!.getItemViewType(position - 1)
            if (viewType != previousViewType) {
                (parent.adapter as BasicStuffAdapter).getFirstDividerDecoration(viewType, previousViewType)
            } else {
                null
            }
        } else {
            (parent.adapter as BasicStuffAdapter).getFirstDecoration(viewType)
        }
    }


    companion object {

        private val TAG = "BasicStuff"

        private fun drawHorizontalFirstDecoration(canvas: Canvas, view: View, firstDecoration: Drawable, parentTop: Int, parentBottom: Int) {

            val parentLeft = (view.left - firstDecoration.intrinsicWidth + view.translationX).toInt()
            val parentRight = parentLeft

            firstDecoration.setBounds(parentLeft, parentTop, parentRight, parentBottom)
            firstDecoration.draw(canvas)
        }

        private fun drawHorizontalDivider(canvas: Canvas, view: View, divider: Drawable, parentTop: Int, parentBottom: Int) {

            val parentLeft = (view.right + view.translationX).toInt()
            val parentRight = parentLeft + divider.intrinsicWidth

            divider.setBounds(parentLeft, parentTop, parentRight, parentBottom)
            divider.draw(canvas)
        }


        private fun drawVerticalFirstDecoration(canvas: Canvas, view: View, firstDecoration: Drawable, parentLeft: Int, parentRight: Int) {

            val parentTop = (view.top - firstDecoration.intrinsicHeight + view.translationY).toInt()
            val parentBottom = view.top

            firstDecoration.setBounds(parentLeft, parentTop, parentRight, parentBottom)
            firstDecoration.draw(canvas)
        }

        private fun drawVerticalDivider(canvas: Canvas, view: View, divider: Drawable, parentLeft: Int, parentRight: Int) {

            val parentTop = (view.bottom + view.translationY).toInt()
            val parentBottom = parentTop + divider.intrinsicHeight

            divider.setBounds(parentLeft, parentTop, parentRight, parentBottom)
            divider.draw(canvas)
        }


        /**
         * Max allowed duration for a "click", in milliseconds.
         */
        private val MAX_CLICK_DURATION = 1000
    }

}