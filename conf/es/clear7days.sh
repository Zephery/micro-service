#!/usr/bin/env bash

#指定日期(7天前)
DATA=`date -d "1 week ago" +%Y.%m.%d`

echo ${DATA}
#当前日期
time=`date`

#删除7天前的日志
curl -XDELETE http://127.0.0.1:9200/*-${DATA}

if [ $? -eq 0 ];then
  echo ${time}"-->del $DATA log success.." >> /tmp/es-index-clear.log
else
  echo ${time}"-->del $DATA log fail.." >> /tmp/es-index-clear.log
fi