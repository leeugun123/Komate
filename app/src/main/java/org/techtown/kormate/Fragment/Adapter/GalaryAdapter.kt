package org.techtown.kormate.Fragment.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.techtown.kormate.databinding.ActivityBoardPostBinding
import org.techtown.kormate.databinding.GalaryimgBinding

class GalaryAdapter(private val imageUris: MutableList<String>, private var acBinding : ActivityBoardPostBinding) :
    RecyclerView.Adapter<GalaryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalaryAdapter.ViewHolder {

        val binding = GalaryimgBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalaryAdapter.ViewHolder, position: Int) {

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

                acBinding.uploadImgButton.text = "사진 올리기(" + imageUris.size.toString() + "/3)"

            }




        }

    }


}