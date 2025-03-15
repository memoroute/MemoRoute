package com.example.memoroutev2.ui.add

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.example.memoroutev2.R
import com.example.memoroutev2.databinding.FragmentAddTripBinding
import com.example.memoroutev2.model.TripLocation
import com.example.memoroutev2.model.TripPath
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddTripFragment : Fragment() {

    private var _binding: FragmentAddTripBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    
    private lateinit var mapView: MapView
    private lateinit var pointsOverlay: GraphicsOverlay
    private lateinit var pathsOverlay: GraphicsOverlay
    
    private val TAG = "AddTripFragment"
    
    // 地图编辑模式
    private enum class MapEditMode {
        BROWSE,     // 浏览模式
        ADD_POINT,  // 添加点模式
        ADD_PATH    // 添加路径模式
    }
    
    private var currentMode = MapEditMode.BROWSE
    
    // 存储当前正在创建的路径点
    private val currentPathPoints = mutableListOf<Point>()
    
    // 存储所有创建的位置点和路径
    private val locationPoints = mutableListOf<TripLocation>()
    private val paths = mutableListOf<TripPath>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 设置返回按钮
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // 设置日期选择器
        binding.startDateEditText.setOnClickListener {
            showDatePicker(true)
        }

        binding.endDateEditText.setOnClickListener {
            showDatePicker(false)
        }
        
        // 初始化地图
        setupMap()
        
        // 设置地图编辑按钮
        binding.editMapButton.setOnClickListener {
            if (currentMode == MapEditMode.BROWSE) {
                // 切换到编辑模式
                Toast.makeText(requireContext(), "请选择编辑模式：添加点或添加路径", Toast.LENGTH_SHORT).show()
                binding.editMapButton.text = "完成编辑"
                binding.fabAddPoint.visibility = View.VISIBLE
                binding.fabAddLine.visibility = View.VISIBLE
                binding.fabClear.visibility = View.VISIBLE
            } else {
                // 切换回浏览模式
                setMapMode(MapEditMode.BROWSE)
                binding.editMapButton.text = "编辑地图"
                binding.fabAddPoint.visibility = View.GONE
                binding.fabAddLine.visibility = View.GONE
                binding.fabClear.visibility = View.GONE
            }
        }
        
        // 设置添加点按钮
        binding.fabAddPoint.setOnClickListener {
            setMapMode(MapEditMode.ADD_POINT)
        }
        
        // 设置添加路径按钮
        binding.fabAddLine.setOnClickListener {
            setMapMode(MapEditMode.ADD_PATH)
            Toast.makeText(requireContext(), "请在地图上点击添加路径点，双击结束", Toast.LENGTH_SHORT).show()
        }
        
        // 设置清除按钮
        binding.fabClear.setOnClickListener {
            clearCurrentEditing()
        }
        
        // 初始隐藏编辑按钮
        binding.fabAddPoint.visibility = View.GONE
        binding.fabAddLine.visibility = View.GONE
        binding.fabClear.visibility = View.GONE

        // 设置发布按钮
        binding.publishButton.setOnClickListener {
            publishTrip()
        }

        // 设置添加标签按钮
        binding.addTagChip.setOnClickListener {
            // TODO: 显示添加标签对话框
        }
        
        // 设置位置输入框点击事件
        binding.locationEditText.setOnClickListener {
            // 如果地图已经初始化，则显示地图卡片
            if (::mapView.isInitialized) {
                binding.mapCard.visibility = View.VISIBLE
            }
        }
    }
    
    private fun setupMap() {
        try {
            Log.d(TAG, "Setting up map...")
            
            // 初始化地图视图
            mapView = binding.addTripMapView
            
            // 创建ArcGIS地图对象，使用街道底图
            val map = ArcGISMap(BasemapStyle.ARCGIS_STREETS)
            
            // 设置地图到MapView
            mapView.map = map
            
            // 创建图形覆盖层
            pointsOverlay = GraphicsOverlay()
            pathsOverlay = GraphicsOverlay()
            
            // 添加覆盖层到地图
            mapView.graphicsOverlays.add(pathsOverlay)  // 路径在下层
            mapView.graphicsOverlays.add(pointsOverlay) // 点在上层
            
            // 设置初始位置和缩放级别（中国）
            val chinaPoint = Point(105.0, 35.0, SpatialReferences.getWgs84())
            mapView.setViewpoint(Viewpoint(chinaPoint, 10000000.0))
            
            // 设置地图点击监听器
            mapView.onTouchListener = object : DefaultMapViewOnTouchListener(requireContext(), mapView) {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    when (currentMode) {
                        MapEditMode.ADD_POINT -> {
                            addPointAtScreenLocation(e.x, e.y)
                            return true
                        }
                        MapEditMode.ADD_PATH -> {
                            addPathPointAtScreenLocation(e.x, e.y)
                            return true
                        }
                        else -> return super.onSingleTapConfirmed(e)
                    }
                }
                
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    if (currentMode == MapEditMode.ADD_PATH && currentPathPoints.size > 1) {
                        // 双击完成路径
                        finishCurrentPath()
                        return true
                    }
                    return super.onDoubleTap(e)
                }
            }
            
            // 加载地图
            map.loadAsync()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up map: ${e.message}")
            Toast.makeText(context, "地图设置失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    
    private fun setMapMode(mode: MapEditMode) {
        currentMode = mode
        
        // 更新UI显示当前模式
        binding.mapModeText.text = when (mode) {
            MapEditMode.BROWSE -> "浏览"
            MapEditMode.ADD_POINT -> "添加点"
            MapEditMode.ADD_PATH -> "添加路径"
        }
        
        // 如果切换模式，清除当前编辑
        if (mode != MapEditMode.ADD_PATH) {
            currentPathPoints.clear()
        }
    }
    
    private fun addPointAtScreenLocation(screenX: Float, screenY: Float) {
        try {
            // 将屏幕坐标转换为地图坐标
            val mapPoint = mapView.screenToLocation(android.graphics.Point(screenX.toInt(), screenY.toInt()))
            
            if (mapPoint != null) {
                // 创建点图形
                val pointSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 12f)
                val pointGraphic = Graphic(mapPoint, pointSymbol)
                
                // 添加到覆盖层
                pointsOverlay.graphics.add(pointGraphic)
                
                // 创建并保存位置点数据
                val tripLocation = TripLocation(
                    latitude = mapPoint.y,
                    longitude = mapPoint.x,
                    name = "位置点 ${locationPoints.size + 1}"
                )
                locationPoints.add(tripLocation)
                
                // 显示提示
                Toast.makeText(requireContext(), "已添加位置点: ${tripLocation.name}", Toast.LENGTH_SHORT).show()
                
                // 更新位置输入框
                if (binding.locationEditText.text.isNullOrEmpty()) {
                    binding.locationEditText.setText("位置点 ${locationPoints.size}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding point: ${e.message}")
            Toast.makeText(requireContext(), "添加点失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun addPathPointAtScreenLocation(screenX: Float, screenY: Float) {
        try {
            // 将屏幕坐标转换为地图坐标
            val mapPoint = mapView.screenToLocation(android.graphics.Point(screenX.toInt(), screenY.toInt()))
            
            if (mapPoint != null) {
                // 添加到当前路径点集合
                currentPathPoints.add(mapPoint)
                
                // 如果是第一个点，添加一个标记
                if (currentPathPoints.size == 1) {
                    val startSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, 12f)
                    val startGraphic = Graphic(mapPoint, startSymbol)
                    pointsOverlay.graphics.add(startGraphic)
                }
                
                // 如果有多个点，绘制临时路径线
                if (currentPathPoints.size > 1) {
                    // 清除之前的临时路径
                    if (pathsOverlay.graphics.size > paths.size) {
                        pathsOverlay.graphics.removeAt(pathsOverlay.graphics.size - 1)
                    }
                    
                    // 创建新的临时路径
                    val pointCollection = PointCollection(SpatialReferences.getWgs84())
                    currentPathPoints.forEach { pointCollection.add(it) }
                    
                    val polyline = Polyline(pointCollection)
                    val lineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3f)
                    val pathGraphic = Graphic(polyline, lineSymbol)
                    
                    // 添加到覆盖层
                    pathsOverlay.graphics.add(pathGraphic)
                }
                
                // 显示提示
                Toast.makeText(requireContext(), "已添加路径点 ${currentPathPoints.size}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding path point: ${e.message}")
            Toast.makeText(requireContext(), "添加路径点失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun finishCurrentPath() {
        if (currentPathPoints.size < 2) {
            Toast.makeText(requireContext(), "路径需要至少两个点", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            // 创建路径点集合
            val tripLocations = currentPathPoints.mapIndexed { index, point ->
                TripLocation(
                    latitude = point.y,
                    longitude = point.x,
                    type = TripLocation.LocationType.PATH_POINT,
                    order = index,
                    name = "路径点 ${index + 1}"
                )
            }
            
            // 创建并保存路径数据
            val tripPath = TripPath(
                name = "路径 ${paths.size + 1}",
                points = tripLocations
            )
            paths.add(tripPath)
            
            // 显示提示
            Toast.makeText(requireContext(), "已完成路径: ${tripPath.name}", Toast.LENGTH_SHORT).show()
            
            // 清除当前路径点
            currentPathPoints.clear()
            
            // 更新位置输入框
            if (binding.locationEditText.text.isNullOrEmpty()) {
                binding.locationEditText.setText("路径 ${paths.size}")
            }
            
            // 切换回浏览模式
            setMapMode(MapEditMode.BROWSE)
        } catch (e: Exception) {
            Log.e(TAG, "Error finishing path: ${e.message}")
            Toast.makeText(requireContext(), "完成路径失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun clearCurrentEditing() {
        when (currentMode) {
            MapEditMode.ADD_POINT -> {
                // 清除最后添加的点
                if (pointsOverlay.graphics.size > 0 && locationPoints.isNotEmpty()) {
                    pointsOverlay.graphics.removeAt(pointsOverlay.graphics.size - 1)
                    locationPoints.removeAt(locationPoints.size - 1)
                    Toast.makeText(requireContext(), "已清除最后添加的点", Toast.LENGTH_SHORT).show()
                }
            }
            MapEditMode.ADD_PATH -> {
                // 清除当前路径
                currentPathPoints.clear()
                
                // 移除临时路径图形
                if (pathsOverlay.graphics.size > paths.size) {
                    pathsOverlay.graphics.removeAt(pathsOverlay.graphics.size - 1)
                }
                
                // 移除起始点标记
                if (pointsOverlay.graphics.size > locationPoints.size) {
                    pointsOverlay.graphics.removeAt(pointsOverlay.graphics.size - 1)
                }
                
                Toast.makeText(requireContext(), "已清除当前路径", Toast.LENGTH_SHORT).show()
            }
            MapEditMode.BROWSE -> {
                // 清除所有点和路径
                pointsOverlay.graphics.clear()
                pathsOverlay.graphics.clear()
                locationPoints.clear()
                paths.clear()
                currentPathPoints.clear()
                Toast.makeText(requireContext(), "已清除所有点和路径", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun publishTrip() {
        // 验证必填字段
        val title = binding.tripTitleEditText.text.toString().trim()
        val location = binding.locationEditText.text.toString().trim()
        val startDateStr = binding.startDateEditText.text.toString().trim()
        
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "请输入旅行标题", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (location.isEmpty()) {
            Toast.makeText(requireContext(), "请输入位置", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (startDateStr.isEmpty()) {
            Toast.makeText(requireContext(), "请选择开始日期", Toast.LENGTH_SHORT).show()
            return
        }
        
        // TODO: 保存旅行数据到数据库或发送到服务器
        
        Toast.makeText(requireContext(), "旅行已发布", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val formattedDate = dateFormat.format(calendar.time)
                if (isStartDate) {
                    binding.startDateEditText.setText(formattedDate)
                } else {
                    binding.endDateEditText.setText(formattedDate)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    
    override fun onPause() {
        mapView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.dispose()
        _binding = null
    }
}