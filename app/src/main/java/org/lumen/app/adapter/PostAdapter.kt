package org.lumen.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.lumen.app.R
import org.lumen.app.adapter.PostAdapter.MyViewHolder
import org.lumen.app.data.model.post.Post
import org.lumen.app.databinding.PostItemBinding
import org.lumen.app.util.formatDate
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter ( var postList: List<Post>) : RecyclerView.Adapter<PostAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val post = postList[position]
        holder.binding.content.text = post.content
        holder.binding.date.text = formatDate(post.createdAt)
        holder.binding.username.text = post.user.username
        holder.binding.subUsername.text = "@${post.user.username}"
        holder.binding.valueLike.text = post.likesCount.toString()
        holder.binding.valueComments.text = post.commentsCount.toString()

        if (post.isLiked) {
            holder.binding.iconLike.setImageResource(R.drawable.ic_heart_filled)

        } else {
            holder.binding.iconLike.setImageResource(R.drawable.ic_heart)
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    inner class MyViewHolder ( val binding : PostItemBinding) : RecyclerView.ViewHolder(binding.root){}


}