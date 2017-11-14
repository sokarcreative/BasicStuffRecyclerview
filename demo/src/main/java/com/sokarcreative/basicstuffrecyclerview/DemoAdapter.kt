package com.sokarcreative.basicstuffrecyclerview

import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.sokarcreative.basicstuffrecyclerview.models.Message
import com.sokarcreative.basicstuffrecyclerview.models.TitleH
import com.sokarcreative.library.BasicStuffAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_holder_content.view.*
import kotlinx.android.synthetic.main.view_holder_title.view.*
import java.util.*

/**
* Created by sokarcreative on 29/10/2017.
*/
class DemoAdapter(private var objects: ArrayList<Any>, private val activity: MainActivity) : BasicStuffAdapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_CONTENT = 0
    private val VIEW_TYPE_TITLE_H1 = 1
    private val VIEW_TYPE_TITLE_H2 = 2
    private val VIEW_TYPE_TITLE_H3 = 3

    fun refresh(items: ArrayList<Any>) {
        this.objects = items
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val `object` = objects[position]
        return if (`object` is Message) {
            VIEW_TYPE_CONTENT
        } else {
            when ((`object` as TitleH).titleType) {
                TitleH.H3 -> VIEW_TYPE_TITLE_H3
                TitleH.H2 -> VIEW_TYPE_TITLE_H2
                TitleH.H1 -> VIEW_TYPE_TITLE_H1
                else -> VIEW_TYPE_TITLE_H3
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_CONTENT) {
            ContentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_holder_content, parent, false))
        } else {
            TitleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_holder_title, parent, false), viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_CONTENT) {
            onBindContentViewHolder(holder as ContentViewHolder, position)
        } else {
            onBindTitleViewHolder(holder as TitleViewHolder, position)
        }
    }

    private fun onBindTitleViewHolder(holder: TitleViewHolder, position: Int) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = (objects[position] as TitleH).timeInMillis
        when (getItemViewType(position)) {
            VIEW_TYPE_TITLE_H3 -> holder.itemView.textViewTitle.text = Util.simpleDateFormatDay.format(calendar.time)
            VIEW_TYPE_TITLE_H2 -> holder.itemView.textViewTitle.text = Util.simpleDateFormatMonth.format(calendar.time)
            else -> holder.itemView.textViewTitle.text = Util.simpleDateFormatYear.format(calendar.time)
        }
    }

    private fun onBindContentViewHolder(holder: ContentViewHolder, position: Int) {
        val message = objects[position] as Message
        holder.itemView.textViewAuthor.text = activity.getString(R.string.author_x, message.author)
        holder.itemView.textViewMessage!!.text = activity.getString(R.string.message_x, message.message)
    }

    override fun getItemCount(): Int {
        return objects.size
    }

    /**
     * Allow to choose which viewType can be a sticky view.
     *
     * @param viewType The view type of the new View.
     * @return return true if the viewType is a sticky view.
     * @see .isStickyHeader
     */
    override fun isStickyHeader(viewType: Int): Boolean {
        return activity.checkBoxShowStickyHeader.isChecked && isHeader(viewType)
    }

    override fun getFirstDecoration(viewType: Int): Drawable? {
        return if (activity.checkBoxShowFirstLastDecoration.isChecked) ContextCompat.getDrawable(activity, R.drawable.smart_first_decoration) else null
    }

    override fun getLastDecoration(viewType: Int): Drawable? {
        return if (activity.checkBoxShowFirstLastDecoration.isChecked) ContextCompat.getDrawable(activity, R.drawable.smart_last_decoration) else null
    }



    /**
     * Called by SmartLinearItemDecoration when a ViewHolder is drawn. This drawable is added just at the bottom of a view of a given viewType.
     *
     * @param viewType The view type of the new View.
     * @return a divider drawable of the given type.
     */
    override fun getDividerDecoration(viewType: Int): Drawable? {
        if (activity.checkBoxShowDivider.isChecked) {
            return when (viewType) {
                VIEW_TYPE_CONTENT -> ContextCompat.getDrawable(activity, R.drawable.smart_divider_content)
                VIEW_TYPE_TITLE_H3 -> ContextCompat.getDrawable(activity, R.drawable.smart_divider_title_h3)
                VIEW_TYPE_TITLE_H2 -> ContextCompat.getDrawable(activity, R.drawable.smart_divider_title_h2)
                else -> ContextCompat.getDrawable(activity, R.drawable.smart_divider_title_h1)
            }
        }
        return null
    }

    override fun getLastDividerDecoration(viewType: Int, nextViewType: Int): Drawable? {
        if (activity.checkBoxShowDivider.isChecked) {
            when (viewType) {
                VIEW_TYPE_CONTENT -> return ContextCompat.getDrawable(activity, R.drawable.smart_divider_space)
                VIEW_TYPE_TITLE_H3 -> return ContextCompat.getDrawable(activity, R.drawable.smart_divider_title_h3)
                VIEW_TYPE_TITLE_H2 -> return ContextCompat.getDrawable(activity, R.drawable.smart_divider_title_h2)
                VIEW_TYPE_TITLE_H1 -> return ContextCompat.getDrawable(activity, R.drawable.smart_divider_title_h1)
            }
        }
        return null
    }


    override fun getFirstDividerDecoration(viewType: Int, previousViewType: Int): Drawable? {
        if (activity.checkBoxShowDivider.isChecked) {
            if (viewType == VIEW_TYPE_CONTENT) {
                return ContextCompat.getDrawable(activity, R.drawable.smart_divider_space)
            }
        }
        return null
    }

    override fun onCreateAndBindStickyView(parent: RecyclerView, position: Int): View {
        if (activity.checkboxShowCustomStickyHeader.isChecked) {
            val stickyView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_title, parent, false)

            initTitleViewHolder(stickyView, getItemViewType(position))
            val textViewTitle : TextView = stickyView.findViewById(R.id.textViewTitle)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = (objects[position] as TitleH).timeInMillis
            when (getItemViewType(position)) {
                VIEW_TYPE_TITLE_H3 -> textViewTitle.text = Util.simpleDateFormatDayCustom.format(calendar.time)
                VIEW_TYPE_TITLE_H2 -> textViewTitle.text = Util.simpleDateFormatMonthCustom.format(calendar.time)
                VIEW_TYPE_TITLE_H1 -> textViewTitle.text = activity.getString(R.string.happy_new_year_x, Util.simpleDateFormatYear.format(calendar.time))
                else -> textViewTitle.text = Util.simpleDateFormatYear.format(calendar.time)
            }
            return stickyView
        } else {
            return super.onCreateAndBindStickyView(parent, position)
        }
    }

    internal inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    internal inner class TitleViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {
        init {
            initTitleViewHolder(itemView, viewType)
        }
    }

    private fun initTitleViewHolder(itemView: View, viewType: Int) {
        val rootView = itemView as LinearLayout
        val paddingStartEnd = rootView.paddingLeft
        var marginTopBottom = rootView.paddingTop
        when (viewType) {
            VIEW_TYPE_TITLE_H3 -> itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.titleh3))
            VIEW_TYPE_TITLE_H2 -> {
                marginTopBottom = Util.convertDpToPixel(itemView.getContext(), 24F)
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.titleh2))
            }
            VIEW_TYPE_TITLE_H1 -> {
                marginTopBottom = Util.convertDpToPixel(itemView.getContext(), 36F)
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.titleh1))
            }
        }
        rootView.setPadding(paddingStartEnd, marginTopBottom, paddingStartEnd, marginTopBottom)
    }

    override fun getItems(): ArrayList<Any>? {
        return objects
    }

    override fun isDraggable(viewType: Int): Boolean {
        return activity.checkboxDraggableUnderDay.isChecked && viewType == VIEW_TYPE_CONTENT
    }

    override fun allowMove(viewTypeDraggable: Int, headerViewType: Int): Boolean {
        return viewTypeDraggable == VIEW_TYPE_CONTENT && headerViewType == VIEW_TYPE_TITLE_H3
    }

    override fun isHeader(viewType: Int): Boolean {
        return viewType != VIEW_TYPE_CONTENT
    }

}



