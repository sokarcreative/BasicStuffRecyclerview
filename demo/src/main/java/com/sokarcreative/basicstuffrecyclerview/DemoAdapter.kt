package com.sokarcreative.basicstuffrecyclerview

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
class DemoAdapter(private var items: ArrayList<Any>, private val activity: MainActivity) : BasicStuffAdapter() {

    private val VIEW_TYPE_CONTENT = 0
    private val VIEW_TYPE_TITLE_H1 = 1
    private val VIEW_TYPE_TITLE_H2 = 2
    private val VIEW_TYPE_TITLE_H3 = 3

    val firstDecoration : Drawable? by lazy (LazyThreadSafetyMode.NONE) { ContextCompat.getDrawable(activity, R.drawable.first_decoration) }
    val lastDecoration : Drawable? by lazy (LazyThreadSafetyMode.NONE) { ContextCompat.getDrawable(activity, R.drawable.last_decoration) }

    val dividerContent : Drawable? by lazy (LazyThreadSafetyMode.NONE) { ContextCompat.getDrawable(activity, R.drawable.divider_content) }
    val dividerContentSpace : Drawable? by lazy (LazyThreadSafetyMode.NONE) { ContextCompat.getDrawable(activity, R.drawable.divider_space) }

    val dividerH1 : Drawable? by lazy (LazyThreadSafetyMode.NONE) { ContextCompat.getDrawable(activity, R.drawable.divider_h1) }
    val dividerH2 : Drawable? by lazy (LazyThreadSafetyMode.NONE) { ContextCompat.getDrawable(activity, R.drawable.divider_h2) }
    val dividerH3 : Drawable? by lazy (LazyThreadSafetyMode.NONE) { ContextCompat.getDrawable(activity, R.drawable.divider_h3) }

    fun refresh(items: ArrayList<Any>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return if (item is Message) {
            VIEW_TYPE_CONTENT
        } else {
            when ((item as TitleH).titleType) {
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
        val viewType = getItemViewType(position)
        if (viewType == VIEW_TYPE_CONTENT) {
            (holder as ContentViewHolder).bind(items[position] as Message)
        } else {
            (holder as TitleViewHolder).bind(items[position] as TitleH, viewType)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun isStickyHeader(viewType: Int): Boolean = activity.checkBoxShowStickyHeader.isChecked && isHeader(viewType)

    override fun getFirstDecoration(viewType: Int): Drawable? = if (activity.checkBoxShowFirstLastDecoration.isChecked) firstDecoration else null

    override fun getLastDecoration(viewType: Int): Drawable? = if (activity.checkBoxShowFirstLastDecoration.isChecked) lastDecoration else null

    override fun getDividerDecoration(viewType: Int): Drawable? {
        if (activity.checkBoxShowDivider.isChecked) {
            return when (viewType) {
                VIEW_TYPE_CONTENT -> dividerContent
                VIEW_TYPE_TITLE_H3 -> dividerH3
                VIEW_TYPE_TITLE_H2 -> dividerH2
                else -> dividerH1
            }
        }
        return null
    }

    override fun getFirstDividerDecoration(viewType: Int, previousViewType: Int): Drawable? {
        if (activity.checkBoxShowDivider.isChecked) {
            if (viewType == VIEW_TYPE_CONTENT) {
                return dividerContentSpace
            }
        }
        return null
    }

    override fun getLastDividerDecoration(viewType: Int, nextViewType: Int): Drawable? {
        if (activity.checkBoxShowDivider.isChecked) {
            when (viewType) {
                VIEW_TYPE_CONTENT -> return dividerContentSpace
                VIEW_TYPE_TITLE_H3 -> return dividerH3
                VIEW_TYPE_TITLE_H2 -> return dividerH2
                else-> return dividerH1
            }
        }
        return null
    }


    override fun onCreateAndBindStickyView(parent: RecyclerView, position: Int): View {
        if (activity.checkboxShowCustomStickyHeader.isChecked) {
            val stickyView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_title, parent, false)
            val textViewTitle : TextView = stickyView.findViewById(R.id.textViewTitle)

            val viewType = getItemViewType(position)
            initTitleViewHolder(stickyView, viewType)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = (items[position] as TitleH).timeInMillis

            when (viewType) {
                VIEW_TYPE_TITLE_H3 -> textViewTitle.text = activity.simpleDateFormatDayCustom.format(calendar.time)
                VIEW_TYPE_TITLE_H2 -> textViewTitle.text = activity.simpleDateFormatMonthCustom.format(calendar.time)
                VIEW_TYPE_TITLE_H1 -> textViewTitle.text = activity.getString(R.string.happy_new_year_x, activity.simpleDateFormatYear.format(calendar.time))
                else -> textViewTitle.text = activity.simpleDateFormatYear.format(calendar.time)
            }
            return stickyView
        } else {
            return super.onCreateAndBindStickyView(parent, position)
        }
    }

    override fun getItems(): ArrayList<Any>? = items

    override fun isDraggable(viewType: Int): Boolean = activity.checkboxDraggableUnderDay.isChecked && viewType == VIEW_TYPE_CONTENT

    override fun allowMove(viewTypeDraggable: Int, headerViewType: Int): Boolean = viewTypeDraggable == VIEW_TYPE_CONTENT && headerViewType == VIEW_TYPE_TITLE_H3

    override fun isHeader(viewType: Int): Boolean = viewType != VIEW_TYPE_CONTENT

    internal inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(message : Message){
            itemView.textViewAuthor.text = activity.getString(R.string.author_x, message.author)
            itemView.textViewMessage.text = activity.getString(R.string.message_x, message.message)
        }

    }

    internal inner class TitleViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {

        init {
            initTitleViewHolder(itemView, viewType)
        }

        fun bind(titleH: TitleH, viewType: Int){
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = titleH.timeInMillis
            when (viewType) {
                VIEW_TYPE_TITLE_H3 -> itemView.textViewTitle.text = activity.simpleDateFormatDay.format(calendar.time)
                VIEW_TYPE_TITLE_H2 -> itemView.textViewTitle.text = activity.simpleDateFormatMonth.format(calendar.time)
                else -> itemView.textViewTitle.text = activity.simpleDateFormatYear.format(calendar.time)
            }
        }

    }

    val paddingTopBottomH1 : Int by lazy (LazyThreadSafetyMode.NONE) { activity.convertDpToPixel(36F) }
    val paddingTopBottomH2 : Int by lazy (LazyThreadSafetyMode.NONE) { activity.convertDpToPixel(24F) }

    val backgroundColorTitleH1 : Int by lazy (LazyThreadSafetyMode.NONE) { ContextCompat.getColor(activity, R.color.backgroundColorH1) }
    val backgroundColorTitleH2 : Int by lazy (LazyThreadSafetyMode.NONE) { ContextCompat.getColor(activity, R.color.backgroundColorH2) }

    private fun initTitleViewHolder(itemView: View, viewType: Int) {
        val rootView = itemView as LinearLayout
        val paddingStartEnd = rootView.paddingLeft
        var paddingTopBottom = rootView.paddingTop
        when (viewType) {
            VIEW_TYPE_TITLE_H2 -> {
                paddingTopBottom = paddingTopBottomH2
                itemView.setBackgroundColor(backgroundColorTitleH2)
            }
            VIEW_TYPE_TITLE_H1 -> {
                paddingTopBottom = paddingTopBottomH1
                itemView.setBackgroundColor(backgroundColorTitleH1)
            }
        }
        rootView.setPadding(paddingStartEnd, paddingTopBottom, paddingStartEnd, paddingTopBottom)
    }

    companion object {
        private fun Context.convertDpToPixel(dp: Float): Int {
            return (dp * (resources.displayMetrics.densityDpi / 160f)).toInt()
        }
    }

}



