package org.lumen.app.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.lumen.app.R
import org.lumen.app.data.model.Reward
import org.lumen.app.databinding.RewardItemBinding

class ConquistaAdapter(
    var conquistaList: MutableList<Reward>,
) : RecyclerView.Adapter<ConquistaAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        val view = RewardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int,
    ) {
        val conquista = conquistaList[position]

        val context = holder.itemView.context

        holder.binding.tvTitulo.text = conquista.title
        holder.binding.tvDificuldade.text = conquista.difficulty

        holder.binding.cardContainer.setCardBackgroundColor(
            ContextCompat.getColor(context, R.color.card_purple),
        )
        holder.binding.tvTitulo.setTextColor(
            ContextCompat.getColor(context, R.color.white),
        )
        holder.binding.tvDificuldade.setTextColor(
            ContextCompat.getColor(context, R.color.white),
        )

        holder.binding.icStar.setImageResource(R.drawable.ic_star_cheia)
        holder.binding.icStar.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.star_yellow))
    }

    fun addConquistas(newConquistas: List<Reward>) {
        val startPosition = conquistaList.size
        conquistaList.addAll(newConquistas)

        notifyItemRangeInserted(startPosition, newConquistas.size)
    }

    override fun getItemCount(): Int = conquistaList.size

    class MyViewHolder(
        val binding: RewardItemBinding,
    ) : RecyclerView.ViewHolder(binding.root)
}
