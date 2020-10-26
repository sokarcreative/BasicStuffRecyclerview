package com.sokarcreative.basicstuffrecyclerview

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sokarcreative.basicstuffrecyclerview.models.*
import com.sokarcreative.basicstuffrecyclerview.divider.LinearDividersListener
import com.sokarcreative.basicstuffrecyclerview.stickyheader.LinearStickyHeadersListener

class DemoAdapter(context: Context, val addOrRemove: (movie: Movie) -> Unit, val addOrRemoveAllMovies: (headerCategory: MainViewModel.HeaderCategory) -> Unit, val scrollToPosition: (position: Int) -> Unit, var stickyHeadersEnabled: Triple<Boolean, Boolean, Boolean>, var dividersEnabled: MainViewModel.DividersEnabled) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), LinearDividersListener, LinearStickyHeadersListener {

    var items: List<Any> = emptyList()

    override fun getItemCount(): Int = items.count()

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is Header -> VIEW_TYPE_HEADER
        is MainViewModel.HeaderCategory -> VIEW_TYPE_CATEGORY
        is MainViewModel.MovieState -> VIEW_TYPE_MOVIE
        else -> throw IllegalStateException()
    }

    override fun isStickyHeader(viewType: Int): Boolean = when (viewType) {
        VIEW_TYPE_HEADER -> stickyHeadersEnabled.first
        VIEW_TYPE_CATEGORY -> stickyHeadersEnabled.second
        VIEW_TYPE_MOVIE -> stickyHeadersEnabled.third
        else -> throw IllegalStateException()
    }

    val drawableDividerFirstLast: Drawable = ContextCompat.getDrawable(context, R.drawable.divider_first_last)!!
    val drawableMovieDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.divider_between_movies)!!

    val drawableDividerBetweenMovieAndCategory: Drawable = ContextCompat.getDrawable(context, R.drawable.divider_between_movie_and_category)!!
    val drawableDividerBetweenMovieAndheader: Drawable = ContextCompat.getDrawable(context, R.drawable.divider_between_movie_and_header)!!

    override fun getFirstDecoration(viewType: Int): Drawable? = if(!dividersEnabled.isFirstLastDecorationEnabled) null else drawableDividerFirstLast

    override fun getFirstDividerDecoration(viewType: Int, previousViewType: Int): Drawable? = if(!dividersEnabled.isFirstDividerDecorationEnabled) null else {
        when {
            previousViewType == VIEW_TYPE_MOVIE && viewType == VIEW_TYPE_CATEGORY -> drawableDividerBetweenMovieAndCategory
            previousViewType == VIEW_TYPE_MOVIE && viewType == VIEW_TYPE_HEADER -> drawableDividerBetweenMovieAndheader
            else -> null
        }
    }

    override fun getDividerDecoration(viewType: Int): Drawable? = if(!dividersEnabled.isDividerDecorationEnabled) null else {
        when (viewType) {
            VIEW_TYPE_MOVIE -> drawableMovieDivider
            else -> null
        }
    }

    override fun getLastDividerDecoration(viewType: Int, nextViewType: Int): Drawable? = if(!dividersEnabled.isLastDividerDecorationEnabled) null else drawableDividerFirstLast

    override fun getLastDecoration(viewType: Int): Drawable? = if(!dividersEnabled.isFirstLastDecorationEnabled) null else drawableDividerFirstLast

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_demo_header, parent, false))
            VIEW_TYPE_CATEGORY -> CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_demo_category, parent, false))
            VIEW_TYPE_MOVIE -> MovieViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_demo_movie, parent, false))
            else -> throw IllegalStateException()
        }
    }

    override fun onCreateAndBindStickyView(parent: RecyclerView, position: Int): View = when (getItemViewType(position)) {
        VIEW_TYPE_CATEGORY -> super.onCreateAndBindStickyView(parent, position).apply {
            findViewById<ImageView>(R.id.imageViewFavorites).visibility = View.INVISIBLE
        }
        else -> super.onCreateAndBindStickyView(parent, position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(items[position] as Header)
            is CategoryViewHolder -> holder.bind(items[position] as MainViewModel.HeaderCategory, position)
            is MovieViewHolder -> holder.bind(items[position] as MainViewModel.MovieState)
            else -> throw IllegalStateException()
        }
    }

    fun refresh(items: List<Any>) {
        val oldItems = this.items
        val newItems = items

        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldItems.size

            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }
        })

        this.items = newItems
        diff.dispatchUpdatesTo(this)
    }

    class HeaderViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textView = itemView.findViewById<TextView>(R.id.textViewTitle)
        fun bind(header: Header) {
            itemView.setOnClickListener { Toast.makeText(itemView.context, "${++iter}", Toast.LENGTH_SHORT).show() }
            textView.setCompoundDrawablesWithIntrinsicBounds(header.getDrawableRes(), 0, 0, 0)
            textView.compoundDrawables[0].mutate().colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(itemView.context, header.getColorRes()), BlendModeCompat.SRC_ATOP)
            textView.text = itemView.context.getString(header.getNameStringRes())
        }

        companion object {
            var iter: Int = 0
        }
    }

    inner class CategoryViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val constraintLayout = itemView.findViewById<ConstraintLayout>(R.id.constraintLayout)
        val textViewCategory = itemView.findViewById<TextView>(R.id.textViewCategory)
        val imageViewFavorites = itemView.findViewById<ImageView>(R.id.imageViewFavorites)

        fun bind(headerCategory: MainViewModel.HeaderCategory, position: Int) {
            with(itemView) {
                setOnClickListener {
                    scrollToPosition.invoke(position)
                }
                with(constraintLayout) {
                    (background.mutate() as GradientDrawable).setStroke(1, ContextCompat.getColor(itemView.context, headerCategory.header.getColorRes()))

                    with(textViewCategory) {
                        text = itemView.context.getString(headerCategory.category.titleStringRes())
                    }

                    with(imageViewFavorites) {
                        when(headerCategory.header){
                            Header.MOVIES -> setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.favorite_border))
                            Header.FAVORITES -> setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.remove_circle_outline))
                        }
                        setOnClickListener {
                            addOrRemoveAllMovies.invoke(headerCategory)
                        }
                    }
                }
            }
        }
    }

    inner class MovieViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val constraintLayout = itemView.findViewById<ConstraintLayout>(R.id.constraintLayout)
        val textViewTitle = itemView.findViewById<TextView>(R.id.textViewTitle)
        val imageViewFavorite = itemView.findViewById<ImageView>(R.id.imageViewFavorite)

        fun bind(movieState: MainViewModel.MovieState) {
            with(constraintLayout) {
                with(textViewTitle) {
                    text = movieState.movie().name
                }

                with(imageViewFavorite) {
                    when(movieState){
                        is MainViewModel.MovieState.Default -> setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.favorite_border))
                        is MainViewModel.MovieState.Favorite -> setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.remove_circle_outline))
                    }
                    setOnClickListener {
                        addOrRemove.invoke(movieState.movie())
                    }
                }
            }
        }
    }

    companion object {
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_CATEGORY = 2
        const val VIEW_TYPE_MOVIE = 3
    }

}

