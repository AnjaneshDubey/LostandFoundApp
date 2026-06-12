-- Lost and Found Item Tracker Database Schema
-- H2 In-Memory Database Setup Script

-- Users Table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    role ENUM('STUDENT', 'ADMIN') DEFAULT 'STUDENT',
    college_name VARCHAR(100) NOT NULL DEFAULT 'G.L Bajaj Institute of Technology and Management, Greater Noida',
    avatar_data LONGTEXT,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP,
    failed_login_attempts INT DEFAULT 0,
    last_username_change TIMESTAMP DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_email ON users(email);

-- Items Table
CREATE TABLE items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    tracking_number VARCHAR(20) UNIQUE NOT NULL,
    user_id INT NOT NULL,
    college_name VARCHAR(100) NOT NULL DEFAULT 'G.L Bajaj Institute of Technology and Management, Greater Noida',
    item_name VARCHAR(100) NOT NULL,
    description TEXT,
    category ENUM('ELECTRONICS', 'BOOKS', 'PERSONAL', 'DOCUMENTS', 'ACCESSORIES', 'OTHER') NOT NULL,
    location VARCHAR(100) NOT NULL,
    image_data TEXT,
    date_lost DATE,
    date_found DATE,
    status ENUM('LOST', 'FOUND', 'CLAIMED', 'RETURNED') DEFAULT 'LOST',
    found_by INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (found_by) REFERENCES users(user_id) ON DELETE SET NULL
);
CREATE INDEX idx_item_name ON items(item_name);
CREATE INDEX idx_category ON items(category);
CREATE INDEX idx_location ON items(location);
CREATE INDEX idx_status ON items(status);
CREATE INDEX idx_tracking_number ON items(tracking_number);

-- Status Updates Table
CREATE TABLE status_updates (
    update_id INT PRIMARY KEY AUTO_INCREMENT,
    item_id INT NOT NULL,
    previous_status ENUM('LOST', 'FOUND', 'CLAIMED', 'RETURNED'),
    new_status ENUM('LOST', 'FOUND', 'CLAIMED', 'RETURNED') NOT NULL,
    updated_by INT NOT NULL,
    comments TEXT,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES users(user_id) ON DELETE CASCADE
);
CREATE INDEX idx_item_id ON status_updates(item_id);
CREATE INDEX idx_update_date ON status_updates(update_date);

-- Insert default admin user
-- Password: admin123 (hashed with Custom SHA-256)
INSERT INTO users (username, hashed_password, email, role) 
VALUES ('admin', '8A7KM8bwZvVkOp8aKn0GSg==:W7MhHQuL6xXUPoSu79Ld/6Zj59hb61jZMnsHij7gpJ0=', 'admin@college.edu', 'ADMIN');

-- Insert sample student user
-- Password: student123 (hashed with Custom SHA-256)
INSERT INTO users (username, hashed_password, email, role) 
VALUES ('john_doe', '9PsKEY3+QJxz0oJuTMvB/g==:ven5buRYI1Lsu8sygHPzgh0u9i5G7OVRAb6LgnwSHSU=', 'john@college.edu', 'STUDENT');

-- Insert sample lost items
INSERT INTO items (tracking_number, user_id, item_name, description, category, location, date_lost, status) VALUES 
('LF-20251024-0001', 2, 'Blue Backpack', 'Blue Jansport backpack', 'PERSONAL', 'Library', '2025-10-23', 'LOST'),
('LF-20251020-0001', 1, 'Red Backpack', 'Nike red backpack', 'PERSONAL', 'Library', '2025-10-20', 'LOST'),
('LF-20251021-0002', 1, 'iPhone 14 Pro', 'Space gray iPhone', 'ELECTRONICS', 'Cafeteria', '2025-10-21', 'LOST'),
('LF-20251022-0003', 1, 'Textbook', 'Cormen algorithm book', 'BOOKS', 'CS Dept', '2025-10-22', 'LOST'),
('LF-20251023-0004', 1, 'Black Wallet', 'Leather wallet', 'PERSONAL', 'Court', '2025-10-23', 'LOST');

-- Insert sample found items
INSERT INTO items (tracking_number, user_id, item_name, description, category, location, date_found, status, found_by) VALUES 
('LF-20251024-0002', 2, 'iPhone 13', 'Black iPhone 13', 'ELECTRONICS', 'Cafeteria', '2025-10-24', 'FOUND', 2),
('LF-20251018-0006', 2, 'Blue Bottle', 'Steel bottle', 'ACCESSORIES', 'Gym', '2025-10-18', 'FOUND', 2),
('LF-20251019-0007', 2, 'Calculator', 'Casio FX-991', 'ELECTRONICS', 'Math Bldg', '2025-10-19', 'FOUND', 2),
('LF-20251020-0008', 2, 'Notebook', 'Brown notebook', 'BOOKS', 'Chem Lab', '2025-10-20', 'FOUND', 2);
