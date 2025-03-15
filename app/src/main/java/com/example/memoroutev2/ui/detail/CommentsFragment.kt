package com.example.memoroutev2.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memoroutev2.R
import com.example.memoroutev2.data.DataSource
import com.example.memoroutev2.databinding.FragmentCommentsBinding
import com.example.memoroutev2.model.Comment
import java.util.Date
import java.util.UUID

class CommentsFragment : Fragment() {

    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var commentsAdapter: CommentsAdapter
    private var tripId: String? = null
    private val comments = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tripId = it.getString("tripId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 设置返回按钮
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // 初始化RecyclerView
        setupRecyclerView()

        // 加载评论数据
        loadComments()

        // 设置发送评论按钮
        binding.sendButton.setOnClickListener {
            sendComment()
        }
    }

    private fun setupRecyclerView() {
        commentsAdapter = CommentsAdapter(comments) { comment, position ->
            toggleLike(comment, position)
        }
        binding.commentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentsAdapter
        }
    }

    private fun loadComments() {
        // 这里应该从服务器或本地数据库加载评论
        // 现在使用模拟数据
        val sampleComments = getSampleComments()
        
        if (sampleComments.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.commentsRecyclerView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.commentsRecyclerView.visibility = View.VISIBLE
            comments.clear()
            comments.addAll(sampleComments)
            commentsAdapter.notifyDataSetChanged()
        }
    }

    private fun getSampleComments(): List<Comment> {
        // 模拟评论数据
        return listOf(
            Comment(
                id = UUID.randomUUID().toString(),
                tripId = tripId ?: "",
                userId = "user1",
                userName = "旅行爱好者",
                content = "这个地方真的很美，值得一去！",
                createdAt = Date(System.currentTimeMillis() - 86400000), // 1天前
                likes = 12
            ),
            Comment(
                id = UUID.randomUUID().toString(),
                tripId = tripId ?: "",
                userId = "user2",
                userName = "摄影师小王",
                content = "风景如画，拍照效果非常好，强烈推荐！",
                createdAt = Date(System.currentTimeMillis() - 172800000), // 2天前
                likes = 8
            ),
            Comment(
                id = UUID.randomUUID().toString(),
                tripId = tripId ?: "",
                userId = "user3",
                userName = "背包客",
                content = "交通便利，但是人有点多，建议早点去避开人流。",
                createdAt = Date(System.currentTimeMillis() - 259200000), // 3天前
                likes = 5
            )
        )
    }

    private fun sendComment() {
        val commentText = binding.commentInput.text.toString().trim()
        if (commentText.isEmpty()) {
            Toast.makeText(context, "评论内容不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        // 创建新评论
        val newComment = Comment(
            id = UUID.randomUUID().toString(),
            tripId = tripId ?: "",
            userId = "currentUser", // 应该是当前登录用户的ID
            userName = "我", // 应该是当前登录用户的名称
            content = commentText,
            createdAt = Date()
        )

        // 添加到列表并刷新UI
        if (comments.isEmpty()) {
            binding.emptyView.visibility = View.GONE
            binding.commentsRecyclerView.visibility = View.VISIBLE
        }
        
        commentsAdapter.addComment(newComment)
        binding.commentsRecyclerView.scrollToPosition(0)
        
        // 清空输入框
        binding.commentInput.text.clear()
        
        // 这里应该将评论保存到服务器或本地数据库
        Toast.makeText(context, "评论已发布", Toast.LENGTH_SHORT).show()
    }

    private fun toggleLike(comment: Comment, position: Int) {
        // 切换点赞状态
        val updatedComment = comment.copy(
            isLiked = !comment.isLiked,
            likes = if (comment.isLiked) comment.likes - 1 else comment.likes + 1
        )
        
        // 更新列表
        comments[position] = updatedComment
        commentsAdapter.notifyItemChanged(position)
        
        // 这里应该将点赞状态更新到服务器或本地数据库
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 