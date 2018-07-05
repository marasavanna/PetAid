package com.halcyonmobile.adoption.pet

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.halcyonmobile.adoption.R
import com.halcyonmobile.adoption.model.Pet
import com.halcyonmobile.adoption.profile.PetAdapter
import kotlinx.android.synthetic.main.activity_manage_pets.*

class ManagePetsActivity : AppCompatActivity() {

    private val pets = ArrayList<Pet>()
    private var isWishlist = false
    private lateinit var adapter: PetAdapter
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val databasePets = database.child("pets")
    private val databaseWishlist = database.child("wishlist")
    private var userId = ""
    private var lastModifiedPet = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_pets)

        isWishlist = intent.getBooleanExtra("isWishlist", false)

        userId = auth.currentUser?.uid!!

        listPets.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        adapter = PetAdapter(pets, { pet, _ ->
            val intent = Intent(this, PetDetailActivity::class.java)
            intent.putExtra("id", pet.id)
            startActivity(intent)
        }, { pet, position ->
            if (!isWishlist) {
                optionsForPets(pet, position)
            } else {
                optionsForWishlist(pet)
            }
        })
        listPets.adapter = adapter
        if (isWishlist) {
            databaseWishlist.addChildEventListener(object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onChildMoved(data: DataSnapshot, prevChildName: String?) {
                }

                override fun onChildChanged(data: DataSnapshot, prevChildName: String?) {
                }

                override fun onChildAdded(data: DataSnapshot, prevChildName: String?) {
                    val wishlist = data.getValue(Wishlist::class.java)!!
                    databasePets.child(wishlist.petId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(data: DataSnapshot) {
                            adapter.add(data.getValue(Pet::class.java))
                        }

                    })

                }
                override fun onChildRemoved(data: DataSnapshot) {
                    val wishlist = data.getValue(Wishlist::class.java)!!
                    databasePets.child(wishlist.petId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(data: DataSnapshot) {
                            adapter.remove(data.getValue(Pet::class.java))
                            recreate()
                        }

                    })
                }
            })
        } else {

        }
    }

    private fun optionsForWishlist(pet: Pet) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select action")
        builder.setItems(R.array.actions_wishlist, { _, i ->
            if (i == 0) {
                databaseWishlist.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(data: DataSnapshot) {
                        val wishlists = ArrayList<Wishlist>()
                        data.children.mapNotNullTo(wishlists, { it.getValue(Wishlist::class.java) })
                        wishlists.filter { it.petId == pet.id }.forEach {
                            databaseWishlist.child(it.id).removeValue()
                        }
                    }
                })
            }
        })
        builder.setNeutralButton("Cancel", null)
        builder.show()
    }

    private fun optionsForPets(pet: Pet, position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select action")
        builder.setItems(R.array.actions_pets, { _, i ->
            when (i) {
                0 -> {
                    lastModifiedPet = position
                    val intent = Intent(this, EditPetActivity::class.java)
                    intent.putExtra("id", pet.id)
                    startActivity(intent)
                }
                1 -> {
                    databaseWishlist.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(data: DataSnapshot) {
                            val wishlists = ArrayList<Wishlist>()
                            data.children.mapNotNullTo(wishlists, { it.getValue(Wishlist::class.java) })
                            wishlists.filter { it.petId == pet.id }.forEach {
                                databaseWishlist.child(it.id).removeValue()
                            }
                        }
                    })
                    databasePets.child(pet.id).removeValue()
                }
            }
        })
        builder.setNeutralButton("Cancel", null)
        builder.show()
    }

}
