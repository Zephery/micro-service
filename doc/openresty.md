上一篇文章讲到使用多级缓存来减少数据库的访问来加快网页的速度，只是，仍旧没有“嗖”一下就加载出来的感觉，想再优化一下，优化代码什么的已经到了极限。上周无意中看到了openresty，可以使




```html
2018/02/10 18:53:51 [error] 2833#0: *3942 lua subrequests cycle while processing "/index.html", client: 113.108.186.130, server: www.wenzhihuai.com, request: "GET /index.html?pagenum=23 HTTP/1.1", subrequest: "/index.html", host: "www.wenzhihuai.com"
2018/02/10 18:53:51 [error] 2833#0: *3942 lua entry thread aborted: runtime error: /opt/lua/hello.lua:25: failed to issue subrequest: -1
stack traceback:
coroutine 0:
	[C]: in function 'capture'
	/opt/lua/hello.lua:25: in function </opt/lua/hello.lua:1>, client: 113.108.186.130, server: www.wenzhihuai.com, request: "GET /index.html?pagenum=23 HTTP/1.1", subrequest: "/index.html", host: "www.wenzhihuai.com"
```






<div align="center">

![](http://image.wenzhihuai.com/images/20180210073318.png)

</div>

解决办法：
ngx.req.set_header("Accept-Encoding", "");