package org.example.util;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInit {

  public static void Init() {
    try (Connection conn = DatabaseCfg.getConnection();
        Statement st = conn.createStatement()) {

      st.execute("""
          CREATE TABLE IF NOT EXISTS users (
              id INT PRIMARY KEY AUTO_INCREMENT,
              name VARCHAR(255),
              email VARCHAR(255),
              hashedPassword VARCHAR(255),
              imageUrl VARCHAR(512)
          );
          """);
      
      // Migration: Add imageUrl if it doesn't exist
      try {
          st.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS imageUrl VARCHAR(512)");
      } catch (Exception e) {
          // Ignore if column already exists or syntax differs slightly (H2 supports IF NOT EXISTS for ADD COLUMN but let's be safe)
          // Actually H2 might throw if column exists if IF NOT EXISTS isn't supported in older versions, 
          // but assuming modern H2 it should work or we can catch duplicate column error.
          System.out.println("Column imageUrl might already exist or could not be added: " + e.getMessage());
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void InsertTestData() {
    try (Connection conn = DatabaseCfg.getConnection();
        Statement st = conn.createStatement()) {
      st.execute("""
          MERGE INTO users (id, name, email, hashedPassword, imageUrl) KEY(id) VALUES
          (1, 'Іван Петров', 'ivan.petrov@example.com', 'hashedpassword123', 'https://picsum.photos/200?random=1'),
          (2, 'Марія Сидорова', 'maria.sidorova@example.com', 'hashedpassword456', 'https://picsum.photos/200?random=2');
          """);

      st.execute("""
          MERGE INTO services (id, name, description) KEY(id) VALUES
          (1, 'Басейн', '4 години плавання в басейні'),
          (2, 'Дитячий басейн', '4 години плавання в басейні для дітей');
          """);
      
      // Note: Reviews table doesn't have unique constraint on id in create statement above (it implies but MERGE KEY(id) is safer)
      // Since create table uses AUTO_INCREMENT for id, we should be careful with manual inserts.
      // For simplicity/robustness in tests, using MERGE if H2 supports it, or just INSERT IGNORE logic.
      // Given previous code just did INSERT, it might duplicate on restart if database persists.
      // Switching to MERGE for idempotency if H2 is used.
      
      st.execute("""
          MERGE INTO reviews (id, userID, serviceID, text, rating) KEY(id) VALUES
          (1, 1, 1, 'Відміний басейн чистий та великий.', 5),
          (2, 2, 2, 'Хороший басейн мілкий, через що не так страшно за дитину', 4);
          """);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void InitS() {
    try (Connection conn = DatabaseCfg.getConnection();
        Statement st = conn.createStatement()) {

      st.execute("""
          CREATE TABLE IF NOT EXISTS services (
              id INT PRIMARY KEY AUTO_INCREMENT,
            name VARCHAR(255),
            description VARCHAR(255)
          );
          """);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void InitR() {
    try (Connection conn = DatabaseCfg.getConnection();
        Statement st = conn.createStatement()) {

      st.execute("""
          CREATE TABLE IF NOT EXISTS reviews (
              id INT PRIMARY KEY AUTO_INCREMENT,
              userID INT,
              serviceID INT,
              text VARCHAR(255),
              rating INT
          );
          """);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
