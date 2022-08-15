#创建数据库
create database if not exists framework default charset utf8mb4 collate utf8mb4_general_ci;

#查看创建库是否成功
show databases;

#MySQL5.7版本建议先创建用户，再给用户进行授权操作。

#创建用户
create user 'framework'@'%' identified by '123456';

#查看刚创建的用户
select user,host from mysql.user;

#用户授权
Grant all privileges on framework.* to 'framework'@'%' with grant option;

#刷新系统授权表

flush privileges;