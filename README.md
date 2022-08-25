# RedisMQ
Redis的消息队列(Redis Message Queue) <br>
使用Redis服务做消息队列              <br>
来源：主开发者是中国的 XhaniAgoni    <br>
联系QQ和VX：1305382505               <br>
邮箱：1305382505@qq.com              <br>
版本：1.0                            <br>

<style>
  *{font-size:18px}
<style>

<h2>准备工作：</h2>                           <br>
java  版本 大于 1.8                <br>
window  下载 Redis 5.0以上版本  https://github.com/tporadowski/redis/releases         <br>
sentinel 控制面板(建议1.6.2以上版本)                                                 <br>
nacos 控制台(建议2.0以上版本)                                                       <br>

<h2>启动：</h2>                               <br>
优先启动redis，再启动nacos和sentinel 
(当然不启动并不影响程序运行,只是建议开启)      <br>
之后再启动RedisClientApplication(端口号6381)和RedisServiceApplication(端口号6380)启动类      <br>
你可以登录url ：端口号/swagger-ui.html 查看接口文档    <br>




