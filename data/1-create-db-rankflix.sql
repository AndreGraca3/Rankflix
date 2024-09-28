drop table if exists rank_group_watchlist;
drop table if exists rank_group_watch_status;
drop table if exists rank_group_member;
drop table if exists rank_group_media;
drop table if exists rank_group;
drop table if exists review;
drop table if exists media;
drop table if exists refresh_token;
drop table if exists "user";

create table "user"
(
    id         int primary key identity (1,1),
    email      varchar(100) not null unique,
    username   varchar(100) not null unique,
    avatar_url varchar(255),
    created_at datetime     not null default current_timestamp,
);

create table refresh_token
(
    value      uniqueidentifier primary key default newid(),
    created_at datetime not null            default current_timestamp,
    user_id    int      not null references "user" (id),
);

-- Indexes search for access token by user id
create index idx_access_token_user_id
    on refresh_token (user_id);

create table media
(
    tmdb_id int primary key,
    title   varchar(100) not null,
    type    varchar(10)  not null check (type in ('movie', 'tv'))
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

create index idx_review_media_id
    on review (media_id);

create table rank_group
(
    id       int primary key identity (1,1),
    name     varchar(50) not null,
    owner_id int         not null references "user" (id),
);

create table rank_group_media
(
    media_id int      not null references media (tmdb_id),
    group_id int      not null references rank_group (id),
    added_at datetime not null default current_timestamp,
    primary key (media_id, group_id),
);

create table rank_group_member
(
    group_id int not null references rank_group (id),
    user_id  int not null references "user" (id),
    primary key (group_id, user_id),
);

create table rank_group_watch_status
(
    media_id int not null references media (tmdb_id),
    group_id int not null references rank_group (id),
    user_id  int not null references "user" (id),
    primary key (media_id, group_id, user_id),
);

create table rank_group_watchlist
(
    media_id int      not null references media (tmdb_id),
    group_id int      not null references rank_group (id),
    added_by int      not null references "user" (id),
    added_at datetime not null default current_timestamp,
    primary key (media_id, group_id),
);