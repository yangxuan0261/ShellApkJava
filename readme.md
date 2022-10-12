> apk 加固 demo

---

### 使用步骤

1. 将 *app_src* 工程编译出来的 apk, 放到 *app_shell* 工程 (壳) 的 *assets* 目录下, 重名为 *hello.db*
2. 编译 *app_shell* 并运行即可



---

### 说明

1. *app_shell* 里面 *AndroidManifest.xml* 配置的启动 activity 是 *app_src* 中的启动 activity



---

### 相关参考资源

- 【Android插件化】启动没有在Manifest中注册的Activity - https://blog.csdn.net/u013293125/article/details/105407056
    - https://github.com/ydslib/PluginTest01
- Android最初的加固 - https://www.jianshu.com/p/8e578dc2c5db
- Android开发学习之路-加固实践 (好文, 貌似来源于上一个连接) - https://blog.csdn.net/eastmoon502136/article/details/103703950
    - demo - https://github.com/eastmoon1117/StudyTestCase/tree/master/DexPack/ShellApk
- Apk加固原理学习与复现 - https://www.anquanke.com/post/id/247644



---
