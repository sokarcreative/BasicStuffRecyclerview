package com.sokarcreative.library

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*


/**
 * Created by sokarcreative on 04/11/2017.
 */

class BasicStuffItemTouchHelperCallback : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {

        if(recyclerView.adapter == null || !(recyclerView.adapter is BasicStuffAdapter)){
            return ItemTouchHelper.Callback.makeMovementFlags(0, 0)
        }

        val adapter : BasicStuffAdapter = recyclerView.adapter as BasicStuffAdapter
        val dragFlags = if (adapter.isDraggable(viewHolder.itemViewType)){
            if(recyclerView.layoutManager is LinearLayoutManager){
                if (recyclerView.layoutManager is GridLayoutManager){
                    UP or DOWN or START or END
                }
                else if((recyclerView.layoutManager as LinearLayoutManager).orientation == LinearLayoutManager.VERTICAL){
                    UP or DOWN
                }
                else if((recyclerView.layoutManager as LinearLayoutManager).orientation == LinearLayoutManager.HORIZONTAL){
                    START or END
                }
                else 0
            }
            else 0
        } else 0

        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

        if(recyclerView.adapter == null || !(recyclerView.adapter is BasicStuffAdapter)){
            return false
        }

        val adapter : BasicStuffAdapter = recyclerView.adapter as BasicStuffAdapter

        if(adapter.getItems() == null){
            return false
        }
        if(canMoveToTarget(adapter, source, target)){
            val min : Int
            val max : Int
            if(source.adapterPosition < target.adapterPosition){
                min = source.adapterPosition
                max= target.adapterPosition
            }
            else{
                min = target.adapterPosition
                max= source.adapterPosition
            }
            if(adapter.getItemViewType(min) != adapter.getItemViewType(max)){
                if(min > 0){
                    adapter.notifyItemChanged(min-1)
                }
                if(max < adapter.getItems()!!.size -1){
                    adapter.notifyItemChanged(max+1)
                }
            }
            if(max-min == 1){
                Collections.swap(adapter.getItems(), min, max)
                adapter.notifyItemMoved(source.adapterPosition, target.adapterPosition)
            }
            else{
                val tmp = adapter.getItems()!![source.adapterPosition]
                adapter.getItems()!!.removeAt(source.adapterPosition)
                adapter.getItems()!!.add(target.adapterPosition, tmp)
                adapter.notifyItemMoved(source.adapterPosition, target.adapterPosition)
            }
            return true
        }
        return false
    }

    private fun canMoveToTarget(adapter: BasicStuffAdapter, viewHolderSource : RecyclerView.ViewHolder, viewHolderTarget : RecyclerView.ViewHolder) : Boolean{
        if(viewHolderTarget.adapterPosition == 0 && !adapter.isDraggable(adapter.getItemViewType(viewHolderTarget.adapterPosition))){
            return false
        }
        var i = if(viewHolderTarget.adapterPosition > 0 && viewHolderTarget.adapterPosition < viewHolderSource.adapterPosition) viewHolderTarget.adapterPosition-1 else viewHolderTarget.adapterPosition
        while (i >= 0){
            if(adapter.isHeader(adapter.getItemViewType(i))){
                val targetHeaderViewType = adapter.getItemViewType(i)
                if(adapter.allowMove(viewHolderSource.itemViewType, adapter.getItemViewType(i))){
                    if(adapter.allowOnlyForSameHeaderAsSource(viewHolderSource.itemViewType, targetHeaderViewType)){
                        return isSourceHeaderSameAsTargetHeader(adapter, viewHolderSource.adapterPosition, i, targetHeaderViewType)
                    }
                    else{
                        return true;
                    }
                }
                else{
                    return false;
                }
            }
            i--
        }
        return false
    }

    private fun isSourceHeaderSameAsTargetHeader(adapter: BasicStuffAdapter, sourcePosition : Int, headerTargetPosition : Int, targetHeaderViewType : Int) : Boolean{
        var i = sourcePosition
        while(i >= 0 && i >= headerTargetPosition){
            if(adapter.getItemViewType(i) == targetHeaderViewType){
                return i == headerTargetPosition
            }
            i--
        }
        return false;
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }


}
