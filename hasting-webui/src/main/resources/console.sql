create table app_info (
    id bigint primary key auto_increment,
    app_name varchar(50) unique not null,
    app_desc varchar(400) default null,
    app_owner varchar(50) default null,
    app_owner_email varchar(50) default null
) comment 'app baisic info';

create table host_info (
    id bigint primary key auto_increment,
    app_id bigint not null,
    host_ip varchar(50) not null,
    host_port int default 0,
    weight int default 100,
    want_weight int default 100,
    token varchar(32) default "null",
    host_status tinyint default 0,
    up_time bigint default 0,
    unique key app_host_idx(app_id,host_ip)
) comment 'host info';

create table service_consumer_info (
    id bigint primary key auto_increment,
    service_id bigint not null,
    service_app_id bigint not null,
    consumer_app_id bigint not null,
    consumer_host_id bigint not null,
    consumer_host_uptime bigint default 0,
    unique key app_service_consume_app_host(service_app_id,service_id,consumer_app_id,consumer_host_id)
) comment 'service consumer info';

create table service_info (
    id bigint primary key auto_increment,
    service_name varchar(100) not null,
    service_version varchar(20) not null,
    service_group varchar(50) not null,
    service_impl varchar(100) not null,
    app_id bigint not null,
    service_status tinyint default 0,
    provider_count int default 0,
    consumer_count int default 0,
    unique key app_group_service_version(app_id,service_group,service_name,service_version)
) comment 'service info';

create table service_provider_info (
    id bigint primary key auto_increment,
    service_id bigint not null,
    app_id bigint not null,
    host_id bigint not null,
    provider_host_uptime bigint default 0,
    unique key service_app_host(service_id,app_id,host_id)
) comment 'service provider';

alter table app_info add column limit_sync_status tinyint default 0;
alter table app_info add column limit_count int default 0;
alter table app_info add column limit_sync_time bigint default 0;

create table limit_info (
    id bigint primary key auto_increment,
    app_id bigint not null,
    limit_type tinyint not null,
    limit_application_id bigint default 0,
    limit_service varchar(100) default null,
    limit_service_method varchar(100) default null,
    limit_ttl int default 10000,
    limit_count int default 10,
    update_time bigint default 0,
    key appIdx(app_id)
) comment 'limit info';