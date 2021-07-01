package com.example.texting.gaston.blogApp.core

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.texting.gaston.blogApp.data.model.Post

abstract class BaseViewHolder<T>(itemView : View) : RecyclerView.ViewHolder(itemView){

    abstract fun bind(item : T)

}
