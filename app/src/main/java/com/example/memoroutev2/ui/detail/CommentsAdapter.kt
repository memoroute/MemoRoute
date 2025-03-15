package com.example.memoroutev2.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.memoroutev2.R
import com.example.memoroutev2.model.Comment
import java.text.SimpleDateFormat
import java.util.Locale

class CommentsAdapter(
    private val comments: MutableList<Comment>,
    private val onLikeClickListener: (Comment, Int) -> Unit
) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment, position)
    }

    override fun getItemCount(): Int = comments.size

    fun updateComments(newComments: List<Comment>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }

    fun addComment(comment: Comment) {
        comments.add(0, comment)
        notifyItemInserted(0)
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userAvatar: ImageView = itemView.findViewById(R.id.user_avatar)
        private val userName: TextView = itemView.findViewById(R.id.user_name)
        private val commentDate: TextView = itemView.findViewById(R.id.comment_date)
        private val commentContent: TextView = itemView.findViewById(R.id.comment_content)
        private val likeButton: ImageView = itemView.findViewById(R.id.like_button)
        private val likeCount: TextView = itemView.findViewById(R.id.like_count)

        fun bind(comment: Comment, position: Int) {
            userName.text = comment.userName
            commentDate.text = dateFormat.format(comment.createdAt)
            commentContent.text = comment.content
            likeCount.text = comment.likes.toString()

            // 设置点赞状态
            if (comment.isLiked) {
                likeButton.setImageResource(R.drawable.ic_favorite_filled)
            } else {
                likeButton.setImageResource(R.drawable.ic_favorite_border)
            }

            // 加载用户头像
            if (comment.userAvatar != null) {
                Glide.with(itemView.context)
                    .load(comment.userAvatar)
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .circleCrop()
                    .into(userAvatar)
            } else {
                userAvatar.setImageResource(R.drawable.profile_placeholder)
            }

            // 设置点赞点击事件
            likeButton.setOnClickListener {
                onLikeClickListener(comment, position)
            }
        }
    }
} 