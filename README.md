## 스프링부트 게시판
- mysql, lombok, springboot, jpa
- [참고 자료](https://www.youtube.com/watch?v=YshcPPHClR4&list=PLV9zd3otBRt7jmXvwCkmvJ8dH5tR_20c0&ab_channel=%EC%BD%94%EB%94%A9%EB%A0%88%EC%8B%9C%ED%94%BC)

```mysql
create table board_table
(
id             bigint auto_increment primary key,
created_time   datetime     null,
updated_time   datetime     null,
board_contents varchar(500) null,
board_hits     int          null,
board_pass     varchar(255) null,
board_title    varchar(255) null,
board_writer   varchar(20)  not null,
file_attached  int          null
);

create table board_file_table
(
id                 bigint auto_increment primary key,
created_time       datetime     null,
updated_time       datetime     null,
original_file_name varchar(255) null,
stored_file_name   varchar(255) null,
board_id           bigint       null,
constraint FKcfxqly70ddd02xbou0jxgh4o3
    foreign key (board_id) references board_table (id) on delete cascade
);

```