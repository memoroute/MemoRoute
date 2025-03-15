package com.example.memoroutev2.ui.map

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
import com.example.memoroutev2.databinding.FragmentMapBinding
import com.example.memoroutev2.model.Trip
import com.example.memoroutev2.model.TripLocation
import com.example.memoroutev2.model.TripPath
import com.google.android.material.tabs.TabLayout
import com.bumptech.glide.Glide

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var pointsOverlay: GraphicsOverlay
    private lateinit var pathsOverlay: GraphicsOverlay
    private val TAG = "MapFragment"
    
    // 当前显示的旅行
    private var currentTrip: Trip? = null
    
    // 显示模式：我的足迹 或 所有足迹
    private enum class DisplayMode {
        MY_FOOTPRINTS,
        ALL_FOOTPRINTS
    }
    
    private var currentDisplayMode = DisplayMode.MY_FOOTPRINTS
    private var mapInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            Log.d(TAG, "onCreateView: 开始创建视图")
            _binding = FragmentMapBinding.inflate(inflater, container, false)
            return binding.root
        } catch (e: Exception) {
            Log.e(TAG, "onCreateView 失败: ${e.message}", e)
            // 如果视图绑定失败，返回一个空视图
            return View(context)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            Log.d(TAG, "开始初始化MapFragment视图...")
            
            // 检查binding是否已初始化
            if (_binding == null) {
                Log.e(TAG, "Binding未初始化")
                Toast.makeText(context, "视图绑定失败", Toast.LENGTH_SHORT).show()
                return
            }
            
            // 初始化地图
            Log.d(TAG, "初始化地图视图...")
            mapView = binding.mapView
            
            // 延迟初始化地图，避免在UI线程上进行耗时操作
            view.post {
                try {
                    setupMap()
                } catch (e: Exception) {
                    Log.e(TAG, "地图初始化失败: ${e.message}", e)
                    Toast.makeText(context, "地图初始化失败: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            // 设置地图类型切换
            Log.d(TAG, "设置地图类型切换按钮...")
            binding.mapTypeChip.setOnClickListener {
                try {
                    changeMapType()
                } catch (e: Exception) {
                    Log.e(TAG, "切换地图类型失败: ${e.message}", e)
                    Toast.makeText(context, "切换地图类型失败", Toast.LENGTH_SHORT).show()
                }
            }

            // 设置缩放按钮
            Log.d(TAG, "设置缩放按钮...")
            binding.fabZoomIn.setOnClickListener {
                try {
                    zoomIn()
                } catch (e: Exception) {
                    Log.e(TAG, "放大地图失败: ${e.message}", e)
                    Toast.makeText(context, "无法放大地图", Toast.LENGTH_SHORT).show()
                }
            }

            binding.fabZoomOut.setOnClickListener {
                try {
                    zoomOut()
                } catch (e: Exception) {
                    Log.e(TAG, "缩小地图失败: ${e.message}", e)
                    Toast.makeText(context, "无法缩小地图", Toast.LENGTH_SHORT).show()
                }
            }

            // 设置旅行详情卡片点击事件
            Log.d(TAG, "设置旅行详情卡片点击事件...")
            binding.viewDetailsButton.setOnClickListener {
                try {
                    // 这里应该传递旅行ID
                    val bundle = Bundle().apply {
                        putString("tripId", currentTrip?.id ?: "1")
                    }
                    findNavController().navigate(R.id.navigation_trip_detail, bundle)
                } catch (e: Exception) {
                    Log.e(TAG, "导航到旅行详情失败: ${e.message}", e)
                    Toast.makeText(context, "无法查看详情", Toast.LENGTH_SHORT).show()
                }
            }
            
            // 设置示例旅行信息
            Log.d(TAG, "设置示例旅行信息...")
            binding.tripTitle.text = "北京长城一日游"
            binding.tripLocation.text = "北京 · 2023-09-20"
            
            // 设置标签页切换监听
            Log.d(TAG, "设置标签页切换监听...")
            binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    try {
                        when (tab?.position) {
                            0 -> {
                                // 我的足迹
                                currentDisplayMode = DisplayMode.MY_FOOTPRINTS
                                if (mapInitialized) {
                                    loadMyFootprints()
                                }
                            }
                            1 -> {
                                // 所有足迹
                                currentDisplayMode = DisplayMode.ALL_FOOTPRINTS
                                if (mapInitialized) {
                                    loadAllFootprints()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "标签切换失败: ${e.message}", e)
                        Toast.makeText(context, "加载足迹失败", Toast.LENGTH_SHORT).show()
                    }
                }
                
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
            
            // 默认不在这里加载足迹，等地图初始化完成后再加载
            
            Log.d(TAG, "MapFragment视图初始化完成")
        } catch (e: Exception) {
            Log.e(TAG, "MapFragment视图初始化失败: ${e.message}", e)
            Toast.makeText(context, "地图初始化失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun setupMap() {
        try {
            Log.d(TAG, "开始设置地图...")
            
            // 检查MapView是否已初始化
            if (!::mapView.isInitialized) {
                Log.e(TAG, "MapView未初始化")
                Toast.makeText(context, "地图视图未初始化", Toast.LENGTH_SHORT).show()
                return
            }
            
            // 检查ArcGIS是否已初始化
            if (!com.example.memoroutev2.JiYiApplication.Companion.isArcGISInitialized) {
                Log.e(TAG, "ArcGIS未初始化，无法加载地图")
                Toast.makeText(context, "地图服务未初始化，请重启应用", Toast.LENGTH_SHORT).show()
                return
            }
            
            // 创建ArcGIS地图对象，使用街道底图
            Log.d(TAG, "创建ArcGIS地图对象...")
            val map = ArcGISMap(BasemapStyle.ARCGIS_STREETS)
            
            // 设置地图到MapView
            Log.d(TAG, "设置地图到MapView...")
            mapView.map = map
            
            // 创建图形覆盖层
            Log.d(TAG, "创建图形覆盖层...")
            pointsOverlay = GraphicsOverlay()
            pathsOverlay = GraphicsOverlay()
            
            // 添加覆盖层到地图
            Log.d(TAG, "添加覆盖层到地图...")
            mapView.graphicsOverlays.add(pathsOverlay)  // 路径在下层
            mapView.graphicsOverlays.add(pointsOverlay) // 点在上层
            
            // 设置初始位置和缩放级别（中国）
            Log.d(TAG, "设置初始位置和缩放级别...")
            try {
                // 创建一个Point对象表示中国的位置（经度，纬度，空间参考）
                val chinaPoint = Point(105.0, 35.0, SpatialReferences.getWgs84())
                mapView.setViewpoint(Viewpoint(chinaPoint, 10000000.0))
            } catch (e: Exception) {
                Log.e(TAG, "设置初始位置失败: ${e.message}", e)
                e.printStackTrace()
            }
            
            // 添加地图加载完成的监听器
            Log.d(TAG, "添加地图加载完成的监听器...")
            map.addDoneLoadingListener {
                try {
                    if (map.loadStatus == com.esri.arcgisruntime.loadable.LoadStatus.LOADED) {
                        Log.d(TAG, "地图加载成功")
                        mapInitialized = true
                        activity?.runOnUiThread {
                            try {
                                Toast.makeText(context, "地图加载成功", Toast.LENGTH_SHORT).show()
                                // 地图加载成功后，加载足迹数据
                                if (currentDisplayMode == DisplayMode.MY_FOOTPRINTS) {
                                    loadMyFootprints()
                                } else {
                                    loadAllFootprints()
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "地图加载成功后处理失败: ${e.message}", e)
                            }
                        }
                    } else {
                        Log.e(TAG, "地图加载失败: ${map.loadError?.message}")
                        mapInitialized = false
                        activity?.runOnUiThread {
                            Toast.makeText(context, "地图加载失败: ${map.loadError?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "地图加载监听器处理失败: ${e.message}", e)
                }
            }
            
            // 加载地图
            Log.d(TAG, "开始异步加载地图...")
            map.loadAsync()
            
            Log.d(TAG, "地图设置完成")
        } catch (e: Exception) {
            Log.e(TAG, "地图设置失败: ${e.message}", e)
            Toast.makeText(context, "地图设置失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    
    private fun loadMyFootprints() {
        if (!mapInitialized || !::pointsOverlay.isInitialized || !::pathsOverlay.isInitialized) {
            Log.e(TAG, "地图未初始化，无法加载足迹")
            return
        }
        
        // 清除之前的图形
        pointsOverlay.graphics.clear()
        pathsOverlay.graphics.clear()
        
        try {
            // 获取我的旅行数据
            val myTrips = DataSource.getRecentTrips()
            
            // 如果没有旅行数据，显示提示
            if (myTrips.isEmpty()) {
                Toast.makeText(context, "暂无足迹数据", Toast.LENGTH_SHORT).show()
                return
            }
            
            // 显示第一个旅行的详情
            currentTrip = myTrips.first()
            updateTripInfoCard(currentTrip)
            
            // 创建示例足迹点和路径
            createSampleFootprints(myTrips)
            
            // 缩放到足迹范围
            zoomToFootprints()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading my footprints: ${e.message}", e)
            Toast.makeText(context, "加载足迹失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadAllFootprints() {
        if (!mapInitialized || !::pointsOverlay.isInitialized || !::pathsOverlay.isInitialized) {
            Log.e(TAG, "地图未初始化，无法加载足迹")
            return
        }
        
        // 清除之前的图形
        pointsOverlay.graphics.clear()
        pathsOverlay.graphics.clear()
        
        try {
            // 获取所有旅行数据
            val allTrips = DataSource.getHotDestinations().map { destination ->
                // 将目的地转换为旅行数据
                Trip(
                    id = destination.id,
                    title = destination.name,
                    location = destination.name,
                    description = destination.description,
                    startDate = java.util.Date(),
                    latitude = destination.latitude,
                    longitude = destination.longitude,
                    imageUrl = destination.imageUrl,
                    imageResource = destination.imageResource
                )
            }
            
            // 如果没有旅行数据，显示提示
            if (allTrips.isEmpty()) {
                Toast.makeText(context, "暂无足迹数据", Toast.LENGTH_SHORT).show()
                return
            }
            
            // 显示第一个旅行的详情
            currentTrip = allTrips.first()
            updateTripInfoCard(currentTrip)
            
            // 创建示例足迹点
            createSampleFootprints(allTrips)
            
            // 缩放到足迹范围
            zoomToFootprints()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading all footprints: ${e.message}", e)
            Toast.makeText(context, "加载足迹失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun createSampleFootprints(trips: List<Trip>) {
        // 为每个旅行创建足迹点
        trips.forEachIndexed { index, trip ->
            // 如果有经纬度，添加位置点
            if (trip.latitude != null && trip.longitude != null) {
                val point = Point(trip.longitude, trip.latitude, SpatialReferences.getWgs84())
                
                // 创建点图形
                val pointSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 12f)
                val pointGraphic = Graphic(point, pointSymbol)
                
                // 添加属性以便点击时识别
                pointGraphic.attributes["tripId"] = trip.id
                pointGraphic.attributes["title"] = trip.title
                
                // 添加到覆盖层
                pointsOverlay.graphics.add(pointGraphic)
            }
            
            // 如果是我的足迹模式，添加一些示例路径
            if (currentDisplayMode == DisplayMode.MY_FOOTPRINTS && index < 3) {
                createSamplePath(trip, index)
            }
        }
    }
    
    private fun createSamplePath(trip: Trip, index: Int) {
        // 创建示例路径点
        val pathPoints = mutableListOf<Point>()
        
        // 如果有经纬度，以该点为中心创建路径
        if (trip.latitude != null && trip.longitude != null) {
            // 创建一个围绕中心点的简单路径
            val centerLat = trip.latitude
            val centerLon = trip.longitude
            
            // 根据索引创建不同形状的路径
            when (index % 3) {
                0 -> {
                    // 矩形路径
                    val offset = 0.02
                    pathPoints.add(Point(centerLon - offset, centerLat - offset, SpatialReferences.getWgs84()))
                    pathPoints.add(Point(centerLon + offset, centerLat - offset, SpatialReferences.getWgs84()))
                    pathPoints.add(Point(centerLon + offset, centerLat + offset, SpatialReferences.getWgs84()))
                    pathPoints.add(Point(centerLon - offset, centerLat + offset, SpatialReferences.getWgs84()))
                    pathPoints.add(Point(centerLon - offset, centerLat - offset, SpatialReferences.getWgs84()))
                }
                1 -> {
                    // 三角形路径
                    val offset = 0.03
                    pathPoints.add(Point(centerLon, centerLat + offset, SpatialReferences.getWgs84()))
                    pathPoints.add(Point(centerLon - offset, centerLat - offset, SpatialReferences.getWgs84()))
                    pathPoints.add(Point(centerLon + offset, centerLat - offset, SpatialReferences.getWgs84()))
                    pathPoints.add(Point(centerLon, centerLat + offset, SpatialReferences.getWgs84()))
                }
                2 -> {
                    // 之字形路径
                    val offset = 0.01
                    for (i in 0..5) {
                        val lon = centerLon + (if (i % 2 == 0) offset else -offset)
                        val lat = centerLat + (i * offset)
                        pathPoints.add(Point(lon, lat, SpatialReferences.getWgs84()))
                    }
                }
            }
            
            // 创建路径线
            if (pathPoints.size > 1) {
                val pointCollection = PointCollection(SpatialReferences.getWgs84())
                pathPoints.forEach { pointCollection.add(it) }
                
                val polyline = Polyline(pointCollection)
                
                // 根据索引使用不同颜色
                val color = when (index % 3) {
                    0 -> Color.BLUE
                    1 -> Color.GREEN
                    else -> Color.MAGENTA
                }
                
                val lineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, color, 3f)
                val pathGraphic = Graphic(polyline, lineSymbol)
                
                // 添加属性以便点击时识别
                pathGraphic.attributes["tripId"] = trip.id
                pathGraphic.attributes["title"] = trip.title
                
                // 添加到覆盖层
                pathsOverlay.graphics.add(pathGraphic)
            }
        }
    }
    
    private fun updateTripInfoCard(trip: Trip?) {
        if (trip != null) {
            binding.tripTitle.text = trip.title
            binding.tripLocation.text = "${trip.location} · ${formatDate(trip.startDate)}"
            
            // 加载图片
            if (trip.imageResource != 0) {
                // 优先使用本地资源
                binding.tripImage.setImageResource(trip.imageResource)
            } else if (!trip.imageUrl.isNullOrEmpty()) {
                // 如果没有本地资源，则使用网络图片
                Glide.with(requireContext())
                    .load(trip.imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(binding.tripImage)
            } else {
                // 如果都没有，则使用占位图
                binding.tripImage.setImageResource(R.drawable.placeholder_image)
            }
            
            // 显示卡片
            binding.tripInfoCard.visibility = View.VISIBLE
        } else {
            // 隐藏卡片
            binding.tripInfoCard.visibility = View.GONE
        }
    }
    
    private fun formatDate(date: java.util.Date): String {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return dateFormat.format(date)
    }
    
    private fun zoomToFootprints() {
        // 如果有足迹点，缩放到足迹范围
        if (pointsOverlay.graphics.size > 0) {
            try {
                // 获取所有点的范围
                val extent = pointsOverlay.extent
                
                // 如果范围有效，缩放到该范围
                if (extent != null && !extent.isEmpty) {
                    // 添加一些边距
                    mapView.setViewpointGeometryAsync(extent, 50.0)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error zooming to footprints: ${e.message}", e)
            }
        }
    }
    
    private fun changeMapType() {
        try {
            // 根据当前地图类型切换到不同的底图
            val newBasemapStyle = when (binding.mapTypeChip.text.toString()) {
                "街道图" -> {
                    binding.mapTypeChip.text = "卫星图"
                    BasemapStyle.ARCGIS_IMAGERY
                }
                "卫星图" -> {
                    binding.mapTypeChip.text = "地形图"
                    BasemapStyle.ARCGIS_TOPOGRAPHIC
                }
                else -> {
                    binding.mapTypeChip.text = "街道图"
                    BasemapStyle.ARCGIS_STREETS
                }
            }
            
            // 创建新地图并保持当前视角
            val currentViewpoint = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE)
            val newMap = ArcGISMap(newBasemapStyle)
            mapView.map = newMap
            
            // 加载完成后恢复视角
            newMap.addDoneLoadingListener {
                try {
                    if (currentViewpoint != null) {
                        mapView.setViewpoint(currentViewpoint)
                    }
                    
                    // 重新加载足迹
                    if (currentDisplayMode == DisplayMode.MY_FOOTPRINTS) {
                        loadMyFootprints()
                    } else {
                        loadAllFootprints()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "地图类型切换后处理失败: ${e.message}", e)
                }
            }
            
            // 加载地图
            newMap.loadAsync()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error changing map type: ${e.message}", e)
            Toast.makeText(context, "切换地图类型失败", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun zoomIn() {
        try {
            // 放大地图 - 缩小比例尺
            val newScale = mapView.mapScale * 0.5
            val currentCenter = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).targetGeometry as Point
            mapView.setViewpoint(Viewpoint(currentCenter, newScale))
        } catch (e: Exception) {
            Log.e(TAG, "Error zooming in: ${e.message}", e)
            Toast.makeText(context, "无法放大地图", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun zoomOut() {
        try {
            // 缩小地图 - 增大比例尺
            val newScale = mapView.mapScale * 2.0
            val currentCenter = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).targetGeometry as Point
            mapView.setViewpoint(Viewpoint(currentCenter, newScale))
        } catch (e: Exception) {
            Log.e(TAG, "Error zooming out: ${e.message}", e)
            Toast.makeText(context, "无法缩小地图", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        try {
            Log.d(TAG, "MapFragment onPause")
            if (::mapView.isInitialized) {
                Log.d(TAG, "暂停地图视图")
                mapView.pause()
            }
        } catch (e: Exception) {
            Log.e(TAG, "暂停地图视图失败: ${e.message}", e)
            e.printStackTrace()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        try {
            Log.d(TAG, "MapFragment onResume")
            if (::mapView.isInitialized) {
                Log.d(TAG, "恢复地图视图")
                mapView.resume()
            }
        } catch (e: Exception) {
            Log.e(TAG, "恢复地图视图失败: ${e.message}", e)
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        try {
            Log.d(TAG, "MapFragment onDestroyView")
            if (::mapView.isInitialized) {
                Log.d(TAG, "释放地图视图资源")
                mapView.dispose()
            }
        } catch (e: Exception) {
            Log.e(TAG, "释放地图视图资源失败: ${e.message}", e)
            e.printStackTrace()
        }
        super.onDestroyView()
        _binding = null
    }
} 