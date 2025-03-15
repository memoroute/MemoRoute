package com.example.memoroutev2.ui.detail

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.PointCollection
import com.esri.arcgisruntime.geometry.Polyline
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.example.memoroutev2.R
import com.example.memoroutev2.data.DataSource
import com.example.memoroutev2.databinding.FragmentTripDetailBinding
import com.example.memoroutev2.model.Trip
import com.example.memoroutev2.model.TripLocation
import com.example.memoroutev2.model.TripPath
import java.text.SimpleDateFormat
import java.util.Locale

class TripDetailFragment : Fragment() {

    private var _binding: FragmentTripDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var locationMapView: MapView
    private lateinit var pointsOverlay: GraphicsOverlay
    private lateinit var pathsOverlay: GraphicsOverlay
    private lateinit var animationOverlay: GraphicsOverlay
    private lateinit var locationOverlay: GraphicsOverlay
    private var tripId: String? = null
    private var currentTrip: Trip? = null
    private var isFavorite = false
    private var isPlayingAnimation = false
    private var animationValueAnimator: ValueAnimator? = null
    private val TAG = "TripDetailFragment"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

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
        _binding = FragmentTripDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            // 设置返回按钮
            binding.toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            // 初始化地图
            mapView = binding.mapView
            locationMapView = binding.locationMapView
            setupMap()
            setupLocationMap()

            // 加载旅行详情
            loadTripDetails()

            // 设置收藏按钮
            binding.favoriteButton.setOnClickListener {
                toggleFavorite()
            }

            // 设置评论按钮
            binding.commentButton.setOnClickListener {
                navigateToComments()
            }

            // 设置分享按钮
            binding.shareButton.setOnClickListener {
                shareTrip()
            }

            // 设置导航按钮
            binding.navigateButton.setOnClickListener {
                openNavigation()
            }
            
            // 设置路线回放按钮
            binding.playRouteButton.setOnClickListener {
                toggleRouteAnimation()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onViewCreated: ${e.message}")
            Toast.makeText(context, "初始化失败", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun setupMap() {
        try {
            // 创建ArcGIS地图对象，使用街道底图
            val map = ArcGISMap(BasemapStyle.ARCGIS_STREETS)
            
            // 设置地图到MapView
            mapView.map = map
            
            // 创建图形覆盖层
            pointsOverlay = GraphicsOverlay()
            pathsOverlay = GraphicsOverlay()
            animationOverlay = GraphicsOverlay()
            
            // 添加覆盖层到地图
            mapView.graphicsOverlays.add(pathsOverlay)  // 路径在下层
            mapView.graphicsOverlays.add(pointsOverlay) // 点在上层
            mapView.graphicsOverlays.add(animationOverlay) // 动画层在最上层
            
            // 设置初始位置和缩放级别（北京）
            // 创建一个Point对象表示北京的位置（经度，纬度，空间参考）
            val beijingPoint = Point(116.4074, 39.9042, SpatialReferences.getWgs84())
            mapView.setViewpoint(Viewpoint(beijingPoint, 50000.0))
            
            Log.d(TAG, "Map setup completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up map: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun setupLocationMap() {
        try {
            // 创建ArcGIS地图对象，使用街道底图
            val locationMap = ArcGISMap(BasemapStyle.ARCGIS_STREETS)
            
            // 设置地图到位置MapView
            locationMapView.map = locationMap
            
            // 创建图形覆盖层
            locationOverlay = GraphicsOverlay()
            
            // 添加覆盖层到地图
            locationMapView.graphicsOverlays.add(locationOverlay)
            
            // 设置初始位置和缩放级别（北京）
            val beijingPoint = Point(116.4074, 39.9042, SpatialReferences.getWgs84())
            locationMapView.setViewpoint(Viewpoint(beijingPoint, 50000.0))
            
            Log.d(TAG, "Location map setup completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up location map: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun loadTripDetails() {
        try {
            // 根据tripId获取旅行详情
            // 这里应该从服务器或本地数据库获取数据
            // 现在使用模拟数据
            val trips = DataSource.getRecentTrips()
            currentTrip = trips.find { it.id == tripId } ?: trips.firstOrNull()
            
            currentTrip?.let { trip ->
                // 设置标题和描述
                binding.tripTitle.text = trip.title
                binding.tripLocation.text = trip.location
                binding.tripDate.text = dateFormat.format(trip.startDate)
                binding.tripDescription.text = trip.description
                
                // 加载图片
                if (trip.imageResource != 0) {
                    // 优先使用本地资源
                    binding.tripImage.setImageResource(trip.imageResource)
                } else if (!trip.imageUrl.isNullOrEmpty()) {
                    // 如果没有本地资源，则使用网络图片
                    Glide.with(this)
                        .load(trip.imageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(binding.tripImage)
                } else {
                    // 如果都没有，则使用占位图
                    binding.tripImage.setImageResource(R.drawable.placeholder_image)
                }
                
                // 设置收藏状态
                isFavorite = trip.isFavorite
                updateFavoriteIcon()
                
                // 在地图上显示位置和路径
                displayTripOnMap(trip)
                
                // 在位置地图上显示位置
                displayLocationOnMap(trip)
                
                // 显示路线回放按钮（如果有路径）
                if (trip.paths.isNotEmpty() || trip.locationPoints.size > 1) {
                    binding.playRouteButton.visibility = View.VISIBLE
                } else {
                    binding.playRouteButton.visibility = View.GONE
                }
            } ?: run {
                Toast.makeText(context, "未找到旅行详情", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading trip details: ${e.message}")
            Toast.makeText(context, "加载旅行详情失败", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun displayTripOnMap(trip: Trip) {
        try {
            // 清除之前的图形
            pointsOverlay.graphics.clear()
            pathsOverlay.graphics.clear()
            
            val points = mutableListOf<Point>()
            
            // 添加位置点
            trip.locationPoints.forEach { location ->
                val point = Point(location.longitude, location.latitude, SpatialReferences.getWgs84())
                points.add(point)
                
                // 创建点图形
                val pointSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 12f)
                val pointGraphic = Graphic(point, pointSymbol)
                
                // 添加属性以便点击时识别
                pointGraphic.attributes["name"] = location.name
                pointGraphic.attributes["description"] = location.description
                
                // 添加到覆盖层
                pointsOverlay.graphics.add(pointGraphic)
            }
            
            // 添加路径
            trip.paths.forEach { path ->
                if (path.points.size > 1) {
                    val pointCollection = PointCollection(SpatialReferences.getWgs84())
                    path.points.forEach { location ->
                        pointCollection.add(Point(location.longitude, location.latitude, SpatialReferences.getWgs84()))
                    }
                    
                    val polyline = Polyline(pointCollection)
                    val lineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, path.color, path.width)
                    val pathGraphic = Graphic(polyline, lineSymbol)
                    
                    // 添加属性
                    pathGraphic.attributes["name"] = path.name
                    pathGraphic.attributes["description"] = path.description
                    
                    // 添加到覆盖层
                    pathsOverlay.graphics.add(pathGraphic)
                }
            }
            
            // 如果没有位置点和路径，但有经纬度，则添加一个点
            if (points.isEmpty() && trip.latitude != null && trip.longitude != null) {
                val point = Point(trip.longitude, trip.latitude, SpatialReferences.getWgs84())
                points.add(point)
                
                // 创建点图形
                val pointSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 12f)
                val pointGraphic = Graphic(point, pointSymbol)
                
                // 添加到覆盖层
                pointsOverlay.graphics.add(pointGraphic)
            }
            
            // 缩放到所有点的范围
            if (points.isNotEmpty()) {
                // 如果只有一个点，设置一个合适的缩放级别
                if (points.size == 1) {
                    mapView.setViewpoint(Viewpoint(points[0], 10000.0))
                } else {
                    // 获取所有点的范围
                    val extent = pointsOverlay.extent
                    
                    // 如果范围有效，缩放到该范围
                    if (extent != null && !extent.isEmpty) {
                        // 添加一些边距
                        mapView.setViewpointGeometryAsync(extent, 50.0)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying trip on map: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun displayLocationOnMap(trip: Trip) {
        try {
            // 清除之前的图形
            locationOverlay.graphics.clear()
            
            // 确定要显示的位置点
            var locationPoint: Point? = null
            
            // 优先使用第一个位置点
            if (trip.locationPoints.isNotEmpty()) {
                val firstLocation = trip.locationPoints.first()
                locationPoint = Point(firstLocation.longitude, firstLocation.latitude, SpatialReferences.getWgs84())
            } 
            // 如果没有位置点，但有经纬度，则使用经纬度
            else if (trip.latitude != null && trip.longitude != null) {
                locationPoint = Point(trip.longitude, trip.latitude, SpatialReferences.getWgs84())
            }
            
            // 如果有位置点，在地图上显示
            locationPoint?.let { point ->
                // 创建点图形
                val pointSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLUE, 15f)
                val pointGraphic = Graphic(point, pointSymbol)
                
                // 添加到覆盖层
                locationOverlay.graphics.add(pointGraphic)
                
                // 设置地图视点
                locationMapView.setViewpoint(Viewpoint(point, 10000.0))
                
                // 隐藏中心标记，因为我们已经添加了图形
                binding.locationMarker.visibility = View.GONE
            } ?: run {
                // 如果没有位置信息，显示提示
                Toast.makeText(context, "无法获取位置信息", Toast.LENGTH_SHORT).show()
                
                // 显示中心标记
                binding.locationMarker.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying location on map: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun toggleFavorite() {
        isFavorite = !isFavorite
        updateFavoriteIcon()
        
        // 这里应该将收藏状态更新到服务器或本地数据库
        Toast.makeText(context, if (isFavorite) "已添加到收藏" else "已取消收藏", Toast.LENGTH_SHORT).show()
    }

    private fun updateFavoriteIcon() {
        val favoriteIcon = binding.favoriteIcon
        if (isFavorite) {
            favoriteIcon.setImageResource(R.drawable.ic_favorite_filled)
            favoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_500))
        } else {
            favoriteIcon.setImageResource(R.drawable.ic_favorite_border)
            favoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_500))
        }
    }

    private fun navigateToComments() {
        // 导航到评论页面
        val bundle = Bundle().apply {
            putString("tripId", tripId)
        }
        findNavController().navigate(R.id.action_tripDetailFragment_to_commentsFragment, bundle)
    }

    private fun shareTrip() {
        // 分享旅行
        currentTrip?.let { trip ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, trip.title)
                putExtra(Intent.EXTRA_TEXT, "我在MemoRoute上记录了一次精彩的旅行：${trip.title}\n\n${trip.description}\n\n地点：${trip.location}\n日期：${dateFormat.format(trip.startDate)}")
            }
            startActivity(Intent.createChooser(shareIntent, "分享旅行"))
        }
    }

    private fun openNavigation() {
        // 打开导航
        currentTrip?.let { trip ->
            if (trip.latitude != null && trip.longitude != null) {
                // 这里应该打开地图应用进行导航
                // 由于不同设备上可能有不同的地图应用，这里只显示一个提示
                Toast.makeText(context, "正在导航到 ${trip.location}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "无法获取位置信息", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun toggleRouteAnimation() {
        if (isPlayingAnimation) {
            // 停止动画
            stopRouteAnimation()
        } else {
            // 开始动画
            startRouteAnimation()
        }
    }
    
    private fun startRouteAnimation() {
        try {
            currentTrip?.let { trip ->
                // 获取路径点
                val pathPoints = mutableListOf<Point>()
                
                // 如果有路径，使用路径点
                if (trip.paths.isNotEmpty()) {
                    trip.paths.forEach { path ->
                        path.points.forEach { location ->
                            pathPoints.add(Point(location.longitude, location.latitude, SpatialReferences.getWgs84()))
                        }
                    }
                } 
                // 如果没有路径但有位置点，使用位置点
                else if (trip.locationPoints.isNotEmpty()) {
                    trip.locationPoints.forEach { location ->
                        pathPoints.add(Point(location.longitude, location.latitude, SpatialReferences.getWgs84()))
                    }
                }
                
                // 如果没有足够的点，无法播放动画
                if (pathPoints.size < 2) {
                    Toast.makeText(context, "没有足够的路径点来播放动画", Toast.LENGTH_SHORT).show()
                    return
                }
                
                // 清除动画层
                animationOverlay.graphics.clear()
                
                // 创建动画标记
                val markerSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLUE, 15f)
                val animationGraphic = Graphic(pathPoints[0], markerSymbol)
                animationOverlay.graphics.add(animationGraphic)
                
                // 创建动画
                animationValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = pathPoints.size * 500L // 每个点0.5秒
                    interpolator = LinearInterpolator()
                    addUpdateListener { animator ->
                        val fraction = animator.animatedValue as Float
                        val index = (fraction * (pathPoints.size - 1)).toInt()
                        val nextIndex = (index + 1).coerceAtMost(pathPoints.size - 1)
                        val pointFraction = fraction * (pathPoints.size - 1) - index
                        
                        // 计算当前位置
                        val currentX = pathPoints[index].x + (pathPoints[nextIndex].x - pathPoints[index].x) * pointFraction
                        val currentY = pathPoints[index].y + (pathPoints[nextIndex].y - pathPoints[index].y) * pointFraction
                        val currentPoint = Point(currentX, currentY, SpatialReferences.getWgs84())
                        
                        // 更新动画标记位置
                        animationGraphic.geometry = currentPoint
                    }
                    
                    // 动画结束时
                    addListener(object : android.animation.Animator.AnimatorListener {
                        override fun onAnimationStart(animation: android.animation.Animator) {}
                        
                        override fun onAnimationEnd(animation: android.animation.Animator) {
                            activity?.runOnUiThread {
                                isPlayingAnimation = false
                                binding.playRouteButton.text = "播放路线"
                                binding.playRouteButton.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_play)
                            }
                        }
                        
                        override fun onAnimationCancel(animation: android.animation.Animator) {}
                        
                        override fun onAnimationRepeat(animation: android.animation.Animator) {}
                    })
                }
                
                // 开始动画
                animationValueAnimator?.start()
                isPlayingAnimation = true
                binding.playRouteButton.text = "停止播放"
                binding.playRouteButton.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_stop)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting route animation: ${e.message}")
            e.printStackTrace()
            Toast.makeText(context, "播放路线失败", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun stopRouteAnimation() {
        animationValueAnimator?.cancel()
        animationOverlay.graphics.clear()
        isPlayingAnimation = false
        binding.playRouteButton.text = "播放路线"
        binding.playRouteButton.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_play)
    }

    override fun onPause() {
        mapView.pause()
        locationMapView.pause()
        stopRouteAnimation()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
        locationMapView.resume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.dispose()
        locationMapView.dispose()
        _binding = null
    }
} 