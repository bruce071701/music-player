# Android 项目基础规范

## SDK 版本要求

- compileSdk: 35
- targetSdk: 35 (Android 15)
- minSdk: 26 (Android 8.0)

## Google Play 上架必需

### build.gradle.kts
```kotlin
android {
    packaging {
        jniLibs {
            useLegacyPackaging = false
            excludes += "**/libdatastore_shared_counter.so"  // 16KB 兼容
        }
    }
}
```

### AndroidManifest.xml
```xml
android:extractNativeLibs="false"
tools:targetApi="35"
```

### 必须权限声明
- `POST_NOTIFICATIONS` — Android 13+ 通知必需
- 前台服务需声明 `foregroundServiceType`

### 16KB 页面支持
- `jniLibs.useLegacyPackaging = false`
- `android:extractNativeLibs="false"`
- 排除不兼容 16KB 的第三方 .so 文件

## 签名

### 文件
- `release-keystore.jks` — 签名密钥（不提交 git）
- `keystore.properties` — 密码配置（不提交 git）

### keystore.properties 格式
```
storeFile=../release-keystore.jks
storePassword=xxx
keyAlias=xxx
keyPassword=xxx
```

### build.gradle.kts 配置
```kotlin
val keystoreProperties = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) load(file.inputStream())
}

android {
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties.getProperty("storeFile", ""))
            storePassword = keystoreProperties.getProperty("storePassword", "")
            keyAlias = keystoreProperties.getProperty("keyAlias", "")
            keyPassword = keystoreProperties.getProperty("keyPassword", "")
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
}
```

### 生成签名
```bash
keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my_alias
```

## 版本号规则

```kotlin
versionCode = 100001  // 主版本*100000 + 序号，递增：100001, 100002, 100003...
versionName = "1.0.1" // 用户可见版本
```

### 命名规范
| versionName | versionCode | 说明 |
|-------------|-------------|------|
| 1.0.0 | 100000 | 首个正式版 |
| 1.0.1 | 100001 | 修复版本 |
| 1.0.2 | 100002 | 修复版本 |
| 1.1.0 | 100010 | 小功能更新 |
| 2.0.0 | 200000 | 大版本更新 |

文件位置：`app/build.gradle.kts` → `defaultConfig` 块

## 打包脚本

项目根目录保留 `build_release.sh`，一键执行：

```bash
./build_release.sh
```

### 脚本功能
1. 检查签名文件是否存在
2. 读取并显示当前版本号
3. Clean 旧构建
4. Lint 检查
5. 构建签名 AAB（Google Play 上传用）
6. 构建签名 APK（测试安装用）
7. 复制产物到 `release_output/v{版本号}/` 目录
8. 文件名带版本号：`{项目名}-v1.0.1.aab`
9. 验证签名
10. 显示文件大小

### 产出目录结构
```
release_output/v1.0.1/
├── {项目名}-v1.0.1.aab          # 上传 GP
├── {项目名}-v1.0.1-arm64.apk    # 64位测试
├── {项目名}-v1.0.1-armv7.apk    # 32位测试
└── {项目名}-v1.0.1-x86_64.apk   # 模拟器测试
```

### 手动打包命令
```bash
# AAB（上传 Google Play）
./gradlew bundleRelease

# APK（测试安装）
./gradlew assembleRelease

# 全量清理重建
./gradlew clean bundleRelease assembleRelease
```

### 发版流程
1. 修改 `app/build.gradle.kts` 中 versionCode + versionName
2. 运行 `./build_release.sh`
3. 上传 `release_output/` 下的 .aab 到 Google Play Console
4. 提交审核

## .gitignore 必须包含

```
*.jks
*.keystore
keystore.properties
local.properties
/build
**/build/
.gradle/
.idea/
*.apk
*.aab
.DS_Store
```

## namespace 与 applicationId 分离时注意

当 `namespace` 和 `applicationId` 不同：
- AndroidManifest 中组件用完整类名：`android:name="com.xxx.MainActivity"`
- R 类 import 跟随 namespace
- BuildConfig import 跟随 namespace
- Widget broadcast action 用实际 applicationId 作为 package
