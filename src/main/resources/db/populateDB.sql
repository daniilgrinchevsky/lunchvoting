DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM dishes;
DELETE FROM restaurants;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO restaurants (name) VALUES
  ('Penthouse'),
  ('Maybeer'),
  ('Lounge');

INSERT INTO users (name, email, password, restaurant_id) VALUES
  ('User', 'user@gmail.com', '{noop}password', 100000),
  ('Admin', 'admin@gmail.com', '{noop}admin', 100001);

INSERT INTO user_roles(role, user_id) VALUES
  ('ROLE_USER', 100003),
  ('ROLE_USER', 100004),
  ('ROLE_ADMIN', 100004);

INSERT INTO dishes (name, price, restaurant_id) VALUES
  ('Tiramisu', 220, 100000),
  ('Caesar', 490, 100000),
  ('Greek salad', 250, 100001),
  ('Tacos', 200, 100002),
  ('Fish and chips', 310, 100001),
  ('Lasagna', 550, 100002),
  ('Tom yum',470, 100002),
  ('Hamburger', 300, 100001),
  ('Neapolitan pizza', 450, 100000);
