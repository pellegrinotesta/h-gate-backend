INSERT INTO `users` (`id`, `name`, `surname`, `email`, `password`, `status`) VALUES
(1, 'Mario', 'Rossi', 'mario.rossi@example.com', '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.', 'ATTIVO'),
(2, 'Luigi', 'Bianchi', 'luigi.bianchi@example.com', '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.', 'ATTIVO');

INSERT INTO `roles` (`user_id`, `role`) VALUES
(1, 'STAFF'),
(2, 'ADMIN');

INSERT INTO `password_reset_token` (`id`, `token`, `user_id`, `expiry_date`) VALUES
(1, 'reset-token-123', 1, DATE_ADD(NOW(), INTERVAL 1 DAY));
