create table if not exists "id"
(
    "id" bigserial primary key not null unique
);

create table if not exists "base"
(
    "created_at"  timestamp not null default current_timestamp,
    "updated_at"  timestamp,
    "deleted_at"  timestamp,
    "deleted"     boolean   not null default false,
    "description" varchar(500),
    primary key ("id")
) inherits ("id");

create table if not exists "users"
(
    "first_name" varchar(50),
    "last_name"  varchar(50),
    "email"      varchar(100) not null unique,
    "password"   varchar(100) not null,
    primary key ("id")
) inherits ("base");


insert into "users" ("first_name", "last_name", "email", "password")
VALUES ('Bardia', 'Namjoo', 'bardiademon@gmail.com', '123456');

