package com.example.memoroutev2.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.memoroutev2.R
import com.example.memoroutev2.model.Destination

/**
 * 热门目的地适配器，用于ViewPager2
 */
class PopularDestinationAdapter(
    private val destinations: List<Destination>,
    private val onItemClick: (Destination) -> Unit,
    private val onFavoriteClick: (Destination) -> Unit
) : RecyclerView.Adapter<PopularDestinationAdapter.DestinationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_popular_destination, parent, false)
        
        // 确保子视图使用match_parent作为宽度
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        view.layoutParams = layoutParams
        
        return DestinationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        val destination = destinations[position]
        holder.bind(destination)
    }

    override fun getItemCount(): Int = destinations.size

    inner class DestinationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.destination_image)
        private val nameTextView: TextView = itemView.findViewById(R.id.destination_name)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.destination_description)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.destination_rating)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.btn_favorite)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(destinations[position])
                }
            }

            favoriteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleFavorite(destinations[position])
                }
            }
        }

        fun bind(destination: Destination) {
            nameTextView.text = destination.name
            descriptionTextView.text = destination.description
            ratingBar.rating = destination.popularity

            // 设置收藏图标
            val favoriteIcon = if (destination.isFavorite) {
                R.drawable.ic_favorite
            } else {
                R.drawable.ic_favorite_border
            }
            favoriteButton.setImageResource(favoriteIcon)

            // 加载图片
            if (destination.imageResource != 0) {
                // 优先使用本地资源
                imageView.setImageResource(destination.imageResource)
            } else if (destination.imageUrl.isNotEmpty()) {
                // 如果没有本地资源，则使用网络图片
                Glide.with(itemView.context)
                    .load(destination.imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .centerCrop()
                    .into(imageView)
            } else {
                // 如果都没有，则使用占位图
                imageView.setImageResource(R.drawable.placeholder_image)
            }
        }

        private fun toggleFavorite(destination: Destination) {
            destination.isFavorite = !destination.isFavorite
            onFavoriteClick(destination)
            notifyItemChanged(adapterPosition)
        }
    }
} 