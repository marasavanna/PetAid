package com.halcyonmobile.adoption.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.AsyncTask
import android.os.Binder
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.halcyonmobile.adoption.R
import com.halcyonmobile.adoption.chat.ChatModels
import com.halcyonmobile.adoption.chat.MessagesActivity
import com.halcyonmobile.adoption.model.User

/**
 * Created by AoD Akitektuo on 28-May-18 at 00:08.
 */
class NotificationService : Service() {

    private val binder = MyBinder(this)
    private val userId = FirebaseAuth.getInstance().currentUser?.uid!!
    private val databaseMessages = FirebaseDatabase.getInstance().reference.child("messages")
    private val databaseUsers = FirebaseDatabase.getInstance().reference.child("users")

    override fun onBind(intent: Intent?): IBinder = binder

    class MyBinder(val service: NotificationService) : Binder() {
        fun getNotificationService(): NotificationService = service
    }

    val task = @SuppressLint("StaticFieldLeak")
    object : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void): Void? {
            databaseMessages.addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildAdded(data: DataSnapshot, p1: String?) {
                    val message = data.getValue(ChatModels.Message::class.java)!!
                    if (message.userReceiverId == userId) {
                        databaseUsers.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(data: DataSnapshot) {
                                val user = data.getValue(User::class.java)!!
                                val intent = Intent(application, MessagesActivity::class.java)
                                intent.putExtra("id", message.conversationId)
                                intent.putExtra("corespondentImage", user.image)
                                intent.putExtra("corespondentUsername", user.username)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                val pendingIntent = PendingIntent.getActivity(application, 0, intent, 0)
                                val builder = NotificationCompat.Builder(application, "Messages")
                                        .setSmallIcon(R.drawable.ic_pets_black_24dp)
                                        .setContentTitle(user.username)
                                        .setContentText(message.message)
                                        .setContentIntent(pendingIntent)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setAutoCancel(true)
                                val notificationManager = NotificationManagerCompat.from(application)
                                notificationManager.notify(0, builder.build())
                            }

                        })
                    }
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }

            })
            return null
        }
    }

}