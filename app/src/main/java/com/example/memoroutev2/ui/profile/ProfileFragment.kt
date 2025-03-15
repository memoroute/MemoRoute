package com.example.memoroutev2.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.memoroutev2.R
import com.example.memoroutev2.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 设置用户信息
        setupUserInfo()

        // 设置编辑按钮
        binding.editProfileButton.setOnClickListener {
            Toast.makeText(requireContext(), "编辑个人资料", Toast.LENGTH_SHORT).show()
            // TODO: 跳转到编辑个人资料页面
        }

        // 设置设置按钮
        binding.settingsButton.setOnClickListener {
            Toast.makeText(requireContext(), "设置", Toast.LENGTH_SHORT).show()
            // TODO: 跳转到设置页面
        }

        // TODO: 设置旅行列表
    }

    private fun setupUserInfo() {
        // 模拟用户数据
        val userName = "我行故我在"
        val userBio = "热爱旅行的摄影师，善于记录旅途中的美好瞬间。已去过20个城市，计划环游世界。"
        val tripsCount = 12
        val photosCount = 87
        val placesCount = 20
        
        // 设置用户信息
        binding.userName.text = userName
        binding.userBio.text = userBio
        binding.tripsCount.text = tripsCount.toString()
        binding.photosCount.text = photosCount.toString()
        binding.placesCount.text = placesCount.toString()
        
        // 加载用户头像
        binding.profileImage.setImageResource(R.drawable.traveler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}