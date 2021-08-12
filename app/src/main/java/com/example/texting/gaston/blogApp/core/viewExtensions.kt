package com.example.texting.gaston.blogApp.core

import android.content.Context
import android.view.View
import android.widget.Toast

fun View.hide(){
    this.visibility = View.GONE
}

fun View.show(){
    this.visibility = View.VISIBLE
}

fun toast(context: Context, msg : String){
    Toast.makeText(
        context,
        msg,
        Toast.LENGTH_SHORT
    ).show()

}


