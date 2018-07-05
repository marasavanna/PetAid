package com.halcyonmobile.adoption.chat

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.halcyonmobile.adoption.ImageBindingAdapter
import com.halcyonmobile.adoption.R
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by AoD Akitektuo on 27-May-18 at 17:17.
 */
class ConversationAdapter : RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {

    private val conversations = ArrayList<ViewHolder.Conversation>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_conversation, parent, false))

    override fun getItemCount(): Int = conversations.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(conversations[position])

    fun add(conversation: ViewHolder.Conversation) {
        val position = conversations.size
        conversations.add(conversation)
        notifyItemInserted(position)
    }

    fun clear() {
        conversations.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        data class Conversation(val id: String, val image: String, val username: String, val message: String, val time: String, val onClick: (id: String) -> Unit)

        private val imageUser = itemView.findViewById<CircleImageView>(R.id.imageUser)
        private val textUsername = itemView.findViewById<TextView>(R.id.textUsername)
        private val textMessage = itemView.findViewById<TextView>(R.id.textMessage)
        private val textTime = itemView.findViewById<TextView>(R.id.textTime)

        fun bind(conversation: Conversation) = with(conversation) {
            ImageBindingAdapter.setImageUrl(imageUser, image)
            textUsername.text = username
            textMessage.text = message
            textTime.text = time
            itemView.setOnClickListener { onClick(id) }
        }

    }

}