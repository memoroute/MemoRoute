package com.example.memoroutev2.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.memoroutev2.R
import com.example.memoroutev2.model.Trip
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 最近旅行适配器，用于RecyclerView
 */
class RecentTripsAdapter(
    private val trips: List<Trip>,
    private val onItemClick: (Trip) -> Unit
) : RecyclerView.Adapter<RecentTripsAdapter.TripViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.bind(trip)
    }

    override fun getItemCount(): Int = trips.size

    inner class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.trip_image)
        private val titleTextView: TextView = itemView.findViewById(R.id.trip_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.trip_date)
        private val locationTextView: TextView = itemView.findViewById(R.id.trip_location)
        private val pointsCountTextView: TextView = itemView.findViewById(R.id.trip_points_count)
        private val pathsCountTextView: TextView = itemView.findViewById(R.id.trip_paths_count)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(trips[position])
                }
            }
        }

        fun bind(trip: Trip) {
            titleTextView.text = trip.title
            
            // 格式化日期范围
            val endDateStr = trip.endDate?.let { dateFormat.format(it) } ?: dateFormat.format(trip.startDate)
            val dateRange = "${dateFormat.format(trip.startDate)} - $endDateStr"
            dateTextView.text = dateRange
            
            // 设置位置信息
            locationTextView.text = trip.location
            
            // 设置足迹点和路线数量
            pointsCountTextView.text = "${trip.pointsCount}个足迹点"
            pathsCountTextView.text = "${trip.pathsCount}条路线"
            
            // 加载图片
            if (trip.imageResource != 0) {
                // 优先使用本地资源
                imageView.setImageResource(trip.imageResource)
            } else if (!trip.imageUrl.isNullOrEmpty()) {
                // 如果没有本地资源，则使用网络图片
                Glide.with(itemView.context)
                    .load(trip.imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .centerCrop()
                    .into(imageView)
            } else {
                // 如果都没有，则使用占位图
                imageView.setImageResource(R.drawable.placeholder_image)
            }
        }
    }
} 