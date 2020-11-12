package com.sokarcreative.basicstuffrecyclerview.divider

import android.graphics.drawable.Drawable
import com.sokarcreative.basicstuffrecyclerview.Decoration

/**
 * Created by sokarcreative on 17/04/2018.
 */
interface LinearDividersListener {

    /**
     * @property viewType The current viewType.
     * @return decoration before the first viewHolder which has the given [viewType].
     */
    fun getFirstDecoration(viewType: Int): Decoration? = null

    /**
     * @property viewType The current viewType.
     * @property previousViewType The previous viewType.
     * @return decoration between [viewType] and [previousViewType]
     */
    fun getFirstDividerDecoration(viewType: Int, previousViewType: Int): Decoration? = null

    /**
     * @property viewType The current viewType.
     * @return divider decoration between each viewHolder with the same [viewType].
     */
    fun getDividerDecoration(viewType: Int): Decoration? = null

    /**
     * @property viewType The current viewType.
     * @return grid border decoration between around viewHolder with the same [viewType].
     */
    fun getGridSideBorderDecoration(viewType: Int): Decoration? = null

    /**
     * @property viewType The current viewType.
     * @property nextViewType The next viewType.
     * @return decoration between [viewType] and [nextViewType].
     */
    fun getLastDividerDecoration(viewType: Int, nextViewType: Int): Decoration? = null

    /**
     * @property viewType The current viewType.
     * @return decoration after the last viewHolder which has the given [viewType].
     */
    fun getLastDecoration(viewType: Int): Decoration? = null

}