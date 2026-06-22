
package org.lumen.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.lumen.app.R
import org.lumen.app.data.model.SessionMessage
import org.lumen.app.data.remote.Constants.BASE_URL
import org.lumen.app.databinding.MessageAiItemBinding
import org.lumen.app.databinding.MessageOtherUserItemBinding
import org.lumen.app.databinding.MessageUserItemBinding
import org.lumen.app.util.formatDate

class MessageAdapter(
    private val currentUserId: String,
    var messageList: MutableList<SessionMessage>,
    private val onMessageClick: (SessionMessage, position: Int) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_USER_LOGGED = 1
    private val VIEW_TYPE_AI = 2
    private val VIEW_TYPE_OTHER_USER = 3

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]

        return when {
            message.isAi -> VIEW_TYPE_AI
            message.userId == currentUserId -> VIEW_TYPE_USER_LOGGED
            else -> VIEW_TYPE_OTHER_USER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_AI -> AiViewHolder(MessageAiItemBinding.inflate(inflater, parent, false))
            VIEW_TYPE_USER_LOGGED -> UserLoggedViewHolder(MessageUserItemBinding.inflate(inflater, parent, false))
            else -> OtherUserViewHolder(MessageOtherUserItemBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]

        when (holder) {
            is AiViewHolder -> {
                holder.binding.textViewMessage.text = message.text
                holder.binding.textViewTime.text = message.timestamp?.let { formatDate(it) } ?: ""

                Glide.with(holder.itemView.context)
                    .load(R.drawable.ic_luminha)
                    .circleCrop()
                    .into(holder.binding.avatarAi)

                holder.binding.root.setOnClickListener { onMessageClick(message, position) }
            }

            is UserLoggedViewHolder -> {
                holder.binding.textViewMessage.text = message.text
                holder.binding.textViewTime.text = message.timestamp?.let { formatDate(it) } ?: ""

                holder.binding.root.setOnClickListener { onMessageClick(message, position) }
            }

            is OtherUserViewHolder -> {
                holder.binding.textViewMessage.text = message.text
                holder.binding.textViewTime.text = message.timestamp?.let { formatDate(it) } ?: ""
                holder.binding.textViewUsername.text = message.username
                holder.binding.root.setOnClickListener { onMessageClick(message, position) }
            }
        }
    }

    fun addMessages(newMessages: List<SessionMessage>) {
        val startPosition = messageList.size
        messageList.addAll(newMessages)
        notifyItemRangeInserted(startPosition, newMessages.size)
    }

    override fun getItemCount(): Int = messageList.size
    inner class AiViewHolder(val binding: MessageAiItemBinding) : RecyclerView.ViewHolder(binding.root)
    inner class UserLoggedViewHolder(val binding: MessageUserItemBinding) : RecyclerView.ViewHolder(binding.root)
    inner class OtherUserViewHolder(val binding: MessageOtherUserItemBinding) : RecyclerView.ViewHolder(binding.root)
}