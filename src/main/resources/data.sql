use easysobi;

insert into inventory(inventory_name)
values ('예혁의 인벤토리'),
       ('유진의 인벤토리');

insert into user(email, nickname, kakao_id)
values ('abc@naver.com', '방예혁', 123912),
       ('ggg@naver.com', '김유진', 122112);

insert into user_inventory(user_id, inventory_id)
values (1, 1),
       (1, 2),
       (2, 2);

insert into item(name, category, count, mfg_date, inventory_id)
values ('서울우유', 1, 2, null, 1),
       ('단백질 쉐이크', 2, 4, null, 2),
       ('딸기', 2, 4, null, 2),
       ('바나나', 3, 1, null, 1),
       ('고구마', 3, 2, null, 1);