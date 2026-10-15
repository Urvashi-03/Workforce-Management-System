-- Optional: the app creates the database/table automatically (ddl-auto=update).
-- Run this only if you prefer to create the schema manually.
CREATE DATABASE IF NOT EXISTS employee_db;
USE employee_db;

CREATE TABLE IF NOT EXISTS employee (
    employee_id     INT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(100)  NOT NULL,
    email           VARCHAR(255)  NOT NULL UNIQUE,
    phone_no        VARCHAR(10)   NOT NULL,
    department      VARCHAR(255)  NOT NULL,
    designation     VARCHAR(255)  NOT NULL,
    date_of_joining DATE          NOT NULL,
    salary          DOUBLE        NOT NULL,
    status          VARCHAR(20)   NOT NULL
);

-- Sample data
INSERT INTO employee (full_name, email, phone_no, department, designation, date_of_joining, salary, status) VALUES
('Aarav Sharma', 'aarav@example.com', '9876500001', 'Engineering', 'Software Engineer', '2024-06-10', 60000, 'Active'),
('Priya Verma',  'priya@example.com', '9876500002', 'HR',          'HR Executive',      '2023-03-15', 45000, 'Active'),
('Rohan Mehta',  'rohan@example.com', '9876500003', 'Finance',     'Accountant',        '2022-11-01', 50000, 'Inactive');
