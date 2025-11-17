CREATE TABLE `users` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255),
  `surname` varchar(255),
  `email` varchar(255) UNIQUE,
  `password` varchar(255),
  `status` varchar(255)
);

CREATE TABLE `roles` (
  `user_id` bigint PRIMARY KEY,
  `role` varchar(255)
);

CREATE TABLE `password_reset_token` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `token` varchar(255),
  `user_id` bigint,
  `expiry_date` datetime
);

