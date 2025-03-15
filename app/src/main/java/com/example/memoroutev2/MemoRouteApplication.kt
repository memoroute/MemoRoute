package com.example.memoroutev2

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.ArcGISRuntimeException

class JiYiApplication : Application() {
    
    companion object {
        private const val TAG = "JiYiApplication"
        
        // 添加一个标志，表示ArcGIS是否初始化成功
        @Volatile
        var isArcGISInitialized = false
            private set
    }

    override fun onCreate() {
        super.onCreate()
        
        try {
            Log.d(TAG, "应用程序启动...")
            
            // 强制使用日间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            
            // 初始化ArcGIS
            initializeArcGIS()
            
            Log.d(TAG, "应用程序初始化完成")
        } catch (e: Exception) {
            Log.e(TAG, "应用程序初始化失败: ${e.message}", e)
        }
    }
    
    private fun initializeArcGIS() {
        try {
            Log.d(TAG, "开始初始化ArcGIS Runtime...")
            
            // 检查API密钥是否为空
            val apiKey = "AAPTxy8BH1VEsoebNVZXo8HurFDFqrS0QqC2vkbc8TJ7HeoDf2Zupw9p-ahsPFc12j_4KiaWzgBvXTG6_R67o0Pcdgb0-cizjCAnaul5c9mojI5tNP_y86W5-GNVvQxe_dQOJ93QEhw4ATTcVm9b6MqBrgjuaP6nwEieePaX4zGxRUAp-WAW_j5ip_aHX9nQO_Lsh_-H-1jMrv2oTyTWRukkXt2DN0QRhzy6K4vE_QrtMFU.AT1_kcyZL8RQ"
            if (apiKey.isBlank()) {
                Log.e(TAG, "ArcGIS API密钥为空")
                Toast.makeText(this, "ArcGIS API密钥为空，地图功能可能无法正常工作", Toast.LENGTH_LONG).show()
                return
            }
            
            // 初始化ArcGIS Runtime
            try {
                Log.d(TAG, "设置ArcGIS API密钥...")
                ArcGISRuntimeEnvironment.setApiKey(apiKey)
                Log.d(TAG, "ArcGIS API密钥设置成功")
            } catch (e: Exception) {
                Log.e(TAG, "设置ArcGIS API密钥失败: ${e.message}", e)
                Toast.makeText(this, "设置ArcGIS API密钥失败: ${e.message}", Toast.LENGTH_LONG).show()
                return
            }
            
            // 设置许可级别
            try {
                Log.d(TAG, "设置ArcGIS许可...")
                ArcGISRuntimeEnvironment.setLicense(apiKey)
                Log.d(TAG, "ArcGIS许可设置成功")
            } catch (e: ArcGISRuntimeException) {
                Log.e(TAG, "设置ArcGIS许可失败: ${e.message}", e)
                Toast.makeText(this, "ArcGIS API密钥无效，地图功能可能无法正常工作", Toast.LENGTH_LONG).show()
                e.printStackTrace()
                // 继续执行，尝试使用开发者模式
            }
            
            // 设置初始化成功标志
            isArcGISInitialized = true
            Log.d(TAG, "ArcGIS Runtime初始化成功")
        } catch (e: Exception) {
            isArcGISInitialized = false
            Log.e(TAG, "ArcGIS Runtime初始化失败: ${e.message}", e)
            Toast.makeText(this, "地图初始化失败: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
} 