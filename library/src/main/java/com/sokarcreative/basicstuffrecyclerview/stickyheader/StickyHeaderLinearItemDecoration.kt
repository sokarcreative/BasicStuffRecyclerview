package com.sokarcreative.basicstuffrecyclerview.stickyheader

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StickyHeaderLinearItemDecoration(val linearStickyHeadersListener: LinearStickyHeadersListener) : RecyclerView.ItemDecoration(), RecyclerView.OnItemTouchListener {

    private var mCurrentWidthSpec = 0
    private var mStickyHeaderInfo: StickyHeaderInfo? = null

    /**
     * Draw a sticky view into the Canvas at the top of a RecyclerView.
     *
     * @param canvas Canvas to draw into
     * @param parent RecyclerView this ItemDecoration is drawing into
     * @param state The current state of RecyclerView.
     */
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)

        mStickyHeaderInfo = null

        val adapter = parent.adapter ?: return

        // Sticky Header supports only VERTICAL orientation of  LinearLayoutManager
        if (parent.layoutManager !is LinearLayoutManager || (parent.layoutManager as LinearLayoutManager).orientation != LinearLayoutManager.VERTICAL) {
            return
        }

        // if no visible top view, draw nothing
        val currentVisibleTopView = parent.getChildAt(0 /* 0: means top visible view in recyclerview*/)
                ?: return

        // if for some reasons, no position was found for this top visible view, draw nothing
        val currentVisibleTopViewAdapterPosition = parent.getChildAdapterPosition(currentVisibleTopView).takeIf { it != RecyclerView.NO_POSITION }
                ?: return

        // if first position and there is some top margin available above and no top sticky header, draw nothing
        if ((currentVisibleTopViewAdapterPosition == 0 || closestStickyHeaderPosition(parent, currentVisibleTopViewAdapterPosition - 1) == RecyclerView.NO_POSITION) && linearStickyHeadersListener.isStickyHeader(adapter.getItemViewType(currentVisibleTopViewAdapterPosition)) && currentVisibleTopView.top > 0) {
            return
        }

        // Track currentStickyHeader
        val currentStickyHeaderInfo: StickyHeaderInfo = closestStickyHeaderPosition(parent, currentVisibleTopViewAdapterPosition.takeIf { it == 0 || currentVisibleTopView.top < 0 }
                ?: currentVisibleTopViewAdapterPosition - 1).takeIf { it != RecyclerView.NO_POSITION }?.let { generateNewStickyHeader(parent, it) }
                ?: return

        // Draw current sticky header but check if no conflict with the next sticky header if he exists
        currentStickyHeaderInfo.drawStickyView(canvas, parent, currentStickyHeaderInfo, findNextClosestStickyHeaderView(adapter, parent, currentStickyHeaderInfo))
    }


    /**
     * Find the next closest sticky header below the currentStickyHeaderInfo
     * @param adapter
     * @param parent
     * @param currentStickyHeaderInfo  current sticky header
     */
    private fun findNextClosestStickyHeaderView(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, parent: RecyclerView, currentStickyHeaderInfo: StickyHeaderInfo): View? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child.top > currentStickyHeaderInfo.stickyView.bottom) {
                break
            }
            if (child.top >= currentStickyHeaderInfo.stickyView.top && child.top <= currentStickyHeaderInfo.stickyView.bottom) {
                val positionViewChildBehindStickyView = parent.getChildAdapterPosition(child)
                if (positionViewChildBehindStickyView != RecyclerView.NO_POSITION && parent.getChildViewHolder(child) != null && linearStickyHeadersListener.isStickyHeader(adapter.getItemViewType(positionViewChildBehindStickyView))) {
                    return child
                }
            }
        }
        return null
    }


    /**
     * Called when the view at the top of the recyclerview is not a sticky view.
     *
     * @param currentPositionInAdapter the current position in the adapter we get at the top of the recyclerview.
     * @return the previous sticky header position in adapter.
     */
    private fun closestStickyHeaderPosition(parent: RecyclerView, currentPositionInAdapter: Int): Int {
        val adapter = parent.adapter!!
        return (currentPositionInAdapter downTo 0).firstOrNull { position ->
            linearStickyHeadersListener.isStickyHeader(adapter.getItemViewType(position))
        } ?: RecyclerView.NO_POSITION
    }

    /**
     * Called when the top of a potential sticky view is touching the top of the recyclerview.
     *
     * @param parent RecyclerView this ItemDecoration is drawing into.
     * @param position Adapter position of the new sticky view.
     */
    private fun generateNewStickyHeader(parent: RecyclerView, position: Int): StickyHeaderInfo {
        val stickyView = linearStickyHeadersListener.onCreateAndBindStickyView(parent, position)

        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY) - stickyView.marginStart - stickyView.marginEnd - parent.paddingLeft - parent.paddingRight
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)
        mCurrentWidthSpec = widthSpec

        val childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, 0, stickyView.layoutParams.width)
        val childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, 0, stickyView.layoutParams.height)

        stickyView.measure(childWidthSpec, childHeightSpec)
        stickyView.layout(0, 0, stickyView.measuredWidth, stickyView.measuredHeight)

        return StickyHeaderInfo(position, stickyView).also {
            mStickyHeaderInfo = it
        }
    }


    internal inner class StickyHeaderInfo(val adapterPosition: Int, val stickyView: View) {

        internal var visibleHeightOnScreen: Int = 0

        /**
         * Draw the sticky view at the top of the recyclerview.
         *
         * @param canvas Canvas to draw into.
         * @param nextStickyHeader if null just draw in 0,0 otherwhise need to make a translation in y.
         */
        internal fun drawStickyView(canvas: Canvas, parent: RecyclerView, stickyHeaderInfo: StickyHeaderInfo, nextStickyHeader: View?) {
            canvas.save()
            canvas.translate(parent.paddingLeft + stickyView.marginStart.toFloat(), nextStickyHeader?.let { (nextStickyHeader.top - stickyView.height).toFloat() }
                    ?: 0f)
            stickyHeaderInfo.visibleHeightOnScreen = nextStickyHeader?.top ?: stickyView.height
            stickyView.draw(canvas)
            canvas.restore()
        }
    }


    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        mStickyHeaderInfo?.let { stickyHeaderInfo ->
            if (e.y.toInt() in 0..stickyHeaderInfo.visibleHeightOnScreen) {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if(mTimeBeforeNextClick < System.currentTimeMillis()){
                            mInterceptTouchEventInfo = InterceptTouchEventInfo(stickyHeaderInfo, System.currentTimeMillis())
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        mInterceptTouchEventInfo?.let { interceptTouchEventInfo ->
                            if (stickyHeaderInfo.adapterPosition == interceptTouchEventInfo.stickyHeaderInfo.adapterPosition && System.currentTimeMillis() - interceptTouchEventInfo.actionDownTime < MAX_DURATION_BETWEEN_ACTION_DOWN_AND_ACTION_UP) {
                                linearStickyHeadersListener.onStickyViewClick(rv, stickyHeaderInfo.adapterPosition)
                                stickyHeaderInfo.stickyView.performClick()
                                mInterceptTouchEventInfo = null
                                mTimeBeforeNextClick = System.currentTimeMillis() + MIN_DURATION_BETWEEN_TWO_CLICKS
                                return true
                            }
                        }
                        return true
                    }
                }
            } else {
                mInterceptTouchEventInfo = null
            }
        }
        return false
    }

    private class InterceptTouchEventInfo(val stickyHeaderInfo: StickyHeaderInfo, val actionDownTime: Long)
    private var mTimeBeforeNextClick: Long = 0
    private var mInterceptTouchEventInfo: InterceptTouchEventInfo? = null

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }

    companion object {
        const val MAX_DURATION_BETWEEN_ACTION_DOWN_AND_ACTION_UP: Int = 1000
        const val MIN_DURATION_BETWEEN_TWO_CLICKS: Int = 300
    }

}