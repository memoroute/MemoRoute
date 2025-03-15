package com.example.memoroutev2.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.memoroutev2.R
import com.example.memoroutev2.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 设置返回按钮
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // 设置搜索框
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 可以实现实时搜索
                return false
            }
        })

        // 设置筛选芯片
        setupFilterChips()

        // 模拟搜索结果
        binding.searchResultsCount.text = "找到3个结果"

        // TODO: 设置搜索结果列表
    }

    private fun setupFilterChips() {
        binding.filterLatest.setOnClickListener {
            updateChipSelection(it)
        }
        binding.filterPopular.setOnClickListener {
            updateChipSelection(it)
        }
        binding.filterHistory.setOnClickListener {
            updateChipSelection(it)
        }
        binding.filterHiking.setOnClickListener {
            updateChipSelection(it)
        }
    }

    private fun updateChipSelection(selectedView: View) {
        // 重置所有芯片的选中状态
        binding.filterLatest.isChecked = false
        binding.filterPopular.isChecked = false
        binding.filterHistory.isChecked = false
        binding.filterHiking.isChecked = false

        // 设置选中的芯片
        selectedView.isSelected = true
    }

    private fun performSearch(query: String?) {
        // TODO: 执行搜索操作
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 