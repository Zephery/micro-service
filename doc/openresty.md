新年快乐~~~
上一篇文章讲到使用多级缓存来减少数据库的访问来加快网页的速度，只是，仍旧没有“嗖”一下就加载出来的感觉，想再优化一下，优化代码什么的已经到了极限。上周无意中看到了openresty，能够直接在nginx层直接对请求处理，而不需要每次都访问tomcat，特别是对于自己的首页[http://www.wenzhihuai.com](http://www.wenzhihuai.com)来说，由于首页内容广泛，需要涉及的方法太多，每次打开首页都得耗费大量的时间。

# 一、整体架构
目前本人总共有三个服务器：
**A:119.23.46.71(深圳，Nginx环境，本地Redis，tomcat服务器)**
**B:119.29.188.224(广州，tomcat服务器)**
**C:47.95.10.139(北京，tomact服务器)**
为了减少后端的相应时间，之前使用的是在应用里集成ehcache作为一级缓存，redis作为二级缓存。这种架构存在一种特殊的情况：当Nginx将首页的请求分发给北京节点的时候，响应将变得极其缓慢，用户的请求需要从深圳到北京，再从北京回到深圳，光是延时就要耗费40ms(最好的情况)，由于网速是1M/s，最坏的情况下，响应用户的请求也得耗费几秒。所以，为了减少这种极端情况，设计了这款架构（个人项目随便整整而已，怎么来都行啦）。
由于是博客类的项目，对于一致性要求并不高，所以干脆直接将首页缓存到Redis即可。
<div align="center">![](http://image.wenzhihuai.com/images/20180214105827.png)</div>
步骤：
1.请求到达nginx后，openresty通过lua读取本地缓存，如果不命中，则回源到tomcat集群。  
2.tomcat集群首先从自己的服务器中读取一级缓存Ehcache，如果没有命中，则继续回源到二级缓存。  
3.读取二级缓存Redis，如果依旧没有命中，则回源到MySQL服务器。
整体架构以图为准~~  

# 二、配置过程

### 2.1 Openresty
安装过程可以直接参考官方文档:[http://openresty.org/cn/download.html](http://openresty.org/cn/download.html)，安装前还需安装以下开发库：
```html
yum install pcre-devel openssl-devel gcc curl
```

然后进行编译安装:
```html
tar -xzvf openresty-VERSION.tar.gz
cd openresty-VERSION/
./configure
make
sudo make install
```

### 2.2 Nginx相关配置
Openresty自带了Nginx。所以，只要安装好了Openresty，即可直接使用nginx来配置。
以下只是部分，需要全部的请查看[mynginxconfig](https://github.com/Zephery/micro-service/blob/master/conf/nginx/mynginxconfig.ngx)
```nginx
http {
    include       mime.types;
    default_type  application/octet-stream;
    # 需要添加lua的相关库
    lua_package_path "/opt/openresty/lualib/?.lua;;";
    lua_package_cpath "/opt/openresty/lualib/?.so;;";
    ...

    access_log  logs/access.log  main;
    sendfile        on;
    keepalive_timeout  65;

    upstream backend {
        #consistent_hash was not configured
        hash $uri;
        server 47.95.10.139:8080;
        server 119.23.46.71:8080;
        server 119.29.188.224:8080;
    }
    server {
        listen       80;
        server_name  www.wenzhihuai.com;
        # 精确匹配，打开首页的时候进入
        location  = / {
            default_type    text/html;
            root   html;
            index  index.html index.htm;
            ...
            # 关闭缓存lua脚本，调试的时候专用
            lua_code_cache off;
            content_by_lua_file /opt/lua/hello.lua;
            # 此处不要proxy_pass了，否则lua脚本没用
            # proxy_pass http://backend;

        }
        # 如果上面的不符合，则匹配下面的
        location / {
            default_type    text/html;
            root   html;
            index  index.html index.htm;
            # 对请求进行反向代理
            proxy_pass http://backend;
        }
    }
    ...
}
```
### 2.3 lua脚本
脚本记得放在/opt/lua/hello.lua目录下，对应nginx的配置，同时需要引入redis模块。
```lua
local redis = require "resty.redis"
local red = redis:new()

local request_uri = ngx.var.request_uri

if (request_uri == "/" or request_uri == "/index.html") then
    red:set_timeout(1000) -- 1 sec
    red:connect("119.23.46.71", 6340)
    local ok, err = red:auth("root")
    if not ok then
        ngx.say("failed to connect: ", err)
        return
    end
    --缓存的首页放在key为index里
    local resp, errr = red:get("index")
    if not resp then
        return
    end
    if resp == ngx.null then
        resp = "<h1>hello world</h1>"
    end
    --如果找到，则输出内容
    ngx.print(resp)
    red:close()
    return
end
local pagenum = ngx.req.get_uri_args()["pagenum"]
--因为在nginx中设置了proxy_pass_request_headers off，即不讲请求头部传到lua，所以头部需要重新设置
ngx.req.set_header("Accept", "text/html,application/xhtml+xml,application/xml;")
--这里回源到tomcat的时候，Accept-Encoding默认为gzip，即返回来数据已经是gzip压缩过了的，返回到用户的时候又被压缩了一次，会造成一堆乱码。所以将Accept-Encoding设置为空。
ngx.req.set_header("Accept-Encoding", "")
local respp = ngx.location.capture("/index.do", { method = ngx.HTTP_GET, args = { pagenum = pagenum } })
--打印
ngx.print(respp.body)
return
```

### 2.4 更新首页到redis
每隔20秒直接访问后端进行首页的抓取，然后存储到redis里面，简单粗暴。
```java
@Controller
@SuppressWarnings("unchecked")
public class TimeController {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(TimeController.class);
    @Scheduled(cron = "0/20 * * * * ?")
    public void refreshIndex() throws Exception {
        String ip = IPUtils.getServerIp().replaceAll("\n", "");
        if (REGULARIP.equals(ip)) {
            String content = HttpHelper.getInstance().get("http://119.29.188.224:8080");
            JedisUtil.getInstance().set("index", content);
        }
    }
}
```

# 碰到的问题
### capture报错
```html
2018/02/10 18:53:51 [error] 2833#0: *3942 lua subrequests cycle while processing "/index.html", client: 113.108.186.130, server: www.wenzhihuai.com, request: "GET /index.html?pagenum=23 HTTP/1.1", subrequest: "/index.html", host: "www.wenzhihuai.com"
2018/02/10 18:53:51 [error] 2833#0: *3942 lua entry thread aborted: runtime error: /opt/lua/hello.lua:25: failed to issue subrequest: -1
stack traceback:
coroutine 0:
	[C]: in function 'capture'
	/opt/lua/hello.lua:25: in function </opt/lua/hello.lua:1>, client: 113.108.186.130, server: www.wenzhihuai.com, request: "GET /index.html?pagenum=23 HTTP/1.1", subrequest: "/index.html", host: "www.wenzhihuai.com"
```
### 打开网页乱码，用curl正常显示
查了好久，才发现Openresty如果使用nginx.location.capture是重新请求一次的，如果带有请求头并且开启了gzip压缩，那么数据就相当于被再次压缩。
<div align="center">![](http://image.wenzhihuai.com/images/20180210073318.png)</div>

解决办法：去掉请求头部
ngx.req.set_header("Accept-Encoding", "");