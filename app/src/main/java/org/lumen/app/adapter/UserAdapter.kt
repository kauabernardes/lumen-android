package org.lumen.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import org.lumen.app.data.model.User
import org.lumen.app.databinding.UserItemBinding

class UserAdapter (
    var userList: List<User>
): RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.username.text = "@${user.username}"
    }

    override fun getItemCount(): Int {
        return userList.size
    }


    inner class MyViewHolder (val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }


}