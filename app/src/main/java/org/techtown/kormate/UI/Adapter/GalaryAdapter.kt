package org.techtown.kormate.UI.Adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.techtown.kormate.databinding.ActivityBoardPostBinding
import org.techtown.kormate.databinding.GalaryimgBinding

class GalaryAdapter(private val imageUris: MutableList<String>, private var acBinding : ActivityBoardPostBinding) :
    RecyclerView.Adapter<GalaryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = GalaryimgBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val uri = imageUris[position]
        holder.bind(uri)


    }

    override fun getItemCount(): Int = imageUris.size

    inner class ViewHolder(private val binding: GalaryimgBinding) :

        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(uri: String) {

            Glide.with(itemView)
                .load(uri)
                .centerCrop()
                .into(binding.galaryImg)


            binding.cancelButton.setOnClickListener {

                imageUris.removeAt(position)
                notifyItemRemoved(position)

                acBinding.getImgButton.text = "사진 올리기(" + imageUris.size.toString() + "/3)"

            }




        }

    }


}