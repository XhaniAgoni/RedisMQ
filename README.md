# RedisMQ
Redis的消息队列(Redis Message Queue) <br>
使用Redis服务做消息队列              <br>
来源：主开发者是中国的 XhaniAgoni    <br>
联系QQ和微信：1305382505               <br>
电子邮箱：1305382505@qq.com              <br>
版本：1.0                            <br>


<h2>准备工作：</h2>                           <br>
Java  版本 大于 1.8                <br>
Window  下载 Redis 5.0以上版本  https://github.com/tporadowski/redis/releases         <br>
Sentinel 控制面板(建议1.6.2以上版本)                                                 <br>
Nacos 控制台(建议2.0以上版本)                                                       <br>

<h2>启动：</h2>                               <br>
优先启动Redis，再启动Nacos和Sentinel          <br>
(当然不启动并不影响程序运行,只是建议开启)      <br>
之后再启动RedisClientApplication(端口号6381)和RedisServiceApplication(端口号6380)启动类      <br>
你可以登录url ：端口号/swagger-ui.html 查看接口文档    <br>


<h2>持久化：</h2>
Redis提供的AOF和RDB持久化可以启用 建议全部开启<br>

<br>中文分割线
<hr>
<br>English dividing line

# RedisMQ
Message Queuing Using Redis (Redis Message Queuing) <br>
Use Redis service as message queue  <br>
Source: The main developer is XhaniAgoni in China.  <br>
Contact QQ and WeChat: 1305382505 <br>
E-mail :1305382505@qq.com <br>
Version: 1.0  <br>

<h2>Preparation:</h2>

Java version is greater than 1.8  <br>
Windows download Redis 5.0 or above https://github.com/tporadowski/redis/releases <br>
Sentinel control panel (version 1.6.2 or above is recommended)  <br>
Nacos console (version 2.0 or above is recommended) <br>
<h2>Start:</h2>

Start Redis first, then start Nacos and Sentinel. <br>
(Of course, not starting does not affect the running of the program, but it is recommended to start it) <br>
Then start the RedisClientApplication (port number 6381) and RedisServiceApplication (port number 6380) startup classes.  <br>
You can log on to the website: port number /swagger-ui.html to view the interface document. <br>
<h2>Persistence:</h2>
Use the persistence of AOF and RDB provided by Redis to enable all suggestions to be turned on. <br>
