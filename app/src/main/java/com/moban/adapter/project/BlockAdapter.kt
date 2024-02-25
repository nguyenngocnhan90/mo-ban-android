package com.moban.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import com.moban.R
import com.moban.adapter.AbsAdapter
import com.moban.handler.BlockItemHandler
import com.moban.model.data.project.Block
import com.moban.view.viewholder.BlockItemViewHolder

/**
 * Created by LenVo on 3/17/18.
 */
class BlockAdapter : AbsAdapter<BlockItemViewHolder>() {

    var listener: BlockItemHandler? = null

    var blocks: MutableList<Block> = ArrayList()

    var currentIdx: Int = 0

    override fun getLayoutResourceId(viewType: Int): Int {
        return R.layout.item_block
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutResourceId(viewType), parent, false)
        return BlockItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return blocks.count()
    }

    override fun updateView(holder: BlockItemViewHolder, position: Int) {
        val block = blocks[position]
        holder.bind(block, (position == currentIdx))

        holder.itemView.setOnClickListener { _ ->
            listener?.onClicked(block, position)
            currentIdx = position
            this.notifyDataSetChanged()
        }
    }

    fun updateBlocks(blocks: List<Block>) {
        this.blocks.clear()
        this.blocks = blocks.toMutableList()
        this.notifyDataSetChanged()
    }
}
