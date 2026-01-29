CREATE DATABASE VIDEOGAME_STORE;


-- Insert a regular user
INSERT INTO PROFILE_ (USERNAME, PASSWORD, EMAIL, USER_CODE, NAME, TELEPHONE, SURNAME)
VALUES ('user1', '1234', 'gamer123@example.com', 1, 'Alex', '612345678', 'Gamer');

INSERT INTO USER_ (USER_CODE, GENDER, CARD_NUMBER)
VALUES ('1', 'other', 'ES1234567890123456789012');

-- Insert an admin
INSERT INTO PROFILE_ (USERNAME, PASSWORD, EMAIL, USER_CODE, NAME, TELEPHONE, SURNAME)
VALUES ('admin1', '1234', 'admin1@example.com', 107, 'Admin', '600000001', 'System');

INSERT INTO ADMIN_ (USER_CODE, CURRENT_ACCOUNT)
VALUES ('1', '12312312455');

-- Insert sample data with proper PEGI values
INSERT INTO VIDEOGAME_ (company_name, genre, name, platform, pegi, price, stock, release_date) VALUES
('Nintendo', 'ADVENTURE', 'The Legend of Zelda: Breath of the Wild', 'SWITCH', 'PEGI12', 59.99, 50, '2017-03-03'),
('Rockstar Games', 'ACTION', 'Red Dead Redemption 2', 'PS4', 'PEGI18', 49.99, 30, '2018-10-26'),
('CD Projekt Red', 'RPG', 'Cyberpunk 2077', 'PC', 'PEGI18', 39.99, 40, '2020-12-10'),
('Mojang Studios', 'ADVENTURE', 'Minecraft', 'PC', 'PEGI7', 19.99, 100, '2011-11-18'),
('FromSoftware', 'RPG', 'Elden Ring', 'PS5', 'PEGI16', 59.99, 25, '2022-02-25'),
('Nintendo', 'ADVENTURE', 'Super Mario Odyssey', 'SWITCH', 'PEGI7', 49.99, 35, '2017-10-27'),
('Rockstar Games', 'ACTION', 'Grand Theft Auto V', 'XBOX_ONE', 'PEGI18', 29.99, 45, '2013-09-17'),
('Square Enix', 'RPG', 'Final Fantasy VII Remake', 'PS5', 'PEGI16', 69.99, 20, '2020-04-10'),
('Capcom', 'HORROR', 'Resident Evil Village', 'XBOX_SERIES', 'PEGI18', 49.99, 15, '2021-05-07'),
('Nintendo', 'ADVENTURE', 'Animal Crossing: New Horizons', 'SWITCH', 'PEGI3', 49.99, 60, '2020-03-20');