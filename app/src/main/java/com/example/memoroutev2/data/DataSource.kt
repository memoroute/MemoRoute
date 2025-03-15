package com.example.memoroutev2.data

import com.example.memoroutev2.model.Destination
import com.example.memoroutev2.model.Trip
import com.example.memoroutev2.model.TripLocation
import com.example.memoroutev2.model.TripPath
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * 数据源类，提供应用程序所需的数据
 */
object DataSource {
    
    // 格式化日期
    private fun parseDate(dateString: String): Date {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString) ?: Date()
    }
    
    /**
     * 获取热门目的地数据
     */
    fun getHotDestinations(): List<Destination> {
        return listOf(
            Destination(
                id = "1",
                name = "北京故宫",
                description = "故宫是中国明清两代的皇家宫殿，旧称紫禁城，位于北京中轴线的中心，是中国古代宫廷建筑之精华。",
                imageUrl = "",
                imageResource = com.example.memoroutev2.R.drawable.beijing,
                latitude = 39.916345,
                longitude = 116.397155,
                popularity = 5.0f
            ),
            Destination(
                id = "2",
                name = "上海外滩",
                description = "外滩是上海市中心的一个区域，位于黄浦区的黄浦江畔，即外黄浦滩，是上海开埠后的产物，是旧上海十里洋场的真实写照。",
                imageUrl = "",
                imageResource = com.example.memoroutev2.R.drawable.shanghai,
                latitude = 31.233815,
                longitude = 121.490607,
                popularity = 4.0f
            ),
            Destination(
                id = "3",
                name = "杭州西湖",
                description = "西湖，位于浙江省杭州市西湖区龙井路1号，杭州市区西部，景区总面积49平方千米，汇水面积为21.22平方千米，湖面面积为6.38平方千米。",
                imageUrl = "",
                imageResource = com.example.memoroutev2.R.drawable.hangzhou,
                latitude = 30.242865,
                longitude = 120.148688,
                popularity = 5.0f
            ),
            Destination(
                id = "4",
                name = "广州塔",
                description = "广州塔（英语：Canton Tower），昵称小蛮腰，位于中国广东省广州市海珠区（艾洲岛）赤岗塔附近，距离珠江南岸125米，与珠江新城隔江相望。",
                imageUrl = "",
                imageResource = com.example.memoroutev2.R.drawable.chengdu,
                latitude = 23.105833,
                longitude = 113.322513,
                popularity = 4.0f
            ),
            Destination(
                id = "5",
                name = "西安兵马俑",
                description = "兵马俑，即秦始皇兵马俑，亦简称秦兵马俑或秦俑，第一批全国重点文物保护单位，第一批中国世界遗产，位于今陕西省西安市临潼区秦始皇陵以东1.5千米处的兵马俑坑内。",
                imageUrl = "",
                imageResource = com.example.memoroutev2.R.drawable.sanya,
                latitude = 34.384431,
                longitude = 109.278209,
                popularity = 5.0f
            )
        )
    }
    
    /**
     * 获取最近旅行数据
     */
    fun getRecentTrips(): List<Trip> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        return listOf(
            Trip(
                id = "1",
                title = "北京长城一日游",
                location = "北京",
                description = "今天去了八达岭长城，人真多啊，但是风景很壮观！登上长城，真是不虚此行。",
                startDate = dateFormat.parse("2023-09-20") ?: Date(),
                endDate = dateFormat.parse("2023-09-20"),
                imageUrl = "",
                imageResource = com.example.memoroutev2.R.drawable.beijing,
                latitude = 40.359889,
                longitude = 116.019546,
                locationPoints = getSampleLocationPoints("1"),
                paths = getSamplePaths("1")
            ),
            Trip(
                id = "2",
                title = "上海外滩夜景",
                location = "上海",
                description = "晚上去外滩看夜景，真的太美了！灯光璀璨，江风习习，感受到了上海的国际化魅力。",
                startDate = dateFormat.parse("2023-08-15") ?: Date(),
                endDate = dateFormat.parse("2023-08-15"),
                imageUrl = "",
                imageResource = com.example.memoroutev2.R.drawable.shanghai,
                latitude = 31.233815,
                longitude = 121.490607,
                locationPoints = getSampleLocationPoints("2"),
                paths = getSamplePaths("2")
            ),
            Trip(
                id = "3",
                title = "杭州西湖游船",
                location = "杭州",
                description = "坐船游西湖，欣赏了三潭印月、断桥残雪等景点，湖水清澈，景色宜人，真是人间天堂！",
                startDate = dateFormat.parse("2023-07-10") ?: Date(),
                endDate = dateFormat.parse("2023-07-12"),
                imageUrl = "",
                imageResource = com.example.memoroutev2.R.drawable.hangzhou,
                latitude = 30.242865,
                longitude = 120.148688,
                locationPoints = getSampleLocationPoints("3"),
                paths = getSamplePaths("3")
            ),
            Trip(
                id = "4",
                title = "广州塔夜游",
                location = "广州",
                description = "登上广州塔，俯瞰整个广州城市夜景，珠江两岸灯火辉煌，非常壮观！",
                startDate = dateFormat.parse("2023-06-05") ?: Date(),
                endDate = dateFormat.parse("2023-06-05"),
                imageUrl = "",
                imageResource = com.example.memoroutev2.R.drawable.chengdu,
                latitude = 23.105833,
                longitude = 113.322513,
                locationPoints = getSampleLocationPoints("4"),
                paths = getSamplePaths("4")
            ),
            Trip(
                id = "5",
                title = "西安兵马俑探秘",
                location = "西安",
                description = "参观了兵马俑博物馆，震撼于古代工艺的精湛和秦始皇的雄心壮志，历史感扑面而来！",
                startDate = dateFormat.parse("2023-05-20") ?: Date(),
                endDate = dateFormat.parse("2023-05-22"),
                imageUrl = "",
                imageResource = com.example.memoroutev2.R.drawable.sanya,
                latitude = 34.384431,
                longitude = 109.278209,
                locationPoints = getSampleLocationPoints("5"),
                paths = getSamplePaths("5")
            )
        )
    }
    
    /**
     * 获取示例位置点数据
     */
    private fun getSampleLocationPoints(tripId: String): List<TripLocation> {
        // 根据不同的旅行ID生成不同的位置点
        return when (tripId) {
            "1" -> { // 北京长城
                listOf(
                    TripLocation(
                        id = UUID.randomUUID().toString(),
                        name = "八达岭长城入口",
                        description = "八达岭长城的南入口，游客中心所在地",
                        latitude = 40.359889,
                        longitude = 116.019546
                    ),
                    TripLocation(
                        id = UUID.randomUUID().toString(),
                        name = "八达岭长城北楼",
                        description = "登上北楼，视野开阔，可以俯瞰整个长城",
                        latitude = 40.364556,
                        longitude = 116.023837
                    ),
                    TripLocation(
                        id = UUID.randomUUID().toString(),
                        name = "长城博物馆",
                        description = "了解长城的历史和文化",
                        latitude = 40.357778,
                        longitude = 116.017778
                    )
                )
            }
            "2" -> { // 上海外滩
                listOf(
                    TripLocation(
                        id = UUID.randomUUID().toString(),
                        name = "外滩观光平台",
                        description = "观赏浦东陆家嘴夜景的最佳地点",
                        latitude = 31.233815,
                        longitude = 121.490607
                    ),
                    TripLocation(
                        id = UUID.randomUUID().toString(),
                        name = "外滩万国建筑群",
                        description = "欣赏各种风格的历史建筑",
                        latitude = 31.235556,
                        longitude = 121.488889
                    ),
                    TripLocation(
                        id = UUID.randomUUID().toString(),
                        name = "南京路步行街",
                        description = "著名的购物街，各种商店和美食",
                        latitude = 31.235278,
                        longitude = 121.480556
                    )
                )
            }
            "3" -> { // 杭州西湖
                listOf(
                    TripLocation(
                        id = UUID.randomUUID().toString(),
                        name = "断桥残雪",
                        description = "西湖十景之一，白蛇传故事发生地",
                        latitude = 30.258889,
                        longitude = 120.152778
                    ),
                    TripLocation(
                        id = UUID.randomUUID().toString(),
                        name = "三潭印月",
                        description = "西湖中心的小岛，风景优美",
                        latitude = 30.242865,
                        longitude = 120.148688
                    ),
                    TripLocation(
                        id = UUID.randomUUID().toString(),
                        name = "雷峰塔",
                        description = "西湖南岸的古塔，白蛇传故事地点",
                        latitude = 30.233889,
                        longitude = 120.147778
                    )
                )
            }
            "4" -> { // 广州塔
                listOf(
                    TripLocation(
                        id = UUID.randomUUID().toString(),
                        name = "广州塔观光层",
                        description = "登高俯瞰广州全景",
                        latitude = 23.105833,
                        longitude = 113.322513
                    ),
                    TripLocation(
                        id = UUID.randomUUID().toString(),
                        name = "珠江夜游码头",
                        description = "乘船游览珠江夜景",
                        latitude = 23.107778,
                        longitude = 113.325556
                    ),
                    TripLocation(
                        id = UUID.randomUUID().toString(),
                        name = "花城广场",
                        description = "广州市中心的大型广场",
                        latitude = 23.119444,
                        longitude = 113.324444
                    )
                )
            }
            else -> { // 西安兵马俑
                listOf(
                    TripLocation(
                        id = UUID.randomUUID().toString(),
                        name = "兵马俑一号坑",
                        description = "最大的兵马俑坑，有6000多个陶俑",
                        latitude = 34.384431,
                        longitude = 109.278209
                    ),
                    TripLocation(
                        id = UUID.randomUUID().toString(),
                        name = "兵马俑二号坑",
                        description = "展示不同兵种的陶俑",
                        latitude = 34.385278,
                        longitude = 109.279444
                    ),
                    TripLocation(
                        id = UUID.randomUUID().toString(),
                        name = "秦始皇陵",
                        description = "秦始皇的陵墓",
                        latitude = 34.381944,
                        longitude = 109.258889
                    )
                )
            }
        }
    }
    
    /**
     * 获取示例路径数据
     */
    private fun getSamplePaths(tripId: String): List<TripPath> {
        // 根据不同的旅行ID生成不同的路径
        val locationPoints = getSampleLocationPoints(tripId)
        
        // 如果位置点少于2个，无法形成路径
        if (locationPoints.size < 2) {
            return emptyList()
        }
        
        // 创建一条连接所有位置点的路径
        val pathPoints = locationPoints.mapIndexed { index, location ->
            TripLocation(
                id = UUID.randomUUID().toString(),
                name = "路径点 ${index + 1}",
                description = "从 ${location.name} 到 ${locationPoints.getOrNull(index + 1)?.name ?: "终点"}",
                latitude = location.latitude,
                longitude = location.longitude,
                type = TripLocation.LocationType.PATH_POINT,
                order = index
            )
        }
        
        return listOf(
            TripPath(
                id = UUID.randomUUID().toString(),
                name = "旅行路线",
                description = "完整的旅行路线",
                points = pathPoints,
                color = when (tripId) {
                    "1" -> 0xFF3F51B5.toInt() // 蓝色
                    "2" -> 0xFF4CAF50.toInt() // 绿色
                    "3" -> 0xFFFF9800.toInt() // 橙色
                    "4" -> 0xFFE91E63.toInt() // 粉色
                    else -> 0xFF9C27B0.toInt() // 紫色
                }
            )
        )
    }
} 