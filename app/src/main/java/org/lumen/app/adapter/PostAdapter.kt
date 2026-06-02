package org.lumen.app.adapter

import android.sax.Element
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.lumen.app.R
import org.lumen.app.data.model.post.ClickElement
import org.lumen.app.data.model.post.Post
import org.lumen.app.databinding.PostItemBinding
import org.lumen.app.util.formatDate

class PostAdapter(
    var postList: MutableList<Post>,
    private val onLike : (Post, position: Int, element: ClickElement) -> Unit,
) : RecyclerView.Adapter<PostAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MyViewHolder {
        val view = PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val post = postList[position]
        holder.binding.content.text = post.content
        holder.binding.date.text = formatDate(post.createdAt.toString())
        holder.binding.username.text = post.user.username
        holder.binding.subUsername.text = "@${post.user.username}"
        holder.binding.valueLike.text = post.likesCount.toString()
        holder.binding.valueComments.text = post.commentsCount.toString()

        if (post.isLiked == true) {
            holder.binding.iconLike.setImageResource(R.drawable.ic_heart_filled)

        } else {
            holder.binding.iconLike.setImageResource(R.drawable.ic_heart)
        }

        holder.binding.iconLike.setOnClickListener {
            onLike(post,position, ClickElement.LIKE)
        }
        holder.binding.content.setOnClickListener { onLike(post, position, ClickElement.CONTENT)  }
    }

    fun addPosts(newPosts: List<Post>) {
        val startPosition = postList.size
        postList.addAll(newPosts)

        notifyItemRangeInserted(startPosition, newPosts.size)
    }

    override fun getItemCount(): Int {
        return postList.size

    }

    inner class MyViewHolder(val binding: PostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {}


}