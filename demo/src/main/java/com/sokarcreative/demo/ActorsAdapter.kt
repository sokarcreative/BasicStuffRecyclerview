package com.sokarcreative.demo

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sokarcreative.basicstuffrecyclerview.divider.LinearDividersListener
import com.sokarcreative.demo.databinding.ViewholderActorsActorBinding
import com.sokarcreative.demo.models.Actor

class ActorsAdapter(context: Context, var dividersEnabled: MainViewModel.DividersEnabled): RecyclerView.Adapter<ActorsAdapter.ActorViewHolder>(), LinearDividersListener {
    var actors = emptyList<Actor>()

    val drawableDividerFirstLast: Drawable = GradientDrawable().apply {
        setSize(context.resources.getDimension(R.dimen.common_horizontal_space).toInt(), 0)
    }
    val drawableDividerBetweenActors: Drawable = GradientDrawable().apply {
        setSize(context.convertDpToPixel(10f), 0)
    }
    override fun getItemCount(): Int = actors.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder = ActorViewHolder(ViewholderActorsActorBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        holder.bind(actors[position])
    }

    override fun getDividerDecoration(viewType: Int): Drawable? = if(!dividersEnabled.isDividerDecorationEnabled) null else drawableDividerBetweenActors
    override fun getFirstDecoration(viewType: Int): Drawable? = if(!dividersEnabled.isFirstLastDecorationEnabled) null else drawableDividerFirstLast
    override fun getLastDecoration(viewType: Int): Drawable? = if(!dividersEnabled.isFirstLastDecorationEnabled) null else drawableDividerFirstLast

    fun refresh(actors: Set<Actor>) {
        val oldItems = this.actors
        val newItems = actors.toList()

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

        this.actors = newItems
        diff.dispatchUpdatesTo(this)
    }

    class ActorViewHolder(private val binding: ViewholderActorsActorBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(actor: Actor){
            binding.textViewName.text = actor.name
        }
    }
}