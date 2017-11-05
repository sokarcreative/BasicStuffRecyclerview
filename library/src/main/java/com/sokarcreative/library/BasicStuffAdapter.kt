package com.sokarcreative.library

import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View

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
 abstract class BasicStuffAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    /************
     * Dividers *
     ************/

    /**
     * @return the decoration at the top of the first viewHolder which has the given [viewType].
     */
    open fun getFirstDecoration(viewType: Int) : Drawable? {
        return null
    }

    /**
     * The top divider decoration between two viewHolder with different viewTypes. The current [viewType] and the [previousViewType].
     * @property viewType The current viewType
     * @property previousViewType The previous viewType
     * @return the bottom divider decoration between two viewHolder with different viewTypes.
     */
    open fun getFirstDividerDecoration(viewType: Int, previousViewType: Int) : Drawable? {
        return null
    }

    /**
     * @return the divider decoration between each viewHolder with the same [viewType].
     */
    open fun getDividerDecoration(viewType: Int) : Drawable? {
        return null
    }

    /**
     * The bottom divider decoration between two viewHolder with different viewTypes. The current [viewType] and the [nextViewType].
     * @property viewType The current viewType
     * @property nextViewType The next viewType
     * @return the bottom divider decoration between two viewHolder with different viewTypes.
     */
    open fun getLastDividerDecoration(viewType: Int, nextViewType: Int) : Drawable? {
        return null
    }

    /**
     * @return the decoration at the bottom of the last viewHolder which has the given [viewType].
     */
    open fun getLastDecoration(viewType: Int) : Drawable? {
        return null
    }

    /******************
     * Sticky headers *
     ******************/

    /**
     * @return true if the [viewType] should be a sticky header.
     */
    open fun isStickyHeader(viewType: Int): Boolean {
        return false
    }

    /**
     * Create and bind a sticky view of the [position] and add to [parent] viewGroup. You can either create a new View manually or inflate it from an XML layout file.
     * @return the new stickyView created and binded.
     */
    open fun onCreateAndBindStickyView(parent: RecyclerView, position: Int): View {
        val holder : RecyclerView.ViewHolder = parent.adapter.onCreateViewHolder(parent, parent.adapter.getItemViewType(position))
        parent.adapter.onBindViewHolder(holder, position)
        return holder.itemView
    }

    /**
     * Click event on Sticky View at the given [position].
     * @property parent the current recyclerView.
     */
    open fun onStickyViewClick(parent: RecyclerView, position: Int) {
        parent.scrollToPosition(position)
    }

    /********
     * Drag *
     ********/

    open fun getItems() : MutableList<Any>?{
        return null
    }

    open fun isDraggable(viewType: Int) : Boolean{
        return false
    }

    open fun isHeader(viewType: Int) : Boolean{
        return false
    }

    open fun allowMove(viewTypeDraggable: Int, headerViewType: Int) : Boolean{
        return false
    }
    open fun allowOnlyForSameHeaderAsSource(viewTypeDraggable : Int, headerViewType: Int) : Boolean{
        return false
    }

}
