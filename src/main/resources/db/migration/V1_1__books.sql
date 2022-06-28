CREATE TABLE IF NOT EXISTS books (
    id int auto_increment not null,
    title varchar,
    writter varchar,
    publisher varchar,
    price int,
    PRIMARY KEY(id)
);
INSERT INTO books(id, title, writter, publisher, price) VALUES (4, 'タイトル', '著者', '出版社', 300);
