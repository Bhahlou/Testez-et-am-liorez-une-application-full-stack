SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE participate;
TRUNCATE TABLE sessions;
TRUNCATE TABLE teachers;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

REPLACE INTO users(first_name, last_name, admin, email, password) 
VALUES 
    ('Admin', 'Admin', true, 'yoga@studio.com', '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq'),
    ('Doe', 'John', false, 'john@doe.com', '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq');

REPLACE INTO teachers(id, first_name, last_name)
VALUE 
    (1, 'Doe', 'John'),
    (2, 'Kenobi', 'Obi-Wan'),
    (3, 'Jin', 'Qui-Gon');

REPLACE INTO sessions(id, date, description, name, teacher_id)
VALUES
    (1, '2026-01-01 13:30:00', 'Ceci est la description de la session 1', 'Session 1', 1),
    (2, '2026-02-01 14:30:00', 'Ceci est la description de la session 2', 'Session 2', 2);