package com.sokarcreative.demo

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.text.TextUtils
import android.util.LayoutDirection
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sokarcreative.basicstuffrecyclerview.Decoration
import com.sokarcreative.demo.models.*
import com.sokarcreative.basicstuffrecyclerview.divider.LinearDividersListener
import com.sokarcreative.basicstuffrecyclerview.divider.LinearDividersListenerBuilder
import com.sokarcreative.basicstuffrecyclerview.divider.LinearItemDecoration
import com.sokarcreative.basicstuffrecyclerview.stickyheader.LinearStickyHeadersListener
import com.sokarcreative.demo.databinding.*
import java.util.*

class DemoAdapter(context: Context, val addMovie: (movie: Movie) -> Unit, val removeMovie: (movie: Movie) -> Unit, val addOrRemoveAllMovies: (headerCategory: MainViewModel.HeaderCategory) -> Unit, val scrollToPosition: (position: Int) -> Unit, var stickyHeadersEnabled: Triple<Boolean, Boolean, Boolean>, var dividersEnabled: MainViewModel.DividersEnabled, val onActorsOrientationChanged: (isActorsOrientationHorizontal: Boolean) -> Unit, val isActorsOrientationHorizontal: () -> Boolean, val isDividerFeatureEnabled: () -> Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), LinearDividersListener, LinearStickyHeadersListener {

    var items: List<Any> = emptyList()

    override fun getItemCount(): Int = items.count()

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is Header -> VIEW_TYPE_HEADER
        is MainViewModel.Actors -> VIEW_TYPE_ACTORS
        is Actor -> VIEW_TYPE_ACTOR
        is MainViewModel.HeaderCategory -> VIEW_TYPE_CATEGORY
        is MainViewModel.MovieState -> VIEW_TYPE_MOVIE
        else -> throw IllegalStateException()
    }

    override fun isStickyHeader(viewType: Int): Boolean = when (viewType) {
        VIEW_TYPE_HEADER -> stickyHeadersEnabled.first
        VIEW_TYPE_CATEGORY -> stickyHeadersEnabled.second
        VIEW_TYPE_MOVIE -> stickyHeadersEnabled.third
        else -> false
    }

    val decorationFirstLast: Decoration = Decoration.Space(context.convertDpToPixel(if(BuildConfig.isModeLibraryDebug) 60f else 12f))
    val decorationCommon: Decoration = Decoration.Space(context.convertDpToPixel(12f))
    val decorationBetwenMovies: Decoration = Decoration.Space(context.convertDpToPixel(8f))
    val decorationGridBorderMovies: Decoration = Decoration.Space(context.resources.getDimension(R.dimen.common_horizontal_space).toInt())

    private inline val Locale.layoutDirection: Int
        @RequiresApi(17)
        get() = TextUtils.getLayoutDirectionFromLocale(this)

    val decorationBetweenActors: Decoration = Decoration.Drawable(LayerDrawable(
            arrayOf<Drawable>(
                    GradientDrawable().apply {
                        setSize(0, 1)
                        setColor(ContextCompat.getColor(context, R.color.background_app))
                    },
                    GradientDrawable().apply {
                        setSize(0, 1)
                        setColor(ContextCompat.getColor(context, R.color.colorDividerGrey))
                    }
            )
    ).apply {
        if (Locale.getDefault().layoutDirection == LayoutDirection.LTR) {
            setLayerInset(1, context.convertDpToPixel(24f), 0, 0, 0)
        } else {
            setLayerInset(1, 0, 0, context.convertDpToPixel(24f), 0)
        }
    })


    val firstDecorationBetweenHeaderAndActors: Decoration = Decoration.Space(context.convertDpToPixel(24f))
    val firstDecorationBetweenCategoryAndMovie: Decoration = Decoration.Space(context.convertDpToPixel(12f))
    val lastDecorationBetweenMovieAndCategory: Decoration = Decoration.Space(context.convertDpToPixel(48f))
    val firstDecorationBetweenMovieAndheader: Decoration = Decoration.Space(context.convertDpToPixel(12f))

    val lastDecorationActor: Decoration = Decoration.Drawable(ContextCompat.getDrawable(context, R.drawable.divider_last_actor)!!)

    override fun getFirstDecoration(viewType: Int): Decoration? = if (!dividersEnabled.isFirstLastDecorationEnabled) null else decorationFirstLast

    override fun getFirstDividerDecoration(viewType: Int, previousViewType: Int): Decoration? = if (!dividersEnabled.isFirstDividerDecorationEnabled) null else {
        when {
            previousViewType == VIEW_TYPE_ACTOR -> decorationCommon
            previousViewType == VIEW_TYPE_CATEGORY && viewType == VIEW_TYPE_MOVIE -> firstDecorationBetweenCategoryAndMovie
            previousViewType == VIEW_TYPE_MOVIE && viewType == VIEW_TYPE_HEADER -> firstDecorationBetweenMovieAndheader
            viewType == VIEW_TYPE_ACTORS -> firstDecorationBetweenHeaderAndActors
            else -> null
        }
    }

    override fun getDividerDecoration(viewType: Int): Decoration? = if (!dividersEnabled.isDividerDecorationEnabled) null else {
        when (viewType) {
            VIEW_TYPE_ACTOR -> decorationBetweenActors
            VIEW_TYPE_MOVIE -> decorationBetwenMovies
            else -> null
        }
    }

    override fun getGridSideBorderDecoration(viewType: Int): Decoration?  = if(viewType == VIEW_TYPE_MOVIE) decorationGridBorderMovies else null

    override fun getLastDividerDecoration(viewType: Int, nextViewType: Int): Decoration? = if (!dividersEnabled.isLastDividerDecorationEnabled) null else when {
        viewType == VIEW_TYPE_ACTOR -> lastDecorationActor
        viewType == VIEW_TYPE_HEADER && nextViewType == VIEW_TYPE_CATEGORY -> decorationCommon
        viewType == VIEW_TYPE_MOVIE && nextViewType == VIEW_TYPE_CATEGORY -> lastDecorationBetweenMovieAndCategory
        viewType == VIEW_TYPE_ACTORS -> decorationCommon
        else -> null
    }

    override fun getLastDecoration(viewType: Int): Decoration? = if (!dividersEnabled.isFirstLastDecorationEnabled) null else decorationFirstLast

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(ViewholderDemoHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_ACTORS -> ActorsViewModel(ViewholderDemoActorsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_ACTOR -> ActorViewModel(ViewholderDemoActorBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_CATEGORY -> CategoryViewHolder(ViewholderDemoCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_MOVIE -> MovieViewHolder(ViewholderDemoMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalStateException()
        }
    }

    override fun onCreateAndBindStickyView(parent: RecyclerView, position: Int): View = when (getItemViewType(position)) {
        VIEW_TYPE_CATEGORY -> super.onCreateAndBindStickyView(parent, position).apply {
            findViewById<ImageView>(R.id.imageViewFavorites).visibility = View.INVISIBLE
        }
        VIEW_TYPE_HEADER -> super.onCreateAndBindStickyView(parent, position).apply {
            findViewById<SwitchCompat>(R.id.switchOrientation).visibility = View.INVISIBLE
        }
        else -> super.onCreateAndBindStickyView(parent, position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(items[position] as Header)
            is ActorsViewModel -> holder.bind(items[position] as MainViewModel.Actors)
            is ActorViewModel -> holder.bind(items[position] as Actor)
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

    inner class HeaderViewHolder(private val binding: ViewholderDemoHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(header: Header) {
            when (header) {
                Header.ACTORS -> {
                    binding.switchOrientation.setOnCheckedChangeListener(null)
                    binding.switchOrientation.isChecked = isActorsOrientationHorizontal()
                    binding.switchOrientation.setOnCheckedChangeListener { buttonView, isChecked ->
                        onActorsOrientationChanged.invoke(isChecked)
                    }
                    binding.switchOrientation.visibility = View.VISIBLE
                }
                else -> {
                    binding.switchOrientation.visibility = View.INVISIBLE
                }
            }

            with(binding.textViewTitle) {
                setCompoundDrawablesRelativeWithIntrinsicBounds(header.getDrawableRes(), 0, 0, 0)
                compoundDrawablesRelative[0].mutate().colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(itemView.context, header.getColorRes()), BlendModeCompat.SRC_ATOP)
                text = itemView.context.getString(header.getNameStringRes())
            }
        }

    }

    inner class ActorsViewModel(private val binding: ViewholderDemoActorsBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

            binding.root.adapter = ActorsAdapter(itemView.context, dividersEnabled)
            (binding.root.layoutManager as GridLayoutManager).apply {
                spanCount = if(BuildConfig.isModeLibraryDebug) 3 else 1
                spanSizeLookup = object: GridLayoutManager.SpanSizeLookup(){
                    override fun getSpanSize(position: Int): Int {
                        return 1
                    }
                }
            }
        }

        fun bind(actors: MainViewModel.Actors) {
            binding.root.removeAllLinearItemDecorations()

            if (isDividerFeatureEnabled()) {
                binding.root.addItemDecoration(LinearDividersListenerBuilder()
                        .apply {
                            if(dividersEnabled.isFirstLastDecorationEnabled){
                                defaultFirstLastDecoration(Decoration.Space(itemView.resources.getDimension(R.dimen.common_horizontal_space).toInt()))
                            }
                            if(dividersEnabled.isDividerDecorationEnabled){
                                defaultDividerDecoration(Decoration.Space(itemView.context.convertDpToPixel(10f)))
                            }
                        }
                        .buildAndWrapped())
            }

            with(binding.root.adapter as ActorsAdapter) {
                this.dividersEnabled = this@DemoAdapter.dividersEnabled
                refresh(actors.actors)
            }

            binding.root.invalidateItemDecorations()
        }

        fun notifyDividersChanged() {
            (binding.root.adapter as ActorsAdapter).dividersEnabled = this@DemoAdapter.dividersEnabled
            binding.root.removeAllLinearItemDecorations()
            if (isDividerFeatureEnabled()) {
                binding.root.addItemDecoration(LinearDividersListenerBuilder()
                        .apply {
                            if(dividersEnabled.isFirstLastDecorationEnabled){
                                defaultFirstLastDecoration(Decoration.Space(itemView.resources.getDimension(R.dimen.common_horizontal_space).toInt()))
                            }
                            if(dividersEnabled.isDividerDecorationEnabled){
                                defaultDividerDecoration(Decoration.Space(itemView.context.convertDpToPixel(10f)))
                            }
                        }
                        .buildAndWrapped())
            }
        }
    }

    class ActorViewModel(private val binding: ViewholderDemoActorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(actor: Actor) {
            binding.root.text = actor.name
        }
    }

    inner class CategoryViewHolder(private val binding: ViewholderDemoCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerCategory: MainViewModel.HeaderCategory, position: Int) {
            with(binding.root) {
                setOnClickListener {
                    scrollToPosition.invoke(position)
                }
            }
            with(binding.constraintLayout) {
                (background.mutate() as GradientDrawable).setStroke(1, ContextCompat.getColor(itemView.context, headerCategory.header.getColorRes()))
            }
            with(binding.textViewCategory) {
                text = itemView.context.getString(headerCategory.category.titleStringRes())
            }
            with(binding.imageViewFavorites) {
                when (headerCategory.header) {
                    Header.MOVIES -> setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.favorite_border))
                    Header.FAVORITES -> setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.remove_circle_outline))
                    else -> throw java.lang.IllegalStateException("can't happen")
                }
                setOnClickListener {
                    addOrRemoveAllMovies.invoke(headerCategory)
                }
            }
        }
    }

    inner class MovieViewHolder(private val binding: ViewholderDemoMovieBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movieState: MainViewModel.MovieState) {
            with(binding.textViewTitle) {
                text = movieState.movie().name
            }
            with(binding.imageViewFavorite) {
                when (movieState) {
                    is MainViewModel.MovieState.Default -> setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.favorite_border))
                    is MainViewModel.MovieState.Favorite -> setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.remove_circle_outline))
                }
                setOnClickListener {
                    when (movieState) {
                        is MainViewModel.MovieState.Default -> addMovie.invoke(movieState.movie())
                        is MainViewModel.MovieState.Favorite -> removeMovie.invoke(movieState.movie())
                    }
                }
            }
        }
    }

    companion object {
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_ACTORS = 2
        const val VIEW_TYPE_ACTOR = 3
        const val VIEW_TYPE_CATEGORY = 4
        const val VIEW_TYPE_MOVIE = 5
    }
}

