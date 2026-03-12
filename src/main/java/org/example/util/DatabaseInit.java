package org.example.util;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInit {

    public static void Init() {
        try (Connection conn = DatabaseCfg.getConnection();
            Statement st = conn.createStatement()) {

            // Спершу створюємо таблиці без FK
            st.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(255),
                    email VARCHAR(255),
                    hashedPassword VARCHAR(255),
                    imageUrl VARCHAR(512)
                );
                """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS services (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(255),
                    description VARCHAR(255)
                );
                """);

            // reviews пов'язана з users та services
            st.execute("""
                CREATE TABLE IF NOT EXISTS reviews (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    userId INT NOT NULL,
                    serviceId INT NOT NULL,
                    text VARCHAR(255),
                    rating INT,
                    FOREIGN KEY (userId) REFERENCES users(id),
                    FOREIGN KEY (serviceId) REFERENCES services(id)
                );
                """);

            // visitors пов'язана з services
            st.execute("""
                CREATE TABLE IF NOT EXISTS visitors (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(255),
                    phone VARCHAR(20),
                    visitDate VARCHAR(20),
                    serviceId INT NOT NULL,
                    FOREIGN KEY (serviceId) REFERENCES services(id)
                );
                """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void InsertTestData() {
        try (Connection conn = DatabaseCfg.getConnection();
            Statement st = conn.createStatement()) {

            st.execute("""
                MERGE INTO users (id, name, email, hashedPassword, imageUrl) KEY(id) VALUES
                (1, 'Іван Петров', 'ivan@example.com', 'hash123', 'https://picsum.photos/200?random=1'),
                (2, 'Марія Сидорова', 'maria@example.com', 'hash456', 'https://picsum.photos/200?random=2');
                """);

            st.execute("""
                MERGE INTO services (id, name, description) KEY(id) VALUES
                (1, 'Басейн', '4 години плавання в басейні'),
                (2, 'Дитячий басейн', '4 години плавання для дітей');
                """);

            st.execute("""
                MERGE INTO reviews (id, userId, serviceId, text, rating) KEY(id) VALUES
                (1, 1, 1, 'Відмінний басейн, чистий та великий.', 5),
                (2, 2, 2, 'Хороший басейн для дитини.', 4);
                """);

            st.execute("""
                MERGE INTO visitors (id, name, phone, visitDate, serviceId) KEY(id) VALUES
                (1, 'Олексій Коваленко', '+380501234567', '2025-03-10', 1),
                (2, 'Анна Шевченко', '+380671234567', '2025-03-11', 2);
                """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
