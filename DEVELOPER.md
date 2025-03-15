# MemoRoute 开发者文档

## 技术架构

MemoRoute采用现代Android开发技术栈，基于MVVM架构模式设计。

### 核心技术

- **编程语言**: Kotlin
- **最低SDK版本**: API 23 (Android 6.0 Marshmallow)
- **目标SDK版本**: API 34 (Android 14)

### 架构组件

- **UI层**: 
  - Activity/Fragment
  - XML布局
  - Material Design组件
  - ViewBinding

- **表现层**:
  - ViewModel
  - LiveData
  - Navigation Component

- **数据层**:
  - Repository模式
  - Room数据库
  - Retrofit网络请求
  - DataStore偏好设置

### 主要依赖库

- **ArcGIS Runtime SDK**: 用于地图显示和地理数据处理
- **Glide**: 图片加载和缓存
- **Retrofit**: 网络请求
- **Room**: 本地数据库
- **Navigation Component**: 页面导航
- **Material Components**: UI组件
- **ViewPager2**: 轮播和滑动视图
- **RecyclerView**: 列表显示

## 开发环境设置

### 前提条件

- Android Studio Arctic Fox (2021.3.1)或更高版本
- JDK 11或更高版本
- Git

### 克隆项目

```bash
git clone https://github.com/yourusername/memoroutev2.git
cd memoroutev2
```

### 配置ArcGIS API密钥

1. 在[ArcGIS开发者门户](https://developers.arcgis.com/)注册并获取API密钥
2. 在项目根目录创建或编辑`local.properties`文件
3. 添加以下行：
```
arcgis.api.key=YOUR_API_KEY
```

### 构建项目

```bash
./gradlew assembleDebug
```

## 项目结构详解

### 包结构

```
com.example.memoroutev2/
├── data/                  # 数据层
│   ├── local/             # 本地数据源
│   │   ├── dao/           # 数据访问对象
│   │   ├── entity/        # 数据库实体
│   │   └── AppDatabase.kt # Room数据库
│   ├── remote/            # 远程数据源
│   │   ├── api/           # API接口
│   │   ├── dto/           # 数据传输对象
│   │   └── RetrofitClient.kt # 网络客户端
│   └── repository/        # 数据仓库
├── model/                 # 领域模型
├── ui/                    # UI层
│   ├── home/              # 首页相关
│   ├── map/               # 地图相关
│   ├── add/               # 添加旅行
│   ├── detail/            # 旅行详情
│   └── profile/           # 个人资料
└── utils/                 # 工具类
```

### 关键文件

- **MemoRouteApplication.kt**: 应用程序入口，初始化ArcGIS SDK和其他全局组件
- **MainActivity.kt**: 主活动，包含底部导航和Fragment容器
- **AppDatabase.kt**: Room数据库定义
- **Repository类**: 数据访问的统一接口
- **ViewModel类**: 处理UI相关的业务逻辑
- **Fragment类**: 实现各个页面的UI和交互

## 开发指南

### 代码风格

我们遵循[Kotlin官方编码规范](https://kotlinlang.org/docs/coding-conventions.html)，并使用ktlint进行代码风格检查。

主要规则：
- 使用4个空格缩进
- 类名使用PascalCase
- 函数和变量名使用camelCase
- 常量使用UPPER_SNAKE_CASE
- 文件名与顶级类名一致

### 架构原则

1. **关注点分离**: UI、业务逻辑和数据操作应明确分离
2. **单一职责**: 每个类应只有一个职责
3. **依赖注入**: 使用构造函数注入依赖
4. **可测试性**: 代码应易于单元测试

### 添加新功能

1. **创建Issue**: 在GitHub上创建Issue描述新功能
2. **分支开发**: 创建新分支进行开发 (`feature/feature-name`)
3. **编写测试**: 为新功能编写单元测试
4. **提交PR**: 完成开发后提交Pull Request
5. **代码审查**: 等待代码审查和合并

### 地图功能开发

MemoRoute使用ArcGIS Runtime SDK实现地图功能。以下是开发地图相关功能的基本步骤：

1. **初始化地图**:
```kotlin
// 创建地图对象
val map = ArcGISMap(BasemapStyle.ARCGIS_STREETS)
// 设置到MapView
mapView.map = map
```

2. **添加图形覆盖层**:
```kotlin
// 创建图形覆盖层
val graphicsOverlay = GraphicsOverlay()
// 添加到MapView
mapView.graphicsOverlays.add(graphicsOverlay)
```

3. **添加点标记**:
```kotlin
// 创建点
val point = Point(longitude, latitude, SpatialReferences.getWgs84())
// 创建标记符号
val symbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, color, size)
// 创建图形
val graphic = Graphic(point, symbol)
// 添加到覆盖层
graphicsOverlay.graphics.add(graphic)
```

4. **添加路线**:
```kotlin
// 创建点集合
val points = PointCollection(SpatialReferences.getWgs84())
// 添加点
pathPoints.forEach { points.add(it) }
// 创建线
val polyline = Polyline(points)
// 创建线符号
val lineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, color, width)
// 创建图形
val graphic = Graphic(polyline, lineSymbol)
// 添加到覆盖层
graphicsOverlay.graphics.add(graphic)
```

### 调试技巧

1. **日志记录**: 使用`Log`类记录关键信息和错误
```kotlin
Log.d(TAG, "详细信息")
Log.e(TAG, "错误信息", exception)
```

2. **异常处理**: 使用try-catch捕获和处理异常
```kotlin
try {
    // 可能抛出异常的代码
} catch (e: Exception) {
    Log.e(TAG, "操作失败: ${e.message}", e)
    // 处理异常
}
```

3. **性能分析**: 使用Android Profiler分析应用性能

## 测试指南

### 单元测试

使用JUnit和Mockito编写单元测试，主要测试ViewModel和Repository层。

```kotlin
@Test
fun `test trip repository get all trips`() {
    // 准备测试数据
    val trips = listOf(Trip(id = "1", title = "Test Trip"))
    whenever(tripDao.getAllTrips()).thenReturn(trips)
    
    // 执行测试
    val result = tripRepository.getAllTrips()
    
    // 验证结果
    assertEquals(trips, result)
}
```

### UI测试

使用Espresso进行UI测试。

```kotlin
@Test
fun clickAddTripButton_opensAddTripScreen() {
    // 启动主活动
    ActivityScenario.launch(MainActivity::class.java)
    
    // 点击添加按钮
    onView(withId(R.id.fab_add_trip)).perform(click())
    
    // 验证导航到添加旅行页面
    onView(withId(R.id.add_trip_title)).check(matches(isDisplayed()))
}
```

## 发布流程

1. **版本号更新**: 在`build.gradle`中更新版本号
2. **更新日志**: 更新`CHANGELOG.md`文件
3. **创建Release分支**: 创建`release/vX.Y.Z`分支
4. **测试验证**: 在Release分支上进行最终测试
5. **创建Tag**: 创建版本Tag `vX.Y.Z`
6. **构建APK**: 运行`./gradlew assembleRelease`
7. **签名APK**: 使用发布密钥签名APK
8. **创建GitHub Release**: 上传APK并发布Release
9. **合并到主分支**: 将Release分支合并回主分支

## 常见问题

### ArcGIS相关问题

**问题**: 地图无法加载或显示空白
**解决方案**: 
- 检查API密钥是否正确配置
- 确保设备有网络连接
- 检查是否授予了位置权限

### 构建问题

**问题**: 依赖冲突
**解决方案**:
- 使用`./gradlew app:dependencies`查看依赖树
- 使用`exclude`排除冲突的依赖
- 或使用`resolutionStrategy`强制使用特定版本

### 性能问题

**问题**: 地图操作卡顿
**解决方案**:
- 减少地图上的图形数量
- 使用适当的缩放级别
- 优化图形渲染（使用适当的符号大小和复杂度）

## 资源链接

- [ArcGIS Runtime SDK文档](https://developers.arcgis.com/android/)
- [Kotlin官方文档](https://kotlinlang.org/docs/home.html)
- [Android开发者指南](https://developer.android.com/guide)
- [Material Design指南](https://material.io/design)

---

如有任何问题或建议，请联系项目维护者或提交Issue。 