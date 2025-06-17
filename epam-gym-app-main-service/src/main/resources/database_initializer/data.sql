INSERT INTO training_types (training_type_id, description) VALUES('75250412-9beb-417d-a087-4fb2692d39b5','STRENGTH_TRAINING');
INSERT INTO training_types (training_type_id, description) VALUES('b7c9b5d0-1a9c-4ea0-82f7-559a9a3c4283','CARDIOVASCULAR_TRAINING');
INSERT INTO training_types (training_type_id, description) VALUES('1eb6f8c6-fdea-498c-a7c1-23772becb007','HYPERTROPHY_TRAINING');
INSERT INTO training_types (training_type_id, description) VALUES('3a7cf559-d2c7-4286-8ea3-13492a4dbc77','FUNCTIONAL_TRAINING');
INSERT INTO training_types (training_type_id, description) VALUES('2dadf7cd-e5aa-4ec2-9f21-f84fe1f62939','FLEXIBILITY');
INSERT INTO training_types (training_type_id, description) VALUES ('d1c3ed9a-e0a7-4fcc-8829-f32f5d701d10', 'HIIT');
INSERT INTO training_types (training_type_id, description) VALUES ('5e8d317b-aae7-4ba6-9729-57e3b8c617ef', 'YOGA');

-- Insert Users
INSERT INTO users (user_id, firstname, lastname, username, role, password, is_active) VALUES ('a1b2c3d4-e5f6-4a1b-8c1d-1a2b3c4d5e6f', 'John', 'Jose', 'john.jose', 'TRAINEE', /*  password  */'$2a$10$aeG.8A9AWkFf7Pocf26ntebmEm/Ps1XgS4f39MOwPhhJB9JVdSVT2',true);
INSERT INTO users (user_id, firstname, lastname, username, role, password, is_active) VALUES ('b2c3d4e5-f6a1-4b2c-9d1e-2a3b4c5d6e7f', 'Jane', 'Smith', 'jane.smith', 'TRAINEE', /* password1 */'$2a$10$8iLKAfT/ChG659cxTMqqoO9G7AVhBuA44EoxljS.UwEgYckYsA7Qi', true);
INSERT INTO users (user_id, firstname, lastname, username, role, password, is_active) VALUES ('c3d4e5f6-a1b2-4c3d-0e1f-3a4b5c6d7e8f', 'Michael', 'Johnson', 'michael.johnson','TRAINEE',  /* password2 */'$2a$10$W9eGzvxxzrQFi3ot4xOiBei5LHZo8b8pYBUmZGwOUsaPQSKdt4NRy', true);
INSERT INTO users (user_id, firstname, lastname, username, role, password, is_active) VALUES ('d4e5f6a1-b2c3-4d4e-1f1a-4a5b6c7d8e9f', 'Emma', 'Williams', 'emma.williams','TRAINEE',  /* password3 */'$2a$10$kAs3SP2FFkQVpmmeP2ZRyeYdL8LT6.Pg67/N07kPPg8RTwbAN3Ovq', true);

INSERT INTO users (user_id, firstname, lastname, username, role, password, is_active) VALUES ('e5f6a1b2-c3d4-4e5f-2a1b-5a6b7c8d9e0f', 'Robert', 'Brown', 'robert.brown', 'TRAINER',  /* password4 */'$2a$10$5dvPjErUFDfgQzyWkNx1NesxWeplYFqbt/X3Cy3tuoxNKX6Qb4VOi', true);
INSERT INTO users (user_id, firstname, lastname, username, role, password, is_active) VALUES ('f6a1b2c3-d4e5-4f6a-3b1c-6a7b8c9d0e1f', 'Sarah', 'Davis', 'sarah.davis','TRAINER',  /* password5 */ '$2a$10$mlpJ5p03GL1FWBpmyGYhrOwEloNrS0Mfsm3MIydA.TLvgB5rFlNd.', true);
INSERT INTO users (user_id, firstname, lastname, username, role, password, is_active) VALUES ('1a2b3c4d-5e6f-4a1b-4c1d-7a8b9c0d1e2f', 'David', 'Miller', 'david.miller', 'TRAINER',  /* password6 */'$2a$10$UNqmPYdSNj.S9BKGqWGjQeiMkU3f8FTrMQ41/FTpuTvQxT6LaI.RO', true);
INSERT INTO users (user_id, firstname, lastname, username, role, password, is_active) VALUES ('2b3c4d5e-6f1a-4b2c-5d1e-8a9b0c1d2e3f', 'Lisa', 'Wilson', 'lisa.wilson', 'TRAINER',  /* password7 */'$2a$10$ZiP24r6o6quAafK9IsxNq.0SdUXSU.nrwLGf7HqiCH9pOTNNmQBGS', true);

INSERT INTO users (user_id, firstname, lastname, username, role, password, is_active) VALUES ('3c4d5e6f-7a8b-4c3d-9e0f-9a0b1c2d3e4f', 'James', 'Taylor', 'james.taylor', 'TRAINER',  /* password8 */'$2a$10$n1tMfyV.zH4Lvg0E7w2JI.Z7mqSQLtdvV0dSSC0NxL1dvdOHfgFti', true);
INSERT INTO users (user_id, firstname, lastname, username, role, password, is_active) VALUES ('4d5e6f7a-8b9c-4d5e-0f1a-0b1c2d3e4f5a', 'Emily', 'Anderson', 'emily.anderson', 'TRAINER',  /* password9 */'$2a$10$INqPVTSSeVH29hcBTW3TNeiE7G7YfNg/ujvviqSpWxw6dFR8WcIl6', true);


INSERT INTO trainees (trainee_id, date_of_birth, address, user_id) VALUES ('1a2b3c4d-5e6f-4a1b-8c1d-1a2b3c4d5e6f', '1990-05-15', '123 Main St, Anytown', 'a1b2c3d4-e5f6-4a1b-8c1d-1a2b3c4d5e6f');
INSERT INTO trainees (trainee_id, date_of_birth, address, user_id) VALUES ('2b3c4d5e-6f1a-4b2c-9d1e-2a3b4c5d6e7f', '1988-08-22', '456 Oak Ave, Somecity', 'b2c3d4e5-f6a1-4b2c-9d1e-2a3b4c5d6e7f');
INSERT INTO trainees (trainee_id, date_of_birth, address, user_id) VALUES ('3c4d5e6f-1a2b-4c3d-0e1f-3a4b5c6d7e8f', '1992-03-10', '789 Pine Rd, Othertown', 'c3d4e5f6-a1b2-4c3d-0e1f-3a4b5c6d7e8f');
INSERT INTO trainees (trainee_id, date_of_birth, address, user_id) VALUES ('4d5e6f1a-2b3c-4d4e-1f1a-4a5b6c7d8e9f', '1995-11-28', '101 Maple Dr, Newville', 'd4e5f6a1-b2c3-4d4e-1f1a-4a5b6c7d8e9f');


INSERT INTO trainers (trainer_id, specialization, user_id) VALUES ('5e6f1a2b-3c4d-4e5f-2a1b-5a6b7c8d9e0f', '75250412-9beb-417d-a087-4fb2692d39b5', 'e5f6a1b2-c3d4-4e5f-2a1b-5a6b7c8d9e0f');
INSERT INTO trainers (trainer_id, specialization, user_id) VALUES ('6f1a2b3c-4d5e-4f6a-3b1c-6a7b8c9d0e1f', 'b7c9b5d0-1a9c-4ea0-82f7-559a9a3c4283', 'f6a1b2c3-d4e5-4f6a-3b1c-6a7b8c9d0e1f');
INSERT INTO trainers (trainer_id, specialization, user_id) VALUES ('7a1b2c3d-4e5f-4a1b-4c1d-7a8b9c0d1e2f', 'd1c3ed9a-e0a7-4fcc-8829-f32f5d701d10', '1a2b3c4d-5e6f-4a1b-4c1d-7a8b9c0d1e2f');
INSERT INTO trainers (trainer_id, specialization, user_id) VALUES ('8b2c3d4e-5f6a-4b2c-5d1e-8a9b0c1d2e3f', '5e8d317b-aae7-4ba6-9729-57e3b8c617ef', '2b3c4d5e-6f1a-4b2c-5d1e-8a9b0c1d2e3f');
INSERT INTO trainers (trainer_id, specialization, user_id) VALUES ('9c3d4e5f-6a7b-4c3d-9e0f-9a0b1c2d3e4f', '3a7cf559-d2c7-4286-8ea3-13492a4dbc77', '3c4d5e6f-7a8b-4c3d-9e0f-9a0b1c2d3e4f');
INSERT INTO trainers (trainer_id, specialization, user_id) VALUES ('0d4e5f6a-7b8c-4d5e-0f1a-0b1c2d3e4f5a', 'b7c9b5d0-1a9c-4ea0-82f7-559a9a3c4283', '4d5e6f7a-8b9c-4d5e-0f1a-0b1c2d3e4f5a');


INSERT INTO trainer_trainee (trainee_id, trainer_id) VALUES ('1a2b3c4d-5e6f-4a1b-8c1d-1a2b3c4d5e6f', '5e6f1a2b-3c4d-4e5f-2a1b-5a6b7c8d9e0f');
INSERT INTO trainer_trainee (trainee_id, trainer_id) VALUES ('1a2b3c4d-5e6f-4a1b-8c1d-1a2b3c4d5e6f', '6f1a2b3c-4d5e-4f6a-3b1c-6a7b8c9d0e1f');
INSERT INTO trainer_trainee (trainee_id, trainer_id) VALUES ('2b3c4d5e-6f1a-4b2c-9d1e-2a3b4c5d6e7f', '6f1a2b3c-4d5e-4f6a-3b1c-6a7b8c9d0e1f');
INSERT INTO trainer_trainee (trainee_id, trainer_id) VALUES ('2b3c4d5e-6f1a-4b2c-9d1e-2a3b4c5d6e7f', '7a1b2c3d-4e5f-4a1b-4c1d-7a8b9c0d1e2f');
INSERT INTO trainer_trainee (trainee_id, trainer_id) VALUES ('3c4d5e6f-1a2b-4c3d-0e1f-3a4b5c6d7e8f', '7a1b2c3d-4e5f-4a1b-4c1d-7a8b9c0d1e2f');
INSERT INTO trainer_trainee (trainee_id, trainer_id) VALUES ('3c4d5e6f-1a2b-4c3d-0e1f-3a4b5c6d7e8f', '8b2c3d4e-5f6a-4b2c-5d1e-8a9b0c1d2e3f');
INSERT INTO trainer_trainee (trainee_id, trainer_id) VALUES ('4d5e6f1a-2b3c-4d4e-1f1a-4a5b6c7d8e9f', '8b2c3d4e-5f6a-4b2c-5d1e-8a9b0c1d2e3f');
INSERT INTO trainer_trainee (trainee_id, trainer_id) VALUES ('4d5e6f1a-2b3c-4d4e-1f1a-4a5b6c7d8e9f', '5e6f1a2b-3c4d-4e5f-2a1b-5a6b7c8d9e0f');


INSERT INTO trainings (training_id, trainee_id, trainer_id, training_name, training_date, training_type_id, training_duration) VALUES ('a9b8c7d6-e5f4-4a9b-8c7d-1a9b8c7d6e5f', '1a2b3c4d-5e6f-4a1b-8c1d-1a2b3c4d5e6f', '5e6f1a2b-3c4d-4e5f-2a1b-5a6b7c8d9e0f', 'Upper Body Focus', '2025-02-01 10:00:00', '75250412-9beb-417d-a087-4fb2692d39b5', 60);
INSERT INTO trainings (training_id, trainee_id, trainer_id, training_name, training_date, training_type_id, training_duration) VALUES ('b8c7d6e5-f4a3-4b8c-7d6e-2b8c7d6e5f4a', '1a2b3c4d-5e6f-4a1b-8c1d-1a2b3c4d5e6f', '6f1a2b3c-4d5e-4f6a-3b1c-6a7b8c9d0e1f', 'Endurance Run', '2025-02-01 14:30:00', 'b7c9b5d0-1a9c-4ea0-82f7-559a9a3c4283', 45);
INSERT INTO trainings (training_id, trainee_id, trainer_id, training_name, training_date, training_type_id, training_duration) VALUES ('c7d6e5f4-a3b2-4c7d-6e5f-3c7d6e5f4a3b', '2b3c4d5e-6f1a-4b2c-9d1e-2a3b4c5d6e7f', '6f1a2b3c-4d5e-4f6a-3b1c-6a7b8c9d0e1f', 'Interval Training', '2025-02-01 09:00:00', 'b7c9b5d0-1a9c-4ea0-82f7-559a9a3c4283', 30);
INSERT INTO trainings (training_id, trainee_id, trainer_id, training_name, training_date, training_type_id, training_duration) VALUES ('d6e5f4a3-b2c1-4d6e-5f4a-4d6e5f4a3b2c', '2b3c4d5e-6f1a-4b2c-9d1e-2a3b4c5d6e7f', '7a1b2c3d-4e5f-4a1b-4c1d-7a8b9c0d1e2f', 'Advanced HIIT', '2025-02-01 16:00:00', 'd1c3ed9a-e0a7-4fcc-8829-f32f5d701d10', 40);
INSERT INTO trainings (training_id, trainee_id, trainer_id, training_name, training_date, training_type_id, training_duration) VALUES ('e5f4a3b2-c1d0-4e5f-4a3b-5e5f4a3b2c1d', '3c4d5e6f-1a2b-4c3d-0e1f-3a4b5c6d7e8f', '7a1b2c3d-4e5f-4a1b-4c1d-7a8b9c0d1e2f', 'Core Blast', '2025-02-01 11:30:00', 'd1c3ed9a-e0a7-4fcc-8829-f32f5d701d10', 50);
INSERT INTO trainings (training_id, trainee_id, trainer_id, training_name, training_date, training_type_id, training_duration) VALUES ('f4a3b2c1-d0e9-4f4a-3b2c-6f4a3b2c1d0e', '3c4d5e6f-1a2b-4c3d-0e1f-3a4b5c6d7e8f', '8b2c3d4e-5f6a-4b2c-5d1e-8a9b0c1d2e3f', 'Power Yoga', '2025-02-01 08:00:00', '5e8d317b-aae7-4ba6-9729-57e3b8c617ef', 75);
INSERT INTO trainings (training_id, trainee_id, trainer_id, training_name, training_date, training_type_id, training_duration) VALUES ('1d2e3f4a-5b6c-41d2-e3f4-71d2e3f4a5b6', '4d5e6f1a-2b3c-4d4e-1f1a-4a5b6c7d8e9f', '8b2c3d4e-5f6a-4b2c-5d1e-8a9b0c1d2e3f', 'Relaxation Yoga', '2025-02-01 17:00:00', '5e8d317b-aae7-4ba6-9729-57e3b8c617ef', 60);
INSERT INTO trainings (training_id, trainee_id, trainer_id, training_name, training_date, training_type_id, training_duration) VALUES ('2e3f4a5b-6c7d-42e3-f4a5-82e3f4a5b6c7', '4d5e6f1a-2b3c-4d4e-1f1a-4a5b6c7d8e9f', '5e6f1a2b-3c4d-4e5f-2a1b-5a6b7c8d9e0f', 'Lower Body Strength', '2025-02-01 12:45:00', '75250412-9beb-417d-a087-4fb2692d39b5', 55);


ALTER TABLE trainer_trainee ADD CONSTRAINT FK_TRAINER FOREIGN KEY (trainer_id) REFERENCES trainers (trainer_id) ON DELETE CASCADE;


ALTER TABLE trainings DROP CONSTRAINT fk4btmw0yu4tbogiw8qi0ewba74;

ALTER TABLE trainings ADD CONSTRAINT fk4btmw0yu4tbogiw8qi0ewba74 FOREIGN KEY (trainee_id) REFERENCES trainees(trainee_id) ON DELETE CASCADE;


ALTER TABLE trainer_trainee DROP CONSTRAINT fk2sl2lr5n7g2swroe2gid185nk;

ALTER TABLE trainer_trainee ADD CONSTRAINT fk2sl2lr5n7g2swroe2gid185nk FOREIGN KEY (trainee_id) REFERENCES trainees(trainee_id) ON DELETE CASCADE;
