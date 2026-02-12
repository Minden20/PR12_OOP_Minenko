package org.example.entity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Клас користувача системи "Гостьова книга басейну".
 * Зберігає інформацію про користувача та його захешований пароль.
 * 
 * Кожен користувач має унікальний ідентифікатор, ім'я, email та пароль,
 * який зберігається у вигляді хешу для безпеки.
 * 
 * @author MinenkoKN-33
 * @version 1.0
 */
public class User {

  private String hashedPassword;
  /**
     * Унікальний ідентифікатор користувача.
     */
    private int id;
    
    /**
     * Ім'я користувача.
     */
    private String name;
    
    /**
     * Email адреса користувача.
     */
    private String email;
    
    /**
     * Захешований пароль користувача.
     */
    /**
     * URL of the user's image.
     */
    private String imageUrl;

    /**
     * Конструктор за замовчуванням.
     * Створює порожній об'єкт користувача.
     */
    public User(String hashedPassword) {
      this.hashedPassword = hashedPassword;
    }

    /**
     * Конструктор з параметрами.
     * Створює користувача з вказаними даними та автоматично хешує пароль.
     * 
     * @param id унікальний ідентифікатор користувача
     * @param name ім'я користувача
     * @param email email адреса користувача
     * @param password пароль користувача (буде захешований)
     */
    public User(int id, String name, String email, String password) {
        this(id, name, email, password, null);
    }

    /**
     * Конструктор з параметрами включаючи URL картинки.
     * 
     * @param id унікальний ідентифікатор користувача
     * @param name ім'я користувача
     * @param email email адреса користувача
     * @param password пароль користувача (буде захешований)
     * @param imageUrl URL картинки користувача
     */
    public User(int id, String name, String email, String password, String imageUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hashedPassword = hashPassword(password);
        this.imageUrl = imageUrl;
    }

  public User() {

  }

    public static User createFromDb(int id, String name, String email, String hashedPassword, String imageUrl) {
        User user = new User();
        user.id = id;
        user.name = name;
        user.email = email;
        user.hashedPassword = hashedPassword;
        user.imageUrl = imageUrl;
        return user;
    }

    /**
     * Хешує пароль за допомогою алгоритму SHA-256.
     * 
     * @param password пароль для хешування
     * @return захешований пароль у вигляді рядка
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Помилка хешування пароля", e);
        }
    }

    /**
     * Перевіряє, чи відповідає введений пароль збереженому хешу.
     * 
     * @param password пароль для перевірки
     * @return true, якщо пароль правильний, false - інакше
     */
    public boolean checkPassword(String password) {
        String hashed = hashPassword(password);
        return hashed.equals(this.hashedPassword);
    }

    /**
     * Отримує ідентифікатор користувача.
     * 
     * @return ідентифікатор користувача
     */
    public int getId() {
        return id;
    }

    /**
     * Встановлює ідентифікатор користувача.
     * 
     * @param id ідентифікатор користувача
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Отримує ім'я користувача.
     * 
     * @return ім'я користувача
     */
    public String getName() {
        return name;
    }

    /**
     * Встановлює ім'я користувача.
     * 
     * @param name ім'я користувача
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Отримує email адресу користувача.
     * 
     * @return email адреса користувача
     */
    public String getEmail() {
        return email;
    }

    /**
     * Встановлює email адресу користувача.
     * 
     * @param email email адреса користувача
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Отримує захешований пароль користувача.
     * 
     * @return захешований пароль
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * Встановлює захешований пароль користувача.
     * 
     * @param hashedPassword захешований пароль
     */
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    /**
     * Встановлює пароль та автоматично хешує його.
     * 
     * @param password пароль для встановлення
     */
    public void setPassword(String password) {
        this.hashedPassword = hashPassword(password);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "', imageUrl='" + imageUrl + "'}";
    }
}

