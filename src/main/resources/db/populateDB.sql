DELETE
FROM VOTE;
DELETE
FROM DISH;
DELETE
FROM RESTAURANT;
DELETE
FROM MENU;
DELETE
FROM USERS;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO USERS (NAME, PASSWORD, EMAIL, REGISTERED)
VALUES ('User', '{noop}user_password', 'user@mail.com'),
       ('Admin', '{noop}admin_password', 'admin@mail.com');

INSERT INTO user_roles (user_id, role)
VALUES (100000, 'ROLE_USER'),
       (100001, 'ROLE_ADMIN');

INSERT INTO RESTAURANT (NAME)
VALUES ('Room'),
       ('Pizza'),
       ('McDonalds');

INSERT INTO MENU (MENU_DATE, RESTAURANT_ID)
VALUES ('2021-04-23', 100002),
       ('2021-04-25', 100003),
       (current_date, 100003),
       (current_date, 100004),
       ('2021-05-12', 100002);

INSERT INTO DISH (NAME, PRICE, MENU_ID)
VALUES ('Hamburger', 100000, 100005),
       ('Steak', 10000, 100006),
       ('Bugs', 20000, 100007),
       ('BigSteak', 11100, 100007),
       ('BigVine', 22200, 100007),
       ('Takoburger', 33300, 100008);

INSERT INTO VOTE (VOTE_DATE, USER_ID, MENU_ID)
VALUES ('2021-04-23', 100000, 100006),
       ('2021-04-23', 100001, 100006),
       (current_date, 100000, 100007);