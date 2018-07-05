package com.halcyonmobile.adoption.profile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.halcyonmobile.adoption.R
import com.halcyonmobile.adoption.chat.ChatModels
import com.halcyonmobile.adoption.chat.ConversationAdapter
import com.halcyonmobile.adoption.chat.MessagesActivity
import com.halcyonmobile.adoption.model.User
import com.halcyonmobile.adoption.pet.SearchPetActivity
import kotlinx.android.synthetic.main.fragment_chat.*
import java.util.*

class ChatFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            layoutInflater.inflate(R.layout.fragment_chat, container, false)


    companion object {
        fun getInstance() : ChatFragment = ChatFragment()
    }

    private val adapter = ConversationAdapter()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid!!
    private val database = FirebaseDatabase.getInstance().reference
    private val databaseConversations = database.child("conversations")
    private val databaseMessages = database.child("messages")
    private val databaseUsers = database.child("users")

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listConversations.layoutManager = LinearLayoutManager(context)
        listConversations.adapter = adapter
        toolbar.title = "Messages"
        toolbar.inflateMenu(R.menu.toolbar_menu)
        toolbar.setTitleTextColor(resources.getColor(R.color.button_material_light))

        adapter.clear()
        databaseConversations.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(data: DataSnapshot) {
                val conversations = ArrayList<ChatModels.Conversation>()
                data.children.mapNotNullTo(conversations, { it.getValue(ChatModels.Conversation::class.java) })
                conversations.filter { it.userReceiverId == userId || it.userSenderId == userId }
                        .sortedByDescending { it.lastMessage }.forEach {
                            val conversation = it
                            databaseUsers.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {

                                }

                                override fun onDataChange(data: DataSnapshot) {
                                    val users = ArrayList<User>()
                                    data.children.mapNotNullTo(users, { it.getValue(User::class.java) })
                                    var imageCorespondent = ""
                                    var corespondentUsername = ""
                                    var corespondentId = ""
                                    if (userId == conversation.userReceiverId) {
                                        users.filter { it.id == conversation.userSenderId }.forEach {
                                            imageCorespondent = it.image
                                            corespondentUsername = it.username
                                            corespondentId = it.id
                                        }
                                    } else {
                                        users.filter { it.id == conversation.userReceiverId }.forEach {
                                            imageCorespondent = it.image
                                            corespondentUsername = it.username
                                            corespondentId = it.id
                                        }
                                    }
                                    databaseMessages.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(error: DatabaseError) {

                                        }

                                        override fun onDataChange(data: DataSnapshot) {
                                            val messages = ArrayList<ChatModels.Message>()
                                            data.children.mapNotNullTo(messages, { it.getValue(ChatModels.Message::class.java) })
                                            messages.filter { it.conversationId == conversation.id && it.time == conversation.lastMessage }.forEach {
                                                var message = it.message
                                                if (message.length > 25) {
                                                    message = "${message.substring(0, 25)}..."
                                                } else if (message.isEmpty()) {
                                                    message = "Photo"
                                                }
                                                adapter.add(ConversationAdapter.ViewHolder.Conversation(conversation.id,
                                                        imageCorespondent, corespondentUsername, message,
                                                        getTime(conversation.lastMessage), {
                                                    val intent = Intent(context, MessagesActivity::class.java)
                                                    intent.putExtra("id", it)
                                                    intent.putExtra("corespondentImage", imageCorespondent)
                                                    intent.putExtra("corespondentUsername", corespondentUsername)
                                                    intent.putExtra("corespondentId", corespondentId)
                                                    startActivity(intent)
                                                }))
                                            }
                                        }

                                    })
                                }

                            })
                        }
            }

        })

       toolbar.setOnMenuItemClickListener {
           when (it.itemId) {
               R.id.search -> {
                   startActivity(SearchUserActivity.getStartIntent(context))
                   return@setOnMenuItemClickListener true
               }
           }
           return@setOnMenuItemClickListener false
       }
    }

    private fun getTime(date: Long): String {
        val present = Calendar.getInstance()
        val time = Calendar.getInstance()
        time.time = Date(date)
        val days = present.get(Calendar.DAY_OF_YEAR) - time.get(Calendar.DAY_OF_YEAR)
        if (days > 6) {
            return "${time.get(Calendar.DAY_OF_MONTH)} ${getMonthName(time.get(Calendar.MONTH))}"
        }
        if (days < 1) {
            return "${time.get(Calendar.HOUR_OF_DAY)}:${fixMinute(time.get(Calendar.MINUTE))}"
        }
        return getDayName(time.get(Calendar.DAY_OF_WEEK))
    }

    private fun fixMinute(minutes: Int): String {
        var res = minutes.toString()
        if (minutes < 10) {
            res = "0$res"
        }
        return res
    }

    private fun getMonthName(month: Int): String = when (month) {
        Calendar.JANUARY -> "Jan"
        Calendar.FEBRUARY -> "Feb"
        Calendar.MARCH -> "Mar"
        Calendar.APRIL -> "Apr"
        Calendar.MAY -> "May"
        Calendar.JUNE -> "June"
        Calendar.JULY -> "July"
        Calendar.AUGUST -> "Aug"
        Calendar.SEPTEMBER -> "Sep"
        Calendar.OCTOBER -> "Oct"
        Calendar.NOVEMBER -> "Nov"
        else -> "Dec"
    }

    private fun getDayName(day: Int): String = when (day) {
        Calendar.MONDAY -> "Mon"
        Calendar.TUESDAY -> "Tue"
        Calendar.WEDNESDAY -> "Wed"
        Calendar.THURSDAY -> "Thu"
        Calendar.FRIDAY -> "Fri"
        Calendar.SATURDAY -> "Sat"
        else -> "Sun"
    }

    private fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
