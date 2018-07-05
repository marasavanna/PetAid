package com.halcyonmobile.adoption.profile

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.halcyonmobile.adoption.ImageBindingAdapter
import com.halcyonmobile.adoption.R
import com.halcyonmobile.adoption.chat.MessagesActivity
import com.halcyonmobile.adoption.model.User
import kotlinx.android.synthetic.main.activity_user_detail.*
import java.util.*

class UserDetailActivity : AppCompatActivity() {

    private val databaseUsers = FirebaseDatabase.getInstance().reference.child("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        val userId = intent.getStringExtra("id")
        databaseUsers.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(data: DataSnapshot) {
                val user = data.getValue(User::class.java)!!
                if (user.city.isNotEmpty()) {
                    textDescription.text = "This is ${user.username}, who lives in ${user.city}"
                }
                textNameAge.text = "${user.username}, ${getAge(user.dob)}"
                if (user.image.isNotEmpty()) {
                    ImageBindingAdapter.setImageUrl(imageProfile, user.image)
                }
                textAdoptions.text = "${user.adoptedPets} adopted pets"
                textGiven.text = "${user.givenPets} given pets"
                buttonSendMessage.setOnClickListener {
                    val intent = Intent(this@UserDetailActivity, MessagesActivity::class.java)
                    intent.putExtra("id", "")
                    intent.putExtra("corespondentId", user.id)
                    intent.putExtra("corespondentImage", user.image)
                    intent.putExtra("corespondentUsername", user.username)
                    startActivity(intent)
                }
            }

        })
    }

    private fun getAge(time: Long): Int {
        val present = Calendar.getInstance()
        val userDate = Calendar.getInstance()
        userDate.time = Date(time)
        return present.get(Calendar.YEAR) - userDate.get(Calendar.YEAR)
    }

}
