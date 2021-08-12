package com.example.texting.gaston.blogApp.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.texting.databinding.PostItemViewBinding
import com.example.texting.gaston.blogApp.core.BaseViewHolder
import com.example.texting.gaston.blogApp.core.TimeUtils
import com.example.texting.gaston.blogApp.core.hide
import com.example.texting.gaston.blogApp.core.show
import com.example.texting.gaston.blogApp.data.model.Post

class HomeScreenAdapter(private val postList: List<Post>) :
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding =
            PostItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeScreenViewHolder(itemBinding, parent.context)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is HomeScreenViewHolder -> holder.bind(postList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int = postList.size

    private inner class HomeScreenViewHolder(
        val binding: PostItemViewBinding,
        val context: Context
    ) : BaseViewHolder<Post>(binding.root) {
        override fun bind(item: Post) {
            Glide.with(context).load(item.post_image).centerCrop().into(binding.postImage)
            Glide.with(context).load(item.profile_picture).centerCrop().into(binding.profilePicture)
            binding.profileUser.text = item.profile_name

            if(item.post_description.isEmpty()){
                binding.postDescription.hide()
            }else{
                binding.postDescription.show()
                binding.postDescription.text = item.post_description
            }

            /*get time in days convert to miliseconds and div between 1000 to convert to seconds*/
            val createdAt = (item.created_at?.time?.div(1000L))?.let {
                TimeUtils.getTimeAgo(it.toInt())
            }

            binding.postTimeStamp.text = createdAt

        }

    }


}