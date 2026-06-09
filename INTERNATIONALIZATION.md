# 国际化 (i18n) 指南

本文档描述了音乐播放器应用的国际化实现和最佳实践。

## 支持的语言

应用目前支持以下12种语言：

| 语言代码 | 语言名称 | 本地化名称 | 资源文件夹 |
|---------|----------|------------|------------|
| "" | 系统默认 | System | `values/` |
| en | 英语 | English | `values-en/` |
| zh-CN | 简体中文 | 简体中文 | `values-zh-rCN/` |
| zh-TW | 繁体中文 | 繁體中文 | `values-zh-rTW/` |
| es | 西班牙语 | Español | `values-es/` |
| pt | 葡萄牙语 | Português | `values-pt/` |
| ja | 日语 | 日本語 | `values-ja/` |
| ko | 韩语 | 한국어 | `values-ko/` |
| in | 印尼语 | Bahasa Indonesia | `values-in/` |
| fr | 法语 | Français | `values-fr/` |
| de | 德语 | Deutsch | `values-de/` |
| ru | 俄语 | Русский | `values-ru/` |

## 架构组件

### LanguageManager

`LanguageManager` 是管理应用语言设置的核心类：

```kotlin
@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // 设置应用语言
    suspend fun setLanguage(languageCode: String)
    
    // 应用语言设置（立即生效）
    fun applyLanguage(languageCode: String)
    
    // 获取当前语言显示名称
    fun getCurrentDisplayName(): String
    
    // 检查是否支持某种语言
    fun isLanguageSupported(languageCode: String): Boolean
    
    // 获取系统语言代码
    fun getSystemLanguageCode(): String
}
```

### 数据存储

- 使用 Android DataStore 保存用户的语言偏好
- 语言设置在应用重启后会自动恢复
- 支持"跟随系统"选项

### 动态语言切换

- 使用 `AppCompatDelegate.setApplicationLocales()` 实现运行时语言切换
- 无需重启应用即可生效
- 兼容 Android 13+ 的系统级语言设置

## 字符串资源组织

### 分类结构

字符串资源按功能分类组织：

```xml
<!-- 导航和界面元素 -->
<string name="nav_library">Library</string>
<string name="nav_search">Search</string>

<!-- 播放控制 -->
<string name="play">Play</string>
<string name="pause">Pause</string>

<!-- 设置功能 -->
<string name="settings_theme">Theme</string>
<string name="settings_language">Language</string>

<!-- 错误信息 -->
<string name="error_file_not_found">File not found</string>
<string name="error_playback_failed">Playback failed</string>

<!-- 提示信息 -->
<string name="toast_added_to_playlist">Added to playlist</string>
<string name="toast_playlist_created">Playlist created</string>

<!-- 对话框 -->
<string name="dialog_delete_playlist_title">Delete Playlist</string>
<string name="dialog_delete_playlist_message">Are you sure?</string>

<!-- 无障碍支持 -->
<string name="accessibility_play_button">Play button</string>
<string name="accessibility_pause_button">Pause button</string>
```

### 命名约定

- 使用描述性的键名：`settings_theme` 而不是 `theme`
- 分组前缀：`nav_*`, `settings_*`, `error_*`, `toast_*`, `dialog_*`, `accessibility_*`
- 保持一致性：所有语言版本必须包含相同的键

## 翻译质量控制

### 术语一致性

在所有语言版本中保持术语的一致性：

- 播放列表/Playlists - 所有中文版本统一使用"播放列表"
- 歌曲/Tracks - 在界面中统一使用"歌曲"而不是"音轨"
- 设置/Settings - 统一翻译为"设置"

### 文化适应性

- 考虑不同文化的表达习惯
- 德语：考虑复合词的长度对UI的影响
- 阿拉伯语：预留RTL（从右到左）布局支持
- 日语：平衡汉字、平假名和外来语的使用

### 长度考虑

不同语言的文本长度差异很大：
- 德语通常比英语长 30-50%
- 中文通常比英语短 20-30%
- 确保UI布局能适应这些差异

## 使用工具类

### StringFormatter

使用 `StringFormatter` 类来处理复杂的字符串格式化：

```kotlin
// 格式化数量
StringFormatter.formatTrackCount(context, count)
StringFormatter.formatArtistCount(context, count)

// 格式化时长和文件大小
StringFormatter.formatDuration(durationMs)
StringFormatter.formatFileSize(context, sizeBytes)

// 获取本地化的空状态消息
StringFormatter.getEmptyStateMessage(context, ContentType.TRACKS)
```

## 最佳实践

### 1. 开发阶段

- 始终使用 `stringResource(R.string.key)` 而不是硬编码字符串
- 在添加新功能时同时添加所有语言的翻译
- 使用有意义的字符串键名

### 2. 测试

- 在不同语言下测试UI布局
- 验证长文本不会被截断
- 测试动态语言切换功能
- 使用 `LanguageManagerTest` 验证语言管理逻辑

### 3. 维护

- 定期审查翻译质量
- 保持所有语言版本的同步更新
- 收集用户反馈来改进翻译

## 添加新语言

要添加对新语言的支持：

1. **创建资源文件夹**
   ```bash
   mkdir app/src/main/res/values-[language-code]/
   ```

2. **复制并翻译字符串文件**
   ```bash
   cp values/strings.xml values-[language-code]/
   # 然后翻译内容
   ```

3. **更新 LanguageManager**
   ```kotlin
   val SUPPORTED_LANGUAGES = listOf(
       // ... 现有语言
       LanguageOption("[code]", "[English Name]", "[Native Name]")
   )
   ```

4. **测试新语言**
   - 在设置中测试语言切换
   - 验证所有界面的显示效果
   - 确保没有缺失的翻译

## 无障碍支持

应用包含完整的无障碍字符串支持：

- 为所有交互元素提供内容描述
- 使用 `accessibility_*` 前缀的字符串
- 确保屏幕阅读器能正确读取界面元素

## 故障排除

### 常见问题

1. **语言切换后界面没有更新**
   - 检查是否正确调用了 `applyLanguage()`
   - 确认 `AppCompatDelegate` 的设置

2. **某些字符串显示为键名**
   - 检查对应语言的 `strings.xml` 文件
   - 确认字符串键名拼写正确

3. **UI布局在某些语言下破损**
   - 检查文本长度限制
   - 考虑使用弹性布局
   - 测试极端情况（最长/最短文本）

### 调试工具

使用以下方法调试多语言问题：

```kotlin
// 检查当前语言设置
val currentLanguage = languageManager.getCurrentDisplayName()
Log.d("Language", "Current: $currentLanguage")

// 验证语言支持
val isSupported = languageManager.isLanguageSupported("zh-CN")
Log.d("Language", "zh-CN supported: $isSupported")
```

## 未来计划

1. **增加更多语言支持**
   - 阿拉伯语 (RTL支持)
   - 意大利语
   - 土耳其语
   - 泰语

2. **增强功能**
   - 自动检测最佳语言匹配
   - 改进复数形式处理
   - 区域特定的格式化（日期、数字）

3. **工具改进**
   - 翻译完成度检查工具
   - 自动化翻译质量验证
   - 与翻译服务的集成