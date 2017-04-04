# 基于JAVA的新浪微博关系网络爬虫

------
对墙内最大的社交平台之一**新浪微博**的爬虫，随便查了查就有很多，但是基于JAVA实现的好像还很少，大多都是基于Python，py好像天生就是用来搞爬虫的，说实话我也是因为学习爬虫，才开始了解的Python。但是个人还是喜欢JAVA这种语法严谨的语言，再配上Eclipse这个神器更是美滋滋。所以有个什么想法一般还是喜欢用JAVA实现。

这个爬虫爬取的是新浪用户的关注网络，然后用Gephi这个网络分析工具做网络参数分析和可视化。用了HttpClient处理请求，Jsoup解析页面，MySQL存储数据。

初学的一个小Demo，望各路大神指点

## 功能
    爬取特定微博用户的关注网络，并进行图论分析及可视化

## 实现

功能也比较简单明了就不写具体实现过程了，直接放上结果吧

这是用户信息表，共10268个用户
![用户信息表][1]

这是关注关系表
![关注表][2]

这是可视化的网络
![此处输入图片的描述][3]

## 遇到的问题

 - 因为新浪的模拟登陆比较麻烦涉及到JS加密，而且新浪的cookie有效时间较长，所以就选择直接用Fiddler抓取cookie跳过登录流程
 - 绕过新浪的访客系统（Sina Visitor System），将User-agent设为搜索引擎爬虫即可


  [1]: https://raw.githubusercontent.com/pokerfaceSad/SinaNetSpider/master/%E7%94%A8%E6%88%B7%E4%BF%A1%E6%81%AF%E8%A1%A8.png
  [2]: https://raw.githubusercontent.com/pokerfaceSad/SinaNetSpider/master/%E5%85%B3%E7%B3%BB%E8%A1%A8.png
  [3]: https://raw.githubusercontent.com/pokerfaceSad/SinaNetSpider/master/%E5%8F%AF%E8%A7%86%E5%8C%96%E7%BD%91%E7%BB%9C.png
