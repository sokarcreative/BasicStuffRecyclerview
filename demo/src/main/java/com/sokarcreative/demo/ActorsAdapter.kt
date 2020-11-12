package com.sokarcreative.demo

import android.content.Context
import android.graphics.BlendMode
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sokarcreative.basicstuffrecyclerview.Decoration
import com.sokarcreative.basicstuffrecyclerview.divider.LinearDividersListener
import com.sokarcreative.demo.databinding.ViewholderActorsActorBinding
import com.sokarcreative.demo.models.Actor

class ActorsAdapter(context: Context, var dividersEnabled: MainViewModel.DividersEnabled): RecyclerView.Adapter<ActorsAdapter.ActorViewHolder>() {
    var actors = emptyList<Actor>()

    override fun getItemCount(): Int = actors.size

    override fun getItemViewType(position: Int): Int {
        return when((actors[position] as Actor).isMale){
            true -> VIEW_TYPE_ACTOR_MALE
            false -> VIEW_TYPE_ACTOR_FEMALE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder = ActorViewHolder(ViewholderActorsActorBinding.inflate(LayoutInflater.from(parent.context), parent, false), viewType == VIEW_TYPE_ACTOR_MALE)

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        holder.bind(actors[position])
    }

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

    class ActorViewHolder(private val binding: ViewholderActorsActorBinding, isMale: Boolean): RecyclerView.ViewHolder(binding.root){

        init {
            (binding.root.background as GradientDrawable).colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(itemView.context, if(isMale) R.color.colorHeaderActorsMale else R.color.colorHeaderActorsFemale), BlendModeCompat.SRC_ATOP)
        }

        fun bind(actor: Actor){
            binding.textViewName.text = actor.name
        }
    }

    companion object {
        const val VIEW_TYPE_ACTOR_MALE = 1
        const val VIEW_TYPE_ACTOR_FEMALE = 2
    }
}