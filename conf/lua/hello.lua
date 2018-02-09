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
    local resp, errr = red:get("index")
    if not resp then
        return
    end
    if resp == ngx.null then
        resp = "<h1>hello world</h1>"
    end
    ngx.print(resp)
    red:close()
    return
end