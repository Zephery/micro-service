# mysql准备
1. 配置mysql
vim my.cnf增加如下配置：

```html
[mysqld]
log-bin=mysql-bin #添加这一行就ok
binlog-format=ROW #选择row模式
server_id=1 #配置mysql replaction需要定义，不能和canal的slaveId重复。
```
最好加上:
binlog-do-db=myblog



2. 创建账户
```html
CREATE USER canal IDENTIFIED BY 'canal';  
GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';
-- GRANT ALL PRIVILEGES ON *.* TO 'canal'@'%' ;
FLUSH PRIVILEGES;
```


# canal集群部署
1.canal下载
```html
cd /data
wget https://github.com/alibaba/canal/releases/download/v1.0.25/canal.deployer-1.0.25.tar.gz
```

2.解压缩
```html
mkdir /tmp/canal
tar -zxvf canal.deployer-1.0.25.tar.gz  -C /tmp/canal
```

3.配置修改（使用ZK）
vim /tmp/canal/conf/example/instance.properties

**instance.properties**

```html

#################################################
## mysql serverId(不能和mysql的master一样)
canal.instance.mysql.slaveId=342342
# position info
canal.instance.master.address=127.0.0.1:3306
canal.instance.master.journal.name=
canal.instance.master.position=
canal.instance.master.timestamp=


# table meta tsdb info（tsdb的全部注释掉）
# canal.instance.tsdb.enable=true
# canal.instance.tsdb.dir=${canal.file.data.dir:../conf}/${canal.instance.destination:}
# canal.instance.tsdb.url=jdbc:h2:${canal.instance.tsdb.dir}/h2;CACHE_SIZE=1000;MODE=MYSQL;
# canal.instance.tsdb.url=jdbc:mysql://127.0.0.1:3306/canal_tsdb
# canal.instance.tsdb.dbUsername=canal
# canal.instance.tsdb.dbPassword=canal


#canal.instance.standby.address =
#canal.instance.standby.journal.name =
#canal.instance.standby.position = 
#canal.instance.standby.timestamp = 
# username/password
canal.instance.dbUsername=canal
canal.instance.dbPassword=canal
canal.instance.defaultDatabaseName=test
canal.instance.connectionCharset=UTF-8
# table regex
canal.instance.filter.regex=.*\\..*
# table black regex
canal.instance.filter.black.regex=
#################################################
```

**canal.properties(部分)**

```html
canal.zkServers=119.29.188.224:2181
#注释掉tsdb
#canal.instance.tsdb.spring.xml=classpath:spring/tsdb/h2-tsdb.xml
```

4. 启动
```html
bin/startup.sh
```


5. 日志查看
tail -f logs/example/example.log
```html
2018-04-07 14:44:25.594 [main] INFO  c.a.otter.canal.instance.spring.CanalInstanceWithSpring - start CannalInstance for 1-example 
2018-04-07 14:44:25.616 [main] INFO  c.a.otter.canal.instance.core.AbstractCanalInstance - start successful....
```

tail -f logs/canal/canal.log
```html
2018-04-07 14:44:25.861 [main] INFO  com.alibaba.otter.canal.deployer.CanalLauncher - ## the canal server is running now ......
```
即启动成功

# 测试数据
可以通过
```sql
insert into admin values(32432,'aaaaa','bbbbb');
```

<div align="center">

![](http://image.wenzhihuai.com/images/20180407075913310376797.png)

</div>



