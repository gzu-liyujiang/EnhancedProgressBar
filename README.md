# 安卓项目工程模板

![Release APK](https://github.com/gzu-liyujiang/EnhancedProgressBar/workflows/Release%20APK/badge.svg)
![Gradle Package](https://github.com/gzu-liyujiang/EnhancedProgressBar/workflows/Gradle%20Package/badge.svg)

Android的增强版`android.widget.ProgressBar`，带数字的水平滚动条（支持长方形、平行四边形及椭圆角矩形），继承自`android.widget.ProgressBar`，因此用法和`ProgressBar`完全一样。

### 最新版本

[![jitpack](https://jitpack.io/v/gzu-liyujiang/EnhancedProgressBar.svg)](https://jitpack.io/#gzu-liyujiang/EnhancedProgressBar)

```groovy
allprojects {
    repositories {
        maven { url 'https://www.jitpack.io' }
    }
}
```
```groovy
dependencies {
    implementation 'com.github.gzu-liyujiang:EnhancedProgressBar:版本号'
}
```

### 效果预览

![效果图](/screenshot.png)

```xml
<com.github.gzuliyujiang.progressbar.EnhancedProgressBar
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="10dp"
    android:progress="50"
    android:visibility="visible"
    app:lyj_cut_corner="parallelogram"
    app:lyj_reached_color="#00FBD0"
    app:lyj_reached_height="10dp"
    app:lyj_text_align="top"
    app:lyj_text_bold="true"
    app:lyj_text_color="#00FBD0"
    app:lyj_text_offset="4dp"
    app:lyj_text_size="12sp"
    app:lyj_text_visible="true"
    app:lyj_unreached_color="#E5E5E5"
    app:lyj_unreached_height="8dp" />
```

## 开发环境

- JDK 1.8
- Android Studio 4.1.3
- Android SDK 30
- Gradle 6.5
- Plugin@FindViewByME 1.4.3
- Plugin@GsonFormatPlus 1.5.8
- 刘海屏模拟器(Pixel 3 XL API 30)

## License

```text
Copyright (c) 2019-2021 gzu-liyujiang <1032694760@qq.com>

The software is licensed under the Mulan PSL v1.
You can use this software according to the terms and conditions of the Mulan PSL v1.
You may obtain a copy of Mulan PSL v1 at:
    http://license.coscl.org.cn/MulanPSL
THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
PURPOSE.
See the Mulan PSL v1 for more details.
```
