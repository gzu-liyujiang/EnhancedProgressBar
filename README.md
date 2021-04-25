# 安卓项目工程模板

![Release APK](https://github.com/gzu-liyujiang/AliyunGradleConfig/workflows/Release%20APK/badge.svg)
![Gradle Package](https://github.com/gzu-liyujiang/AliyunGradleConfig/workflows/Gradle%20Package/badge.svg)

详情可查看[模板介绍](https://gzu-liyujiang.github.io/AliyunGradleConfig)

## 开发环境

- JDK 1.8
- Android Studio 4.1.3
- Android SDK 30
- Gradle 6.5
- Plugin@FindViewByME 1.4.3
- Plugin@GsonFormatPlus 1.5.8
- 刘海屏模拟器(Pixel 3 XL API 30)

## 架构模式：模块化/组件化

- 通过模块化/组件化组织代码，面向接口编程，尽可能做到高内聚、低耦合、重复用。
- 模块可分为多种类型，一般分为：基础库（日志打印、网络请求、图片加载等）、三方包（微信登录、统计分析、消息推送等）、业务组件（APP外壳、会员中心、商城等）。
- 常见组件间通信方式：直接依赖（耦合太重，不推荐）、事件或广播（难以溯源，不推荐）、路由（如 ARouter）、面向接口（推荐）。

### 模块化/组件化的优势：

- 结构清晰：业务独立，每个业务作为单独的组件，代码实现分离，不会搅在一起。
- 便于协作：每个开发人员只关心自己负责的模块/组件，每个模块/组件作为一个子工程，没有太多的耦合。
- 便于维护：各模块/组件管理自己的代码、布局、资源，主工程可以方便添加与移除。


### 组件化开发的实施步骤

- 1、在接口层（`contract`），每个业务模块（如统计分析）定义相关接口（如`StatisticContract`）并继承自`IContract`约定好要对外提供的方法。

```java
/**
 * 接口约定，用于通过注解技术反射获取接口的实例
 */
public interface IContract {

}
```
```java
public interface StatisticContract extends IContract {

    void setup(@NonNull Application application, @NonNull String appKey, @NonNull String channel,
               boolean agreeEulaOrPolicy, boolean isRelease);

    void onEvent(@NonNull String eventID, @NonNull Map<String, Object> map);

    //省略...
}
```

- 2、具体的业务组件如友盟统计（`umeng`）依赖接口层（`contract`），对约定的接口进行实现（如`UMengImpl`），同时通过注解`ContractImpl`为`StatisticContract`指定该实现类。若在将来要更换成极光统计（`jiguang`），则重新指定实现类即可。

```groovy
implementation project(':contract')
```
```java
/**
 * 用于指定接口的实现类
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ContractImpl {
    String className();
}
```
```java
@ContractImpl(className = "com.github.gzuliyujiang.umeng.UMengImpl")
//@ContractImpl(className = "com.github.gzuliyujiang.jiguang.JiGuangImpl")
public interface StatisticContract extends IContract {
//省略...
}
```

- 3、其他使用方都依赖接口层（`contract`），且具体的业务模块（如`umeng`）必须通过`runtimeOnly`进行代码隔离，并通过接口管理器（`ContractMaster`）获取所需的接口（如`StatisticContract`）使用。

```groovy
implementation project(':contract')
runtimeOnly project(':umeng')
//runtimeOnly project(':jiguang')
```
```java
/**
 * 通过“注解+反射”获取指定接口的实例
 */
public final class ContractMaster {

    @NonNull
    public static <T extends IContract> T get(@NonNull Class<T> contract) {
        //省略...
    }

}
```
```groovy
ContractMaster.get(StatisticContract.class)
```

## 设计模式：MVVM

使用谷歌架构组件：`ViewModel`+`LiveData`+`Room`。

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
