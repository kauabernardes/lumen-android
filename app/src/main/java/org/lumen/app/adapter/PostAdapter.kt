package org.lumen.app.adapter

import android.content.res.ColorStateList
import android.content.res.ColorStateList.valueOf
import android.sax.Element
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.lumen.app.R
import org.lumen.app.R.color.card_checkin_text
import org.lumen.app.data.model.post.ClickElement
import org.lumen.app.data.model.post.Post
import org.lumen.app.data.remote.Constants.BASE_URL
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
        Glide.with(holder.itemView.context)
            .load("${BASE_URL}uploads/${post.user.profileImage}")
            .placeholder(R.drawable.ic_user_circle)
            .centerCrop()
            .into(holder.binding.avatar)



        if (post.isLiked == true) {
            holder.binding.iconLike.setImageResource(R.drawable.ic_heart_filled)
            holder.binding.iconLike.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.card_checkin_text))


        } else {
            holder.binding.iconLike.setImageResource(R.drawable.ic_heart)
            holder.binding.iconLike.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.text_secondary))

        }

        holder.binding.iconLike.setOnClickListener {
            onLike(post,position, ClickElement.LIKE)
        }
        holder.binding.content.setOnClickListener { onLike(post, position, ClickElement.CONTENT)  }
        holder.binding.valueComments.setOnClickListener { onLike(post, position, ClickElement.COMMENT)  }
        holder.binding.iconComments.setOnClickListener { onLike(post, position, ClickElement.COMMENT)  }


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