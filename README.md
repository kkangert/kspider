## 介绍

一个可视化的爬虫平台。以流程图的方式配置爬虫，基本上无需编写代码即可完成工作。
提供了常用的功能，当然使用者也可自定义扩展。[Kspider 前端](https://github.com/kkangert/kspider-ui)更方便、更快捷、更好用。

## 特性

-   支持 xpath 和 css 选择器
-   支持选择器提取、正则提取、json 提取等
-   支持 Cookie 自动管理
-   支持抓取由 js 动态渲染的页面
-   支持代理
-   支持多数据源
-   内置常用的字符串、日期、文件、加解密等函数
-   支持结果保存至多目的地（数据库、csv 文件等）
-   支持插件扩展（自定义执行器，自定义函数等）
-   支持任务日志
-   支持爬虫可视化调试
-   支持同步、异步执行
-   支持插件扩展
-   支持自定义 JS 脚本引擎
-   支持产物下载

新增的特性：

-   采用 Spring Data JPA 实现数据库操作，支持多种数据库，如 MySQL、PostgreSQL、SQLite、Oracle、MSSQL 等
-   支持同步执行，对于执行结果有顺序要求的可以使用该功能
-   增加随机 User-Agent（数据来自：[useragentstring.com](http://useragentstring.com/pages/useragentstring.php) ）
-   增加身份认证机制
-   增加延迟执行节点
-   通过远程 WebDriver 来操纵浏览器（本地不用再加载驱动，更省心，依赖[Selenium](https://github.com/SeleniumHQ/selenium)）
-   支持自定义执行器
-   支持容器化部署

## TODO

-   支持分布式部署
-   增加代理的管理界面，方便进行代理的手工添加（不推荐）和启动代理的自动管理功能
-   支持 SQL 节点更多数据源及数据源配置
-   重构表达式解析器
-   增加自定义函数 JavaScript 脚本节点（满足更复杂的数据处理能力）
-   增加用户管理系统
-   增加OCR图文识别节点
-   增加爬虫任务实时日志

## 免责声明

请勿使用本项目进行任何可能会违反法律规定和道德约束的工作。如您选择使用本项目，即代表您遵守此声明，作者不承担由于您违反此声明所带来的任何法律风险和损失。

## 作者

-   WeChat: kangert
-   Email: kangert@qq.com
