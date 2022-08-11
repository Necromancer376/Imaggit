package com.example.memeshare

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_starred.view.*
import java.net.URL

class StarredItemsAdapter(
    private val context: Context,
    private val list: ArrayList<String>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val picasso = Picasso.get()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_starred,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentUrl = list[position]
//        val image = BitmapFactory.decodeStream(currentUrl.openConnection().getInputStream())

        picasso
            .load(currentUrl)
            .error(R.drawable.ic_image_placeholder)
            .fit()
            .into(holder.itemView.imgItemStarred)

        holder.itemView.ll_item.setOnClickListener {
            val intent = Intent(context,FullImageActivity::class.java)
            intent.putExtra("imgID",currentUrl)
            context.startActivity(intent)
        }
    }

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.imgItemStarred)
        val layout = view.findViewById<LinearLayout>(R.id.ll_item)
    }

}