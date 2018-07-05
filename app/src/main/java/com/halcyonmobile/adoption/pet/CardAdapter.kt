package com.halcyonmobile.adoption.pet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.halcyonmobile.adoption.ImageBindingAdapter
import com.halcyonmobile.adoption.R
import com.halcyonmobile.adoption.model.Pet

/**
 * Created by AoD Akitektuo on 02-Jun-18 at 23:01.
 */
class CardAdapter(context: Context) : ArrayAdapter<Pet>(context, 0) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val viewHolder: ViewHolder
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.bind(getItem(position))
        return view!!
    }

    private class ViewHolder(view: View) {

        val imagePet: ImageView = view.findViewById(R.id.imagePet)
        val textName: TextView = view.findViewById(R.id.textName)
        val textAge: TextView = view.findViewById(R.id.textAge)

        fun bind(pet: Pet) {
            ImageBindingAdapter.setImageUrl(imagePet, pet.imageMain)
            textName.text = pet.name
            textAge.text = "${pet.getAgeString()} old, ${pet.species}"
        }

    }

}