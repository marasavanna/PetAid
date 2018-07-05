package com.halcyonmobile.adoption.pet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.halcyonmobile.adoption.R
import com.halcyonmobile.adoption.model.Pet
import com.halcyonmobile.adoption.model.TraitConnection
import com.halcyonmobile.adoption.profile.PetAdapter
import com.halcyonmobile.adoption.profile.PetsFragment
import kotlinx.android.synthetic.main.activity_recommended.*

/**
 * Created by AoD Akitektuo on 05-Jul-18 at 18:48.
 */
class RecommendedActivity : AppCompatActivity() {

    data class TraitSearch(val id: String = "", var app: Int = 0)

    private val pets = ArrayList<Pet>()
    private lateinit var adapter: PetAdapter
    private lateinit var recommendedPets: List<Pet>
    private val database = FirebaseDatabase.getInstance().reference

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, RecommendedActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommended)
        recommendedPets = PetsFragment.recommmendedPets
        adapter = PetAdapter(pets, PetAdapter.OnClick { pet, _ ->
            val intent = Intent(this, PetDetailActivity::class.java)
            intent.putExtra("id", pet.id)
            startActivity(intent)
        })
        listRecommendedPets.layoutManager = LinearLayoutManager(this)
        listRecommendedPets.adapter = adapter

        findPets()
    }

    private fun findPets() {
        val traits = ArrayList<TraitSearch>()
        database.child("traits").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(data: DataSnapshot) {
                val traitConnections = ArrayList<TraitConnection>()
                data.children.mapNotNullTo(traitConnections) { it.getValue(TraitConnection::class.java) }
                recommendedPets.forEach {
                    val pet = it
                    traitConnections.filter { pet.id == it.targetId }.forEach {
                        val traitId = it.traitId
                        if (traits.any { it.id == traitId }) {
                            for (i in 0 until traits.size) {
                                if (traits[i].id == traitId) {
                                    traits[i].app++
                                }
                            }
                        } else {
                            traits.add(TraitSearch(traitId))
                        }
                        traits.sortByDescending { it.app }
                        for (i in 0..2) {
                            if (i < traits.size) {
                                traitConnections.filter { it.traitId == traits[i].id }.forEach {
                                    val traitConnection = it
                                    database.child("pets").addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(error: DatabaseError) {
                                        }

                                        override fun onDataChange(data: DataSnapshot) {
                                            val pets = ArrayList<Pet>()
                                            data.children.mapNotNullTo(pets) { it.getValue(Pet::class.java) }
                                            pets.filter { it.id == traitConnection.targetId }.forEach {
                                                val p = it
                                                if (this@RecommendedActivity.pets.none { it.id == p.id }) {
                                                    adapter.add(it)
                                                }
                                            }
                                        }

                                    })
                                }
                            }
                        }
                    }
                }
            }

        })
    }

}