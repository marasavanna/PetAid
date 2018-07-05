package com.halcyonmobile.adoption.pet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.halcyonmobile.adoption.ImageBindingAdapter
import com.halcyonmobile.adoption.R
import com.halcyonmobile.adoption.model.Pet
import kotlinx.android.synthetic.main.activity_edit_pet.*

class EditPetActivity : AppCompatActivity() {

    companion object {
        private const val CODE_MAIN = 1
        private const val CODE_BACKGROUND = 2
    }

    private val database = FirebaseDatabase.getInstance().reference
    private val storage = FirebaseStorage.getInstance().reference
    private val databasePets = database.child("pets")
    private var petId = ""
    private lateinit var pet: Pet
    private var changedMain = false
    private var changedBackground = false
    private lateinit var imageMainUri: Uri
    private lateinit var imageBackgroundUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pet)
        petId = intent.getStringExtra("id")
        databasePets.child(petId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(data: DataSnapshot) {
                pet = data.getValue(Pet::class.java)!!
                ImageBindingAdapter.setImageUrl(imageMain, pet.imageMain)
                ImageBindingAdapter.setImageUrl(imageBackground, pet.imageBackground)
                textDescription.setText(pet.description)
            }

        })

        buttonSave.setOnClickListener {
            if (changedMain) {
                val petStorageMain = storage.child("pets").child(pet.id + 1)
                petStorageMain.putFile(imageMainUri).addOnFailureListener { println(it.message) }.addOnSuccessListener {
                    petStorageMain.downloadUrl.addOnSuccessListener {
                        pet.imageMain = it.toString()
                        databasePets.child(pet.id).setValue(pet)
                    }
                }
            }
            if (changedBackground) {
                val petStorageBackground = storage.child("pets").child(pet.id + 2)
                petStorageBackground.putFile(imageBackgroundUri).addOnFailureListener({ println(it.message) }).addOnSuccessListener {
                    petStorageBackground.downloadUrl.addOnSuccessListener {
                        pet.imageBackground = it.toString()
                        databasePets.child(pet.id).setValue(pet)
                    }
                }
            }
            if (textDescription.text.toString().isNotEmpty()) {
                pet.description = textDescription.text.toString()
                databasePets.child(pet.id).setValue(pet)
            }
        }

        imageMain.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), CODE_MAIN)
        }

        imageBackground.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), CODE_BACKGROUND)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CODE_MAIN -> {
                    imageMainUri = data?.data!!
                    changedMain = true
                    ImageBindingAdapter.setImageUrl(imageMain, imageMainUri.toString())
                }
                CODE_BACKGROUND -> {
                    imageBackgroundUri = data?.data!!
                    changedBackground = true
                    ImageBindingAdapter.setImageUrl(imageBackground, imageBackgroundUri.toString())
                }
            }
        }
    }

}
