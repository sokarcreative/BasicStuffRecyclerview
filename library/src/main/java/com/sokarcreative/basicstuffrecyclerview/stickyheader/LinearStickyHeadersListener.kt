package com.sokarcreative.basicstuffrecyclerview.stickyheader

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import java.lang.IllegalStateException

/**
 * Created by sokarcreative on 17/04/2018.
 */
interface LinearStickyHeadersListener {

    /**
     * @return true if the [viewType] should be a sticky header.
     */
    fun isStickyHeader(viewType: Int): Boolean {
        return false
    }

    /**
     * Create and bind a sticky view of the [position] and add to [parent] viewGroup. You can either create a new View manually or inflate it from an XML layout file.
     * @return the new stickyView created and binded.
     */
    fun onCreateAndBindStickyView(parent: RecyclerView, position: Int): View {
        parent.adapter?.let { adapter ->
            val holder: RecyclerView.ViewHolder = adapter.onCreateViewHolder(parent, adapter.getItemViewType(position))
            adapter.onBindViewHolder(holder, position)
            return holder.itemView
        } ?: throw IllegalStateException("no adapter")
    }

    /**
     * Click event on Sticky View at the given [position].
     * @property parent the current recyclerView.
     */
    fun onStickyViewClick(parent: RecyclerView, position: Int) {
        parent.scrollToPosition(position)
    }
}