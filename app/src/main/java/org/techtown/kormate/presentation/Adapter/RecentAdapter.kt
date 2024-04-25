package org.techtown.kormate.presentation.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.techtown.kormate.Model.BoardDetail
import org.techtown.kormate.presentation.Fragment.HomeFragment
import org.techtown.kormate.databinding.RecentpreviewBinding


class RecentAdapter(private val boardList : List<BoardDetail> , private val homeFragment: HomeFragment) : RecyclerView.Adapter<RecentAdapter.ViewHolder>() {

    inner class ViewHolder(val binding : RecentpreviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(RecentpreviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun getItemCount() = boardList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        syncAdapterUi(boardList[position] , holder)
    }

    private fun syncAdapterUi(list : BoardDetail , holder : ViewHolder) {

        holder.binding.apply {

            dateTime.text = list.dateTime

            post.text = if(list.post.length > PREVIEW_POST_LIMIT - 1){
                list.post.substring(PREVIEW_POST_START, PREVIEW_POST_LIMIT - 1)+"..."
            } else
                list.post

            userName.text = list.userName

        }

        Glide.with(holder.itemView.context)
            .load(list.userImg)
            .override(PROFILE_WIDTH_SIZE, PROFILE_HEIGHT_SIZE)
            .circleCrop()
            .into(holder.binding.userImg)

        holder.itemView.setOnClickListener { homeFragment.onNavigateToActivity(list) }

    }


    companion object{
        private const val PROFILE_WIDTH_SIZE = 100
        private const val PROFILE_HEIGHT_SIZE = 100
        private const val PREVIEW_POST_START = 0
        private const val PREVIEW_POST_LIMIT = 53
    }



}