package com.halcyonmobile.adoption.chat

/**
 * Created by AoD Akitektuo on 27-May-18 at 17:47.
 */
class ChatModels {

    data class Conversation(
            val userSenderId: String = "",
            val userReceiverId: String = "",
            var lastMessage: Long = 0,
            val id: String = ""
    )

    data class Message(
            val conversationId: String = "",
            val userSenderId: String = "",
            val userReceiverId: String = "",
            val message: String = "",
            val image: String = "",
            val time: Long = 0,
            val id: String = ""
    )

}