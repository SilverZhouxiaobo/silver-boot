drop table if exists sys_company;
create table sys_company
(
    id          varchar(64) not null comment '主键',
    name        varchar(90) not null comment '公司名称',
    short_name  varchar(90) comment '公司简称',
    code        varchar(32) comment '公司编码',
    node        varchar(32) comment '节点编码',
    pid         varchar(64) comment '上级公司',
    order_no    int comment '排序',
    type        varchar(32) comment '公司类型',
    website     varchar(255) comment '官网地址',
    remark      text(900) comment '公司简介',
    create_by   varchar(64) comment '创建人id',
    create_name varchar(90) comment '创建人姓名',
    create_time datetime comment '创建时间',
    update_by   varchar(64) comment '更新人',
    update_name varchar(90) comment '更新人姓名',
    update_time varchar(64) comment '更新时间',
    deleted     varchar(1) comment '删除标识',
    primary key (id)
) comment = '公司信息';

drop table if exists sys_depart;
create table sys_depart
(
    id          varchar(64)  not null comment '主键',
    name        varchar(90)  not null comment '部门名称',
    code        varchar(32)  not null comment '部门编码',
    node        varchar(32) comment '节点编码',
    order_no    int comment '序号',
    status      varchar(32) comment '组织状态',
    pid         varchar(64) comment '上级组织',
    has_child   varchar(1) comment '是否有下级',
    company     varchar(255) not null comment '归属公司',
    remark      text(900) comment '组织描述',
    create_by   varchar(64) comment '创建人id',
    create_name varchar(90) comment '创建人姓名',
    create_time datetime comment '创建时间',
    update_by   varchar(64) comment '更新人',
    update_name varchar(90) comment '更新人姓名',
    update_time varchar(64) comment '更新时间',
    deleted     varchar(1)   not null comment '删除标识',
    primary key (id)
) comment = '部门管理';

drop table if exists sys_position;
create table sys_position
(
    id          varchar(64) not null comment '主键',
    name        varchar(90) not null comment '岗位名称',
    code        varchar(32) not null comment '编码',
    type        varchar(32) comment '岗位类型',
    order_no    int comment '岗位排序',
    remark      text(900) comment '岗位描述',
    organ       varchar(64) comment '归属组织',
    create_by   varchar(64) comment '创建人id',
    create_name varchar(90) comment '创建人姓名',
    create_time datetime comment '创建时间',
    update_by   varchar(64) comment '更新人',
    update_name varchar(90) comment '更新人姓名',
    update_time varchar(64) comment '更新时间',
    deleted     varchar(1) comment '删除标识',
    primary key (id)
) comment = '岗位信息';

drop table if exists sys_org_user;
create table sys_org_user
(
    id         varchar(64) not null comment '主键',
    user_id    varchar(64) not null comment '用户信息',
    company_id varchar(64) not null comment '公司信息',
    organ_id   varchar(64) comment '组织信息',
    position   varchar(64) comment '岗位信息',
    begin_time datetime    not null comment '开始时间',
    end_time   datetime comment '结束时间',
    active     varchar(1)  not null default 1 comment '是否在职',
    second     varchar(1)  not null default 0 comment '是否借调',
    remark     text(900) comment '工作描述',
    primary key (id)
) comment = '用户-组织关系表';

drop table if exists sys_team;
create table sys_team
(
    id          varchar(64) not null comment '主键',
    name        varchar(90) not null comment '名称',
    code        varchar(32) not null comment '编码',
    type        varchar(32) not null comment '工作组类型',
    begin_time  datetime    not null comment '开始时间',
    end_time    datetime comment '结束时间',
    remark      text(900) comment '职责描述',
    org_code    varchar(64) comment '归属组织',
    create_by   varchar(64) comment '创建人id',
    create_name varchar(90) comment '创建人姓名',
    create_time datetime comment '创建时间',
    update_by   varchar(64) comment '更新人',
    update_name varchar(90) comment '更新人姓名',
    update_time varchar(64) comment '更新时间',
    deleted     varchar(1) comment '删除标识',
    primary key (id)
) comment = '工作组';

drop table if exists sys_team_user;
create table sys_team_user
(
    id         varchar(64) not null comment '主键',
    team_id    varchar(64) not null comment '工作组信息',
    user_id    varchar(64) not null comment '用户信息',
    begin_time datetime    not null comment '加入时间',
    end_time   datetime comment '结束时间',
    active     varchar(1) comment '是否在组',
    remark     text(900) comment '工作描述',
    primary key (id)
) comment = '用户-工作组信息';

drop table if exists sys_user;
create table sys_user
(
    id          varchar(64) not null comment '主键',
    username    varchar(32) not null comment '账号',
    name        varchar(90) not null comment '姓名',
    sex         varchar(32) comment '性别',
    work_no     varchar(32) comment '工号',
    card_no     varchar(32) comment '证件号码',
    phone       varchar(32) comment '手机号码',
    email       varchar(255) comment '邮箱',
    birthday    datetime comment '生日',
    password    varchar(64) not null comment '密码',
    salt        varchar(64) not null comment '加密盐',
    avatar      varchar(255) comment '头像',
    status      varchar(32) not null comment '用户状态',
    create_by   varchar(64) comment '创建人',
    create_name varchar(255) comment '创建人姓名',
    create_time datetime comment '创建时间',
    update_by   varchar(64) comment '更新人',
    update_name varchar(255) comment '更新人姓名',
    update_time datetime comment '更新时间',
    deleted     varchar(1) comment '删除标识',
    primary key (id)
) comment = '用户管理';

drop table if exists sys_user_contact;
create table sys_user_contact
(
    id           varchar(64) not null comment '主键',
    user_id      varchar(64) not null comment '归属用户',
    contact_type varchar(32) comment '联系类型',
    concact_no   varchar(32) comment '联系号码',
    status       varchar(32) comment '状态',
    primary key (id)
) comment = '用户管理-通讯信息';

drop table if exists sys_user_addr;
create table sys_user_addr
(
    id        varchar(64) not null comment '主键',
    user_id   varchar(64) not null comment '关联用户',
    addr_type varchar(32) not null comment '地址类型',
    province  varchar(32) comment '归属省份',
    city      varchar(32) comment '归属城市',
    county    varchar(32) comment '归属区县',
    street    varchar(32) comment '归属街道',
    addr_info text(900) comment '详细地址',
    primary key (id)
) comment = '用户地址信息';

drop table if exists sys_user_card;
create table sys_user_card
(
    id         varchar(64) not null comment '主键',
    user_id    varchar(64) not null comment '归属用户',
    card_type  varchar(32) not null comment '证件类型',
    card_no    varchar(255) comment '证件号码',
    begin_time datetime    not null comment '开始时间',
    end_time   datetime comment '结束时间',
    status     varchar(1)  not null comment '是否有效',
    primary key (id)
) comment = '用户管理-证件信息';

drop table if exists sys_user_mail;
create table sys_user_mail
(
    id        varchar(64) not null comment '主键',
    user_id   varchar(64) comment '用户信息',
    mail_addr varchar(255) comment '邮箱地址',
    smtp_addr varchar(255) comment 'smtp服务器地址',
    smtp_user varchar(90) comment 'smtp登录用户名',
    smtp_pass varchar(64) comment 'smtp登录密码',
    primary key (id)
) comment = '用户邮箱配置';

drop table if exists sys_user_website;
create table sys_user_website
(
    id           varchar(64) not null comment 'id',
    user_id      varchar(64) comment '用户信息',
    name         varchar(90) comment '网址名称',
    addr         varchar(255) comment '访问地址',
    sort         int comment '排序',
    public_flag  varchar(1) comment '是否公用网址',
    create_by   varchar(64) comment '创建人',
    create_name varchar(90) comment '创建人名称',
    create_time datetime comment '创建时间',
    update_by   varchar(64) comment '更新人',
    update_name varchar(90) comment '更新人名称',
    update_time datetime comment '更新时间',
    deleted      varchar(1) comment '删除标识',
    primary key (id)
) comment = '常用网址';