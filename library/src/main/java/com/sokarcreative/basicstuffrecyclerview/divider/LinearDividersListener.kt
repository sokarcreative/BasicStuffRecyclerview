package com.sokarcreative.basicstuffrecyclerview.divider

import android.graphics.drawable.Drawable

/**
 * Created by sokarcreative on 17/04/2018.
 */
interface LinearDividersListener {

    /**
     * @return the decoration at the top of the first viewHolder which has the given [viewType].
     */
    fun getFirstDecoration(viewType: Int): Drawable? {
        return null
    }

    /**
     * The top divider decoration between two viewHolder with different viewTypes. The current [viewType] and the [previousViewType].
     * @property viewType The current viewType
     * @property previousViewType The previous viewType
     * @return the bottom divider decoration between two viewHolder with different viewTypes.
     */
    fun getFirstDividerDecoration(viewType: Int, previousViewType: Int): Drawable? {
        return null
    }

    /**
     * @return the divider decoration between each viewHolder with the same [viewType].
     */
    fun getDividerDecoration(viewType: Int): Drawable? {
        return null
    }

    /**
     * The bottom divider decoration between two viewHolder with different viewTypes. The current [viewType] and the [nextViewType].
     * @property viewType The current viewType
     * @property nextViewType The next viewType
     * @return the bottom divider decoration between two viewHolder with different viewTypes.
     */
    fun getLastDividerDecoration(viewType: Int, nextViewType: Int): Drawable? {
        return null
    }

    /**
     * @return the decoration at the bottom of the last viewHolder which has the given [viewType].
     */
    fun getLastDecoration(viewType: Int): Drawable? {
        return null
    }
}