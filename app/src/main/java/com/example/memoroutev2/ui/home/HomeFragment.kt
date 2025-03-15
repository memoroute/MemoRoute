package com.example.memoroutev2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.memoroutev2.R
import com.example.memoroutev2.data.DataSource
import com.example.memoroutev2.databinding.FragmentHomeBinding
import com.example.memoroutev2.model.Destination
import com.example.memoroutev2.model.Trip
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var popularDestinationAdapter: PopularDestinationAdapter
    private lateinit var recentTripsAdapter: RecentTripsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        setupPopularDestinations()
        setupRecentTrips()
    }

    private fun setupPopularDestinations() {
        // 模拟数据
        val destinations = listOf(
            Destination(
                id = "1",
                name = "北京",
                description = "中国首都，拥有丰富的历史文化遗产和现代化都市风貌。",
                imageUrl = "",
                imageResource = R.drawable.beijing,
                latitude = 39.9042,
                longitude = 116.4074,
                popularity = 4.5f
            ),
            Destination(
                id = "2",
                name = "上海",
                description = "中国最大的经济中心，国际化大都市。",
                imageUrl = "",
                imageResource = R.drawable.shanghai,
                latitude = 31.2304,
                longitude = 121.4737,
                popularity = 4.3f
            ),
            Destination(
                id = "3",
                name = "杭州",
                description = "风景秀丽的城市，以西湖闻名于世。",
                imageUrl = "",
                imageResource = R.drawable.hangzhou,
                latitude = 30.2741,
                longitude = 120.1551,
                popularity = 4.7f
            ),
            Destination(
                id = "4",
                name = "成都",
                description = "四川省省会，以美食和大熊猫而闻名。",
                imageUrl = "",
                imageResource = R.drawable.chengdu,
                latitude = 30.5728,
                longitude = 104.0668,
                popularity = 4.4f
            ),
            Destination(
                id = "5",
                name = "三亚",
                description = "热带海滨城市，拥有美丽的海滩和热带风光。",
                imageUrl = "",
                imageResource = R.drawable.sanya,
                latitude = 18.2524,
                longitude = 109.5127,
                popularity = 4.6f
            )
        )

        // 设置适配器
        popularDestinationAdapter = PopularDestinationAdapter(
            destinations = destinations,
            onItemClick = { destination ->
                // 处理目的地点击
                Toast.makeText(requireContext(), "选择了 ${destination.name}", Toast.LENGTH_SHORT).show()
                // 导航到目的地详情页
                // findNavController().navigate(R.id.action_homeFragment_to_destinationDetailFragment)
            },
            onFavoriteClick = { destination ->
                // 处理收藏点击
                val message = if (destination.isFavorite) {
                    "已将 ${destination.name} 添加到收藏"
                } else {
                    "已将 ${destination.name} 从收藏中移除"
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        )

        // 设置ViewPager2
        binding.viewPagerPopularDestinations.apply {
            adapter = popularDestinationAdapter
            offscreenPageLimit = 3
            
            // 添加页面转换效果
            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(40))
            compositePageTransformer.addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
            setPageTransformer(compositePageTransformer)
        }

        // 设置指示器
        TabLayoutMediator(binding.tabLayoutIndicator, binding.viewPagerPopularDestinations) { _, _ -> 
            // 不需要设置文本
        }.attach()
    }

    private fun setupRecentTrips() {
        // 模拟数据
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val trips = listOf(
            Trip(
                id = "1",
                title = "北京三日游",
                location = "北京",
                description = "北京三日游行程",
                startDate = dateFormat.parse("2023-05-10") ?: Date(),
                endDate = dateFormat.parse("2023-05-12") ?: Date(),
                imageUrl = "",
                imageResource = R.drawable.beijing,
                pointsCount = 12,
                pathsCount = 3
            ),
            Trip(
                id = "2",
                title = "上海周末行",
                location = "上海",
                description = "上海周末行程",
                startDate = dateFormat.parse("2023-06-24") ?: Date(),
                endDate = dateFormat.parse("2023-06-25") ?: Date(),
                imageUrl = "",
                imageResource = R.drawable.shanghai,
                pointsCount = 8,
                pathsCount = 2
            ),
            Trip(
                id = "3",
                title = "杭州西湖一日游",
                location = "杭州",
                description = "杭州西湖一日游行程",
                startDate = dateFormat.parse("2023-07-15") ?: Date(),
                endDate = dateFormat.parse("2023-07-15") ?: Date(),
                imageUrl = "",
                imageResource = R.drawable.hangzhou,
                pointsCount = 5,
                pathsCount = 1
            )
        )

        // 设置适配器
        recentTripsAdapter = RecentTripsAdapter(
            trips = trips,
            onItemClick = { trip ->
                // 处理行程点击
                Toast.makeText(requireContext(), "选择了 ${trip.title}", Toast.LENGTH_SHORT).show()
                // 导航到行程详情页
                // findNavController().navigate(R.id.action_homeFragment_to_tripDetailFragment)
            }
        )

        // 设置RecyclerView
        binding.recyclerViewRecentTrips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentTripsAdapter
        }
    }

    private fun setupClickListeners() {
        // 搜索框点击
        binding.searchBox.setOnClickListener {
            // 导航到搜索页面
            // findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            Toast.makeText(requireContext(), "搜索功能即将上线", Toast.LENGTH_SHORT).show()
        }

        // 添加行程按钮点击
        binding.fabAddTrip.setOnClickListener {
            // 导航到添加行程页面
            // findNavController().navigate(R.id.action_homeFragment_to_addTripFragment)
            Toast.makeText(requireContext(), "添加行程功能即将上线", Toast.LENGTH_SHORT).show()
        }

        // 查看全部热门目的地
        binding.textViewAllDestinations.setOnClickListener {
            // 导航到全部目的地页面
            // findNavController().navigate(R.id.action_homeFragment_to_allDestinationsFragment)
            Toast.makeText(requireContext(), "查看全部目的地功能即将上线", Toast.LENGTH_SHORT).show()
        }

        // 查看全部最近行程
        binding.textViewAllTrips.setOnClickListener {
            // 导航到全部行程页面
            // findNavController().navigate(R.id.action_homeFragment_to_allTripsFragment)
            Toast.makeText(requireContext(), "查看全部行程功能即将上线", Toast.LENGTH_SHORT).show()
        }

        // 排序按钮点击
        binding.buttonSort.setOnClickListener {
            showSortOptionsDialog()
        }
    }

    private fun showSortOptionsDialog() {
        // 显示排序选项对话框
        Toast.makeText(requireContext(), "排序功能即将上线", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * 将dp转换为像素
 */
fun Int.dpToPx(context: android.content.Context?): Int {
    if (context == null) return this
    val density = context.resources.displayMetrics.density
    return (this * density).toInt()
} 