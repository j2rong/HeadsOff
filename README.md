#HeadsOff

- English description please refer to [here](https://github.com/j2rong/HeadsOff#english).

<br/>

##主要功能 (Features)

这是一个**Xposed模块**，用于**禁用**选定应用的[悬挂式（Heads-up）通知](http://developer.android.com/guide/topics/ui/notifiers/notifications.html#Heads-up)。

![Heads-up](http://developer.android.com/images/ui/notifications/heads-up.png)

部分应用（如：**微信WeChat**）滥用悬挂式通知且没有提供关闭选项，造成非常不良的用户体验，启用该模块后即可禁用同时不影响通知在状态栏、下拉列表中的显示。

- 支持Android 5、6


- 禁用选定应用的悬挂式通知
- 全局禁用
- 白名单，放行符合条件的悬挂式通知

<br/>

##屏幕截图 (Screenshots)

<p align="center">
<img src="https://github.com/j2rong/HeadsOff/blob/master/screenshot/0.2.28/screener_20160228(13_21_36).png" width="300"></a>
<img src="https://github.com/j2rong/HeadsOff/blob/master/screenshot/0.2.28/screener_20160228(13_21_19).png" width="300"></a>
</p>
<br/>

##使用方法

- 安装并于[Xposed Installer](https://github.com/rovo89/XposedInstaller)中启用模块，重启

- 在*HeadsOff*中打开要禁用悬挂式通知的应用右侧的开关

- 模块默认**不启用**，请在*HeadsOff*中选择“**设置**”-“**当前状态**”启用

<br/>

##更新日志 (Changelogs)

- **0.4.51**

  ```
  添加日志开关      
  增加白名单，符合条件的通知将不会被屏蔽      
  执行效率优化      
  ```


- **0.3.34**

  ```
  修正当更改是否显示系统应用选项后，列表不进行对应更新的Bug    
  修正部分情况下设置窗口返回时不更新列表的Bug     
  修正一个可能导致出现未知行为的Bug     
  减小APK的大小     
  ```


- **0.2.28**

  ```
  打开实验性功能（全局禁用）
  ```


- **0.1.23**

  ```
  修正选项读取错误
  ```

<br/>

##下载地址 (Downloads)

- [HeadsOff v0.4.51](https://github.com/j2rong/HeadsOff/releases/download/v0.4.51/HeadsOff_v0.4.51.177_2016-03-20.apk)

<br/>

##问题

- 如使用过程中出现问题，请在模块中打开日志，并将Xposed Installer中的日志提交至[此处](https://github.com/j2rong/HeadsOff/issues/new)。

<br/>

##致谢 (Acknowledgements)

- [**Xposed Framework**](https://github.com/rovo89/Xposed)
  
  Original work Copyright (c) 2005-2008, The Android Open Source Project    
  Modified work Copyright (c) 2013, rovo89 and Tungstwenty    
    
  Licensed under the Apache License, Version 2.0 (the "License");    
  you may not use this file except in compliance with the License.    
   
  Unless required by applicable law or agreed to in writing, software   
  distributed under the License is distributed on an "AS IS" BASIS,   
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   
  See the License for the specific language governing permissions and   
  limitations under the License.   

<br/>

##English

- An **Xposed** module for disabling [heads-up notifications](http://developer.android.com/guide/topics/ui/notifiers/notifications.html#Heads-up) per app on Android 5.0 and above.

<br/>

- **Features**

  - Disable heads-up notifications posted by the selected apps;

  - Disable all heads-up

  - White list, notifications match the rules will not be filtered

<br/>

- **Usage**
  - Install and enable the module in [Xposed Installer](https://github.com/rovo89/XposedInstaller), then reboot the phone
  - In **HeadsOff**, switch on the app you want to disable it's heads-up notifications
  - **HeadsOff** is **not enabled** by default, you need to go to "**Settings**"-"**Module Status**" to enable it

<br/>

- **Changelog**

  - **0.4.51**
    ```
	Add a log switch      
    White list, notifications match the rules will not be filtered      
    Optimize efficiency
    ```

  - **0.3.34**

    ```
    Fix a bug that list does not update accordingly whether or not show system app is enabled     
    Fix a bug that list does not refresh when return from settings     
    Fix a bug that may cause unexpected behavior         
    Reduce APK size   
    ```

  - **0.2.28**

    ```
    Add experimental feature (disable all heads-up notifications)
    ```

<br/>

- **Problems**

  Feel free to [open an issue](https://github.com/j2rong/HeadsOff/issues/new) if there is any problem. You can turn on the logs and submit (you can find it in Xposed Installer) to help identify the issue.