package com.halcyonmobile.adoption.chat

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.halcyonmobile.adoption.ImageBindingAdapter
import com.halcyonmobile.adoption.R
import java.util.*

/**
 * Created by AoD Akitektuo on 27-May-18 at 17:17.
 */
class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private val messages = ArrayList<ViewHolder.Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false))

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(messages[position])

    fun add(message: ViewHolder.Message) {
        val position = messages.size
        messages.add(message)
        notifyItemInserted(position)
    }

    fun clear() {
        messages.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        data class Message(val id: String, val image: String, val message: String, val time: String, val isSender: Boolean, val onClick: (id: String, image: String) -> Unit)

        private val cardReceive = itemView.findViewById<CardView>(R.id.cardReceive)
        private val cardSend = itemView.findViewById<CardView>(R.id.cardSend)
        private val imageReceive = itemView.findViewById<ImageView>(R.id.imageReceive)
        private val imageSend = itemView.findViewById<ImageView>(R.id.imageSend)
        private val textReceive = itemView.findViewById<TextView>(R.id.textMessageReceive)
        private val textSend = itemView.findViewById<TextView>(R.id.textMessageSend)
        private val textTimeReceive = itemView.findViewById<TextView>(R.id.textTimeReceive)
        private val textTimeSend = itemView.findViewById<TextView>(R.id.textTimeSend)

        fun bind(message: Message) = with(message) {
            if (isSender) {
                cardReceive.visibility = View.GONE
                imageReceive.visibility = View.GONE
                textReceive.visibility = View.GONE
                textTimeReceive.visibility = View.GONE
                cardSend.visibility = View.VISIBLE
                imageSend.visibility = View.VISIBLE
                textSend.visibility = View.VISIBLE
                textTimeSend.visibility = View.VISIBLE
                if (image.isEmpty()) {
                    textSend.visibility = View.VISIBLE
                    imageSend.visibility = View.GONE
                    textSend.text = this.message
                } else {
                    textSend.visibility = View.GONE
                    imageSend.visibility = View.VISIBLE
                    ImageBindingAdapter.setImageUrl(imageSend, image)
                    itemView.setOnClickListener {
                        onClick(id, image)
                    }
                }
                textTimeSend.text = time
            } else {
                cardSend.visibility = View.GONE
                imageSend.visibility = View.GONE
                textSend.visibility = View.GONE
                textTimeSend.visibility = View.GONE
                cardReceive.visibility = View.VISIBLE
                imageReceive.visibility = View.VISIBLE
                textReceive.visibility = View.VISIBLE
                textTimeReceive.visibility = View.VISIBLE
                if (image.isEmpty()) {
                    textReceive.visibility = View.VISIBLE
                    imageReceive.visibility = View.GONE
                    textReceive.text = this.message
                } else {
                    textReceive.visibility = View.GONE
                    imageReceive.visibility = View.VISIBLE
                    ImageBindingAdapter.setImageUrl(imageReceive, image)
                    itemView.setOnClickListener {
                        onClick(id, image)
                    }
                }
                textTimeReceive.text = time
            }
        }

    }

}