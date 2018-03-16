local redis = require "resty.redis"
local red = redis:new()

local request_uri = ngx.var.request_uri

local function close_redis(red)
    if not red then
        return
    end
    local pool_max_idle_time = 10000
    local pool_size = 100
    red:set("pool_size", pool_size)
    local ok, err = red:set_keepalive(pool_max_idle_time, pool_size)
    if not ok then
        ngx_log(ngx_ERR, "set redis keepalive error : ", err)
    end
end

if (request_uri == "/" or request_uri == "/index.html") then
    red:set_timeout(1000)
    red:connect("119.23.46.71", 6340)
    local ok, err = red:auth("root")
    if not ok then
        ngx.say("failed to connect: ", err)
        return
    end
    local resp, errr = red:get("index")
    if not resp then
        return
    end
    if resp == ngx.null then
        resp = "<h1>hello world</h1>"
    end
    red:set("shouye", "hello world")
    red:expire("shouye", "32")
    ngx.print(resp)
    close_redis(red)
    return
end
local pagenum = ngx.req.get_uri_args()["pagenum"]
ngx.req.set_header("Accept", "text/html,application/xhtml+xml,application/xml;")
ngx.req.set_header("Accept-Encoding", "")
local respp = ngx.location.capture("/index.do", { method = ngx.HTTP_GET, args = { pagenum = pagenum } })
ngx.print(respp.body)
return