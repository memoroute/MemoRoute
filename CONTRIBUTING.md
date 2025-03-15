# 贡献指南

感谢你考虑为MemoRoute项目做出贡献！以下是参与项目的指南，请仔细阅读。

## 行为准则

参与本项目的所有贡献者都应遵循以下行为准则：

- 尊重所有参与者，不论其经验水平、性别、性取向、残疾状况、种族或宗教信仰
- 使用包容性语言
- 接受建设性批评
- 关注项目最佳利益
- 对其他社区成员表示同理心

## 如何贡献

### 报告Bug

如果你发现了Bug，请通过GitHub Issues报告，并包含以下信息：

1. 使用清晰的标题描述问题
2. 详细描述复现步骤
3. 预期行为与实际行为的对比
4. 截图（如适用）
5. 设备信息（型号、Android版本等）
6. 其他可能有助于解决问题的信息

### 提出新功能

如果你有新功能的想法，请先通过Issues讨论，包含以下内容：

1. 功能的详细描述
2. 为什么这个功能对大多数用户有用
3. 可能的实现方式
4. 相关的设计或UI草图（如适用）

### 提交代码

1. Fork项目仓库
2. 创建你的功能分支（`git checkout -b feature/amazing-feature`）
3. 提交你的更改（`git commit -m '添加某某功能'`）
4. 推送到分支（`git push origin feature/amazing-feature`）
5. 创建Pull Request

### Pull Request流程

1. 确保PR描述清晰地说明了更改内容和原因
2. 更新相关文档
3. PR可能会要求进行更改，请保持积极响应
4. PR被接受后，会被合并到主分支

## 开发流程

### 分支命名约定

- `feature/*`: 新功能
- `bugfix/*`: 修复Bug
- `improvement/*`: 改进现有功能
- `refactor/*`: 代码重构
- `docs/*`: 文档更新

### 提交信息规范

我们使用[约定式提交](https://www.conventionalcommits.org/)规范，格式如下：

```
<类型>[可选的作用域]: <描述>

[可选的正文]

[可选的脚注]
```

类型包括：
- `feat`: 新功能
- `fix`: Bug修复
- `docs`: 文档更改
- `style`: 不影响代码含义的更改（空格、格式化等）
- `refactor`: 既不修复Bug也不添加功能的代码更改
- `perf`: 提高性能的代码更改
- `test`: 添加或修正测试
- `chore`: 对构建过程或辅助工具的更改

示例：
```
feat(map): 添加路线绘制功能

添加了在地图上绘制旅行路线的功能，用户可以通过点击地图上的点来创建路线。

Closes #123
```

### 代码审查标准

所有代码都需要通过代码审查才能合并。审查标准包括：

1. 代码质量：遵循项目的代码风格和最佳实践
2. 测试覆盖：新功能和Bug修复应包含适当的测试
3. 文档：代码应有适当的注释，并更新相关文档
4. 性能：代码不应引入明显的性能问题
5. 安全性：代码不应引入安全漏洞

## 开发环境设置

请参考[开发者文档](DEVELOPER.md)中的"开发环境设置"部分。

## 测试

### 单元测试

所有新功能和Bug修复都应包含单元测试。我们使用JUnit和Mockito进行测试。

```kotlin
@Test
fun `when adding a trip then it should be saved to database`() {
    // 测试代码
}
```

### UI测试

对于UI组件，我们使用Espresso进行测试。

```kotlin
@Test
fun clickAddButton_opensAddTripForm() {
    // UI测试代码
}
```

### 运行测试

```bash
# 运行所有测试
./gradlew test

# 运行单元测试
./gradlew testDebugUnitTest

# 运行UI测试
./gradlew connectedDebugAndroidTest
```

## 文档

### 代码注释

- 为公共API添加KDoc注释
- 为复杂的方法添加实现注释
- 使用TODO注释标记待完成的工作

示例：
```kotlin
/**
 * 添加新的旅行记录
 *
 * @param trip 要添加的旅行对象
 * @return 添加成功返回true，否则返回false
 */
fun addTrip(trip: Trip): Boolean {
    // 实现...
}
```

### 更新文档

如果你的更改影响了用户体验或开发流程，请更新相应的文档：

- `README.md`: 项目概述
- `USER_MANUAL.md`: 用户手册
- `DEVELOPER.md`: 开发者文档

## 发布周期

我们使用[语义化版本](https://semver.org/)进行版本控制：

- 主版本号：不兼容的API更改
- 次版本号：向后兼容的功能性新增
- 修订号：向后兼容的问题修正

## 社区

### 沟通渠道

- GitHub Issues: 用于Bug报告和功能请求
- GitHub Discussions: 用于一般讨论和问题
- 电子邮件: support@memoroute.com（用于私人沟通）

### 获取帮助

如果你在贡献过程中需要帮助，可以：

1. 查阅[开发者文档](DEVELOPER.md)
2. 在GitHub Discussions中提问
3. 联系项目维护者

---

再次感谢你对MemoRoute项目的贡献！ 