local redis = require "resty.redis"
local red = redis:new()
local request_uri = ngx.var.request_uri
local ngx_log = ngx.log
local ngx_ERR = ngx.ERR

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

local uri = ngx.var.uri

red:set_timeout(1000)
red:connect("119.23.46.71", 6340)
red:auth("root")
local uri_args = ngx.req.get_uri_args()

red:set("request_uri", request_uri)
red:set("uri_args", uri_args)
red:set("uri", uri)

local key = request_uri
local resp, err = red:get(key)

if resp == ngx.null then
    ngx.req.set_header("Accept", "text/html,application/xhtml+xml,application/xml;")
    ngx.req.set_header("Accept-Encoding", "")
    local targetURL = string.gsub(uri, "html", "do")
    red:set("targetURL", targetURL)
    local respp = ngx.location.capture(targetURL, { method = ngx.HTTP_GET, args = uri_args })
    red:set(key, respp.body)
    red:expire(key,3600)
    return
end
ngx.print(resp)
close_redis(red)
