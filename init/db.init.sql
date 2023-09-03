CREATE TABLE bank
(
    id   SERIAL NOT NULL,
    bank_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_bank PRIMARY KEY (id)
);

CREATE TABLE  app_user
(
    id         SERIAL NOT NULL,
    firstName  VARCHAR(255) NOT NULL,
    lastName   VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE account
(
    id             SERIAL NOT NULL,
    account_number BIGINT NOT NULL,
    card_number    VARCHAR(255) NOT NULL,
    open_date      date NOT NULL,
    balance        numeric (20,5) NOT NULL,
    currency       VARCHAR(5) NOT NULL,
    bank_id        BIGINT NOT NULL,
    app_user_id    BIGINT NOT NULL,
    CONSTRAINT pk_account PRIMARY KEY (id)
);

ALTER TABLE account
    ADD CONSTRAINT FK_ACCOUNT_ON_BANK FOREIGN KEY (bank_id) REFERENCES bank (id);

ALTER TABLE account
    ADD CONSTRAINT FK_ACCOUNT_ON_USER FOREIGN KEY (app_user_id) REFERENCES app_user (id);

CREATE TABLE bank_transaction
(
    id                    SERIAL NOT NULL,
    amount                numeric (20,5) NOT NULL,
    transaction_timestamp TIMESTAMP(3) NOT NULL,
    transaction_type      VARCHAR(255) NOT NULL,
    account_id            BIGINT NOT NULL,
    CONSTRAINT pk_transaction PRIMARY KEY (id)
);

ALTER TABLE bank_transaction
    ADD CONSTRAINT FK_TRANSACTION_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

CREATE TABLE shedlock (
    id         BIGINT NOT NULL,
    lock_until TIMESTAMP(3) NOT NULL,
    locked_at  TIMESTAMP(3) NOT NULL,
    CONSTRAINT pk_shedlock PRIMARY KEY (id)
);

ALTER TABLE shedlock
    ADD CONSTRAINT FK_SHEDLOCK_ON_ACCOUNT FOREIGN KEY (id) REFERENCES account (id);

INSERT INTO bank (id, bank_name)
VALUES ('1', 'Clever-Bank'),
       ('2', 'Sber-Bank'),
       ('3', 'Alfa-Bank'),
       ('4', 'Paritet-Bank'),
       ('5', 'Tinkoff-Bank');

INSERT INTO app_user (id, firstName, patronymic, lastName)
VALUES ('1', 'Иван', 'Иванович', 'Иванов'),
       ('2', 'Петр', 'Петрович', 'Петров'),
       ('3', 'Сергей', 'Сергеевич', 'Сергеев'),
       ('4', 'Егор', 'Егорович', 'Егоров'),
       ('5', 'Алексей', 'Алексеевич', 'Алексеев'),
       ('6', 'Игорь', 'Игоревич', 'Игорев'),
       ('7', 'Семен', 'Семенович', 'Семенов'),
       ('8', 'Роман', 'Романович', 'Романов'),
       ('9', 'Тимофей', 'Тимофеевич', 'Тимофеев'),
       ('10', 'Денис', 'Денисович', 'Денисов'),
       ('11', 'Федор', 'Федорович', 'Федоров'),
       ('12', 'Артем', 'Артемович', 'Артемов'),
       ('13', 'Павел', 'Павлович', 'Павлов'),
       ('14', 'Олег', 'Олегович', 'Олегов'),
       ('15', 'Марк', 'Маркович', 'Марков'),
       ('16', 'Николай', 'Николаевич', 'Николаев'),
       ('17', 'Вадим', 'Вадимович', 'Вадимов'),
       ('18', 'Руслан', 'Русланович', 'Русланов'),
       ('19', 'Тимур', 'Тимурович', 'Тимуров'),
       ('20', 'Борис', 'Борисович', 'Борисов'),
       ('21', 'Глеб', 'Глебович', 'Глебов');

INSERT INTO account (id, account_number, card_number, open_date, balance, currency, bank_id, app_user_id)
VALUES ('1', '1235648975', 'AS16 ASGD 1300 2134 ASDA 345J 2123', '2017-03-14', '25000', 'BYN', '1', '1'),
       ('2', '4329383292', 'BF21 GFRR 2354 5763 JJDY 568K 3215', '2018-08-23', '15000', 'USD', '3', '1'),
       ('3', '7694927649', 'KU65 DETT 3324 6543 HFDA 855G 6342', '2019-12-04', '25000', 'BYN', '2', '2'),
       ('4', '8645654613', 'KK85 LFJU 8946 6546 KHGF 432P 5522', '2020-10-09', '15000', 'EUR', '4', '2'),
       ('5', '6325452852', 'FG99 QWER 1234 7854 DSAE 123A 8946', '2021-11-21', '25000', 'BYN', '1', '3'),
       ('6', '5647277843', 'AS55 WERT 2345 1373 FDSA 321S 1234', '2022-04-15', '15000', 'USD', '2', '3'),
       ('7', '9768654354', 'JG69 ERTY 3456 3754 GFDS 212D 2345', '2021-02-20', '25000', 'BYN', '3', '4'),
       ('8', '8974867576', 'BG87 RTYU 4567 2774 HGFD 232F 3456', '2022-03-01', '15000', 'EUR', '4', '4'),
       ('9', '4562552555', 'AB98 TYUI 5678 3345 JHGF 545G 4567', '2019-04-02', '25000', 'BYN', '1', '5'),
       ('10', '2573755211', 'QW12 YUIO 6789 2347 KJHG 565H 5678', '2018-05-03', '15000', 'USD', '2', '5'),
       ('11', '8676786786', 'WE23 UIOP 4758 5378 LKJH 878J 6789', '2020-06-04', '25000', 'BYN', '3', '6'),
       ('12', '6545787334', 'ER34 IOPA 7485 3477 ZLKJ 565K 4758', '2019-07-05', '15000', 'EUR', '4', '6'),
       ('13', '1233433222', 'RT45 OPAS 5869 6756 XZLK 456L 7485', '2021-08-06', '25000', 'BYN', '1', '7'),
       ('14', '7545646546', 'TY56 PASD 8596 1578 CXZL 654Z 5869', '2022-09-07', '15000', 'USD', '2', '7'),
       ('15', '6543245675', 'YU67 ASDF 1425 3576 VCXZ 987X 8596', '2021-10-08', '25000', 'BYN', '3', '8'),
       ('16', '8745632146', 'UI78 SDFG 2536 6655 BVCX 789C 1425', '2020-11-09', '15000', 'EUR', '4', '8'),
       ('17', '8675778666', 'IO89 DFGH 4152 9966 NBVC 741V 2536', '2019-12-10', '25000', 'BYN', '2', '9'),
       ('18', '6434234232', 'OP91 FGHJ 5263 7788 MNBV 147B 4152', '2021-01-11', '15000', 'USD', '4', '9'),
       ('19', '9878967867', 'PA98 GHJK 9865 1155 VBNM 414N 5263', '2022-02-12', '25000', 'BYN', '3', '10'),
       ('20', '7743265743', 'AS74 HJKL 8754 3265 CVBN 474M 9865', '2019-03-13', '15000', 'EUR', '1', '10'),
       ('21', '2255557777', 'SD41 JKLZ 7845 2356 XCVB 852Q 8754', '2020-04-14', '25000', 'BYN', '2', '11'),
       ('22', '3333655777', 'DF85 KLZX 8956 2154 ZXCV 258W 7845', '2021-05-15', '15000', 'USD', '3', '11'),
       ('23', '9076655555', 'FG52 LZXC 4512 2130 KLZX 525E 8956', '2022-06-16', '25000', 'BYN', '4', '12'),
       ('24', '6975656666', 'GH96 ZXCV 2154 4512 JKLZ 585R 4512', '2021-07-17', '15000', 'EUR', '2', '12'),
       ('25', '1236547789', 'HJ63 XCVB 2356 8956 HJKL 963T 2154', '2018-08-18', '25000', 'BYN', '1', '13'),
       ('26', '3456883222', 'JK36 CVBN 3265 7845 GHJK 369Y 2356', '2022-09-19', '15000', 'USD', '3', '13'),
       ('27', '8765454888', 'KL69 VBNM 1155 8754 FGHJ 636U 3265', '2021-10-20', '25000', 'BYN', '4', '14'),
       ('28', '1456757624', 'LZ25 MNBV 7788 9865 DFGH 696I 1155', '2019-11-21', '15000', 'EUR', '1', '14'),
       ('29', '9856764346', 'ZX58 NBVC 9966 5263 SDFG 951O 7788', '2022-12-22', '25000', 'BYN', '2', '15'),
       ('30', '8975743566', 'XC14 BVCX 6655 4152 ASDF 159P 9966', '2021-01-23', '15000', 'USD', '3', '15'),
       ('31', '7654345654', 'CV47 VCXZ 3576 2536 PASD 753S 6655', '2019-02-24', '25000', 'BYN', '4', '16'),
       ('32', '6455753275', 'VB75 CXZL 1578 1425 GFRR 357D 3576', '2020-03-24', '15000', 'EUR', '1', '16'),
       ('33', '1236525424', 'BN53 XZLK 6756 8596 LFJU 535F 1425', '2021-04-26', '25000', 'BYN', '2', '17'),
       ('34', '8632458444', 'NM26 ZLKJ 3477 5869 QWER 575G 8596', '2022-05-27', '15000', 'USD', '4', '17'),
       ('35', '1268427457', 'MT64 LKJH 5378 7485 WERT 757H 3477', '2020-06-28', '25000', 'BYN', '3', '18'),
       ('36', '3454334534', 'TK59 KJHG 2347 4758 ERTY 735J 5378', '2019-07-01', '15000', 'EUR', '1', '18'),
       ('37', '8299393939', 'HS49 JHGF 3345 6789 RTYU 195K 2347', '2020-08-02', '25000', 'BYN', '2', '19'),
       ('38', '2365452326', 'FY11 HGFD 2774 5678 TYUO 497L 3345', '2021-09-03', '15000', 'USD', '3', '19'),
       ('39', '3523526659', 'KQ96 GFDS 3754 4567 YUIO 658Z 2774', '2022-10-04', '25000', 'BYN', '4', '20'),
       ('40', '5325235222', 'DI49 FDSA 1373 3456 UIOP 483X 3754', '2021-11-05', '15000', 'EUR', '1', '20'),
       ('41', '9659695594', 'GT44 DSAE 7854 2345 IOPA 183C 1373', '2020-12-06', '25000', 'BYN', '2', '21');
