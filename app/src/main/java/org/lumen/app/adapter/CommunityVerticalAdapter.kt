package org.lumen.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import org.lumen.app.data.model.Community
import org.lumen.app.databinding.CommunityVerticalItemBinding

class CommunityVerticalAdapter (
    var communities: MutableList<Community>,
    private val onItemClick: (Community) -> Unit
): RecyclerView.Adapter<CommunityVerticalAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = CommunityVerticalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val community = communities[position]
        holder.binding.name.text = community.name

      holder.binding.btnAccess.setOnClickListener {
          onItemClick(community)
      }

    }

    override fun getItemCount(): Int {
        return communities.size
    }

    fun addCommunities (newCommunities: List<Community>){
        val startPosition = communities.size
        communities.addAll(newCommunities)
        notifyItemRangeInserted(startPosition, newCommunities.size)

    }


    inner class MyViewHolder (val binding: CommunityVerticalItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }


}