package com.halcyonmobile.adoption.pet

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.halcyonmobile.adoption.ImageBindingAdapter
import com.halcyonmobile.adoption.R
import com.halcyonmobile.adoption.chat.MessagesActivity
import com.halcyonmobile.adoption.model.Pet
import com.halcyonmobile.adoption.model.User
import com.halcyonmobile.adoption.profile.UserDetailActivity
import kotlinx.android.synthetic.main.activity_pet_detail.*

class PetDetailActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val databasePets = database.child("pets")
    private val databaseWishlist = database.child("wishlist")
    private val databaseUsers = database.child("users")
    private var userId = ""
    private var petId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_detail)
        petId = intent.getStringExtra("id")
        userId = auth.currentUser?.uid!!
        databasePets.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(data: DataSnapshot) {
                val pets = ArrayList<Pet>()
                data.children.mapNotNullTo(pets, { it.getValue(Pet::class.java) })
                pets.forEach {
                    if (it.id == petId) {
                        if (it.userId == userId) {
                            imageWishlist.visibility = View.GONE
                            imageOwner.visibility = View.GONE
                            textContact.visibility = View.GONE
                            imageContact.visibility = View.GONE
                        } else {
                            databaseUsers.child(it.userId).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {

                                }

                                override fun onDataChange(data: DataSnapshot) {
                                    val user = data.getValue(User::class.java)!!
                                    ImageBindingAdapter.setImageUrl(imageOwner, user.image)
                                    textContact.text = "Contact ${user.username}:"
                                    imageContact.setOnClickListener {
                                        val intent = Intent(this@PetDetailActivity, MessagesActivity::class.java)
                                        intent.putExtra("id", "")
                                        intent.putExtra("corespondentId", user.id)
                                        intent.putExtra("corespondentImage", user.image)
                                        intent.putExtra("corespondentUsername", user.username)
                                        startActivity(intent)
                                    }
                                    imageOwner.setOnClickListener {
                                        val intent = Intent(this@PetDetailActivity, UserDetailActivity::class.java)
                                        intent.putExtra("id", user.id)
                                        startActivity(intent)
                                    }
                                }

                            })
                        }
                        ImageBindingAdapter.setImageUrl(imagePet, it.imageMain)
                        ImageBindingAdapter.setImageUrl(imageBackground, it.imageBackground)
                        textIntro.text = "${it.name}, ${it.getAgeString()} old"
                        textDescription.text = it.description
                    }
                }
            }

        })
        databaseWishlist.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(data: DataSnapshot) {
                val wishlists = ArrayList<Wishlist>()
                data.children.mapNotNullTo(wishlists, { it.getValue(Wishlist::class.java) })
                if (wishlists.none { it.petId == petId }) {
                    imageWishlist.setImageResource(R.drawable.favorite_gray)
                } else {
                    imageWishlist.setImageResource(R.drawable.favorite_red)
                }
            }

        })
        imageWishlist.setOnClickListener {
            databaseWishlist.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(data: DataSnapshot) {
                    val wishlists = ArrayList<Wishlist>()
                    data.children.mapNotNullTo(wishlists, { it.getValue(Wishlist::class.java) })
                    var connectionNotFound = true
                    wishlists.forEach {
                        if (it.petId == petId) {
                            databaseWishlist.child(it.id).removeValue()
                            connectionNotFound = false
                        }
                    }
                    if (connectionNotFound) {
                        val id = databaseWishlist.push().key!!
                        databaseWishlist.child(id).setValue(Wishlist(userId, petId, id))
                        imageWishlist.setImageResource(R.drawable.favorite_red)
                    } else {
                        imageWishlist.setImageResource(R.drawable.favorite_gray)
                    }
                }

            })
        }
    }

}
