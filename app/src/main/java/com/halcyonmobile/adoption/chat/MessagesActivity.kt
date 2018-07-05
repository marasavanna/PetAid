package com.halcyonmobile.adoption.chat

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.halcyonmobile.adoption.ImageBindingAdapter
import com.halcyonmobile.adoption.R
import kotlinx.android.synthetic.main.activity_messages.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class MessagesActivity : AppCompatActivity() {

    private val adapter = MessageAdapter()
    private lateinit var conversationId: String
    private lateinit var image: String
    private lateinit var username: String
    private lateinit var onItemClick: (id: String, image: String) -> Unit
    private val userId = FirebaseAuth.getInstance().currentUser?.uid!!
    private val database = FirebaseDatabase.getInstance().reference
    private val databaseConversations = database.child("conversations")
    private val databaseMessages = database.child("messages")
    private var corespondentId = ""
    private val storage = FirebaseStorage.getInstance().reference.child("messages")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        listMessages.layoutManager = LinearLayoutManager(this)
        listMessages.adapter = adapter
        conversationId = intent.getStringExtra("id")
        image = intent.getStringExtra("corespondentImage")
        username = intent.getStringExtra("corespondentUsername")
        corespondentId = intent.getStringExtra("corespondentId")
        if (conversationId.isEmpty()) {
            databaseConversations.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(data: DataSnapshot) {
                    val conversations = ArrayList<ChatModels.Conversation>()
                    data.children.mapNotNullTo(conversations, { it.getValue(ChatModels.Conversation::class.java) })
                    if (conversations.none { it.userSenderId == corespondentId && it.userReceiverId == userId || it.userReceiverId == corespondentId && it.userSenderId == userId}) {
                        val id = databaseConversations.push().key!!
                        databaseConversations.child(id).setValue(ChatModels.Conversation(userId, corespondentId, id = id))
                        conversationId = id
                    } else {
                        conversations.filter { it.userSenderId == corespondentId || it.userReceiverId == corespondentId }.forEach {
                            conversationId = it.id
                        }
                    }
                    searchMessages()
                }

            })
        } else {
            searchMessages()
        }
        ImageBindingAdapter.setImageUrl(imageUser, image)
        textUsername.text = username
        adapter.clear()
        onItemClick = { id, image ->
            if (ContextCompat.checkSelfPermission(this@MessagesActivity,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MessagesActivity,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        0)
                toast("Permission needed")
            } else {
                getBitmapFromURL(image, id)
            }
        }
//        databaseConversations.child(conversationId).addListenerForSingleValueEvent(object : ValueEventListener {
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//            override fun onDataChange(data: DataSnapshot) {
//                val conversation = data.getValue(ChatModels.Conversation::class.java) as ChatModels.Conversation
//                corespondentId = if (conversation.userSenderId == userId) {
//                    conversation.userReceiverId
//                } else {
//                    conversation.userSenderId
//                }
//                databaseMessages.addListenerForSingleValueEvent(object : ValueEventListener {
//
//                    override fun onCancelled(error: DatabaseError) {
//
//                    }
//
//                    override fun onDataChange(data: DataSnapshot) {
//                        val messages = ArrayList<ChatModels.Message>()
//                        data.children.mapNotNullTo(messages, { it.getValue(ChatModels.Message::class.java) })
//                        messages.filter { it.conversationId == conversationId }.sortedBy { it.time }
//                                .forEach {
//                                    adapter.add(MessageAdapter.ViewHolder.Message(it.id, it.image,
//                                            it.message, getTime(it.time), userId == it.userSenderId, onItemClick))
//                                }
//                    }
//
//                })
//            }
//
//        })
        imageSend.setOnClickListener {
            val message = editMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                val time = Calendar.getInstance().timeInMillis
                val id = databaseMessages.push().key!!
                databaseMessages.child(id).setValue(ChatModels.Message(conversationId, userId,
                        corespondentId, message, "", time, id))
                databaseConversations.child(conversationId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(data: DataSnapshot) {
                        val conversation = data.getValue(ChatModels.Conversation::class.java)!!
                        conversation.lastMessage = time
                        databaseConversations.child(conversationId).setValue(conversation)
                    }

                })
                editMessage.setText("")
            }
        }
        imagePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@MessagesActivity,
                            android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MessagesActivity,
                        arrayOf(android.Manifest.permission.CAMERA),
                        1)
                toast("Permission needed")
            } else {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 3)
            }
        }
        imageGallery.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@MessagesActivity,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MessagesActivity,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        2)
                toast("Permission needed")
            } else {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(intent, 4)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                3 -> {
                    val image = getImageUri(data?.extras?.get("data") as Bitmap)
                    val id = databaseMessages.push().key!!
                    storage.child(id).putFile(image).addOnSuccessListener {
                        storage.child(id).downloadUrl.addOnSuccessListener {
                            val time = Calendar.getInstance().timeInMillis
                            databaseMessages.child(id).setValue(ChatModels.Message(conversationId, userId,
                                    corespondentId, "", it.toString(), time, id))
                            databaseConversations.child(conversationId).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {

                                }

                                override fun onDataChange(data: DataSnapshot) {
                                    val conversation = data.getValue(ChatModels.Conversation::class.java)!!
                                    conversation.lastMessage = time
                                    databaseConversations.child(conversationId).setValue(conversation)
                                }

                            })
                        }
                    }
                }
                4 -> {
                    val image = data?.data!!
                    val id = databaseMessages.push().key!!
                    storage.child(id).putFile(image).addOnSuccessListener {
                        storage.child(id).downloadUrl.addOnSuccessListener {
                            val time = Calendar.getInstance().timeInMillis
                            databaseMessages.child(id).setValue(ChatModels.Message(conversationId, userId,
                                    corespondentId, "", it.toString(), time, id))
                            databaseConversations.child(conversationId).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {

                                }

                                override fun onDataChange(data: DataSnapshot) {
                                    val conversation = data.getValue(ChatModels.Conversation::class.java)!!
                                    conversation.lastMessage = time
                                    databaseConversations.child(conversationId).setValue(conversation)
                                }

                            })
                        }
                    }
                }
            }
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

    private fun getBitmapFromURL(src: String, id: String) {
        val handler = Handler()
        val r = Runnable {
            handler.post {
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
                try {
                    val url = URL(src)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input = connection.inputStream
                    toast("Image saved")
                    val image = BitmapFactory.decodeStream(input)
                    MediaStore.Images.Media.insertImage(contentResolver, image, id, "")
                } catch (e: IOException) {
                    // Log exception
                    null
                }
            }
        }
        val t = Thread(r)
        t.start()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun searchMessages() {
        databaseMessages.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(data: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(data: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(data: DataSnapshot, p1: String?) {
                val message = data.getValue(ChatModels.Message::class.java)!!
                if (message.conversationId == conversationId) {
                    adapter.add(MessageAdapter.ViewHolder.Message(message.id, message.image, message.message, getTime(message.time), userId == message.userSenderId, onItemClick))
                }
            }

            override fun onChildRemoved(data: DataSnapshot) {
            }

        })
    }

    private fun getImageUri(image: Bitmap) : Uri {
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(this.contentResolver, image, "photo", null)
        return Uri.parse(path)
    }

}
