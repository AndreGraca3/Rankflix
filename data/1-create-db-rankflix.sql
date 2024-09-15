drop table if exists rank_list_watch_status;
drop table if exists rank_list_member;
drop table if exists rank_list_media;
drop table if exists rank_list;
drop table if exists review;
drop table if exists media;
drop table if exists access_token;
drop table if exists "user";

create table "user"
(
    id         int primary key identity (1,1),
    email      varchar(100) not null,
    username   varchar(100) not null,
    avatar_url varchar(255),
    created_at datetime     not null default current_timestamp,
);

create table access_token
(
    value      uniqueidentifier primary key default newid(),
    created_at datetime not null            default current_timestamp,
    user_id    int      not null references "user" (id),
);

-- Indexes search for access token by user id
create index idx_access_token_user_id
    on access_token (user_id);

create table media
(
    tmdb_id int primary key,
    title   varchar(100) not null,
    type    varchar(10)  not null check (type in ('movie', 'tv')),
);

create table review
(
    id         uniqueidentifier primary key default newid(),
    rating     float        not null,
    comment    varchar(255) not null,
    created_at datetime     not null        default current_timestamp,
    media_id   int          not null references media (tmdb_id),
    user_id    int          not null references "user" (id),
);

create table rank_list
(
    id   int primary key identity (1,1),
    name varchar(50) not null,
);

create table rank_list_media
(
    media_id int      not null references media (tmdb_id),
    list_id  int      not null references rank_list (id),
    added_at datetime not null default current_timestamp,
    primary key (media_id, list_id),
);

create table rank_list_member
(
    list_id  int not null references rank_list (id),
    user_id  int not null references "user" (id),
    is_owner bit not null,
    primary key (list_id, user_id),
);

create table rank_list_watch_status
(
    media_id int not null references media (tmdb_id),
    list_id  int not null references rank_list (id),
    user_id  int not null references "user" (id),
    primary key (media_id, list_id, user_id),
);