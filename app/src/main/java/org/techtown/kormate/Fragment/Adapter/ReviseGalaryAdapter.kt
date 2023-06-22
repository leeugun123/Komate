package org.techtown.kormate.Fragment.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.techtown.kormate.databinding.ActivityBoardPostBinding
import org.techtown.kormate.databinding.GalaryimgBinding

class ReviseGalaryAdapter(private val imageUris: MutableList<String>,
                          private val otherImageUris: MutableList<String>,
                          private var acBinding : ActivityBoardPostBinding) :
    RecyclerView.Adapter<ReviseGalaryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviseGalaryAdapter.ViewHolder {

        val binding = GalaryimgBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviseGalaryAdapter.ViewHolder, position: Int) {

        val uri = imageUris[position]
        holder.bind(uri)


    }

    override fun getItemCount(): Int = imageUris.size

    inner class ViewHolder(private val binding: GalaryimgBinding) :

        RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: String) {

            Glide.with(itemView)
                .load(uri)
                .centerCrop()
                .into(binding.galaryImg)


            binding.cancelButton.setOnClickListener {

                imageUris.removeAt(position)
                notifyItemRemoved(position)

                acBinding.uploadImgButton.text = "사진 올리기(" + (imageUris.size + otherImageUris.size).toString() + "/3)"

            }


        }

    }


}