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

-- medici / specializzazioni
CREATE TABLE doctors (
  id uuid PRIMARY KEY REFERENCES users(id),
  specialty text,
  workplace text
);

-- appuntamenti
CREATE TABLE appointments (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  patient_id uuid REFERENCES users(id),
  doctor_id uuid REFERENCES doctors(id),
  start_time timestamptz NOT NULL,
  end_time timestamptz NOT NULL,
  status varchar(20) NOT NULL, -- 'booked','cancelled','completed'
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

-- referti metadata
CREATE TABLE reports (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  patient_id uuid REFERENCES users(id),
  doctor_id uuid REFERENCES users(id),
  s3_key text NOT NULL,
  type varchar(50),
  uploaded_at timestamptz DEFAULT now(),
  access_restricted boolean DEFAULT true
);

-- audit log
CREATE TABLE audit_logs (
  id bigserial PRIMARY KEY,
  user_id uuid,
  action text,
  resource_type text,
  resource_id text,
  timestamp timestamptz DEFAULT now(),
  details jsonb
);

