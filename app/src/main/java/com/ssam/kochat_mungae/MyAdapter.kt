package com.ssam.kochat_mungae

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(val mDataSet : ArrayList<Chat>, val stEmail : String) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    var stmyEmail = stEmail

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvChat : TextView= itemView.findViewById(R.id.tvChat)
    }

    override fun getItemViewType(position: Int): Int {
        if(mDataSet.get(position).email.equals(stmyEmail)){
            return 1
        } else {
            return 2
        }

        //return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        var view: View = layoutInflater.inflate(R.layout.my_text_view, parent, false)
        if(viewType == 1){
            view = layoutInflater.inflate(R.layout.right_text_view, parent, false)
        } else {
            view = layoutInflater.inflate(R.layout.my_text_view, parent, false)
        }

        val viewHolder : ViewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvChat.text = "[${mDataSet[position].email}] "+mDataSet[position].etMessage

    }

}
