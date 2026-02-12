package org.example.service;

import org.example.dao.UserDAO;
import org.example.entity.User;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class UserService {
    /**
     * Data Access Object для роботи з користувачами.
     */
    private UserDAO userDAO;
    
    /**
     * Патерн для перевірки email адреси.
     */
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Конструктор.
     * Ініціалізує UserDAO для роботи з даними.
     */
    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Реєструє нового користувача з валідацією даних.
     * 
     * @param name ім'я користувача
     * @param email email адреса користувача
     * @param password пароль користувача
     * @return створений користувач або null, якщо реєстрація не вдалася
     * @throws IllegalArgumentException якщо дані невалідні
     * @throws IOException якщо виникла помилка при роботі з файлом
     */
    public User registerUser(String name, String email, String password, String imageUrl) 
            throws IllegalArgumentException, IOException {
        // Валідація імені
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ім'я не може бути порожнім");
        }
        
        // Валідація email
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email не може бути порожнім");
        }
        
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Невірний формат email адреси");
        }
        
        // Перевірка, чи email вже використовується
        if (userDAO.findByEmail(email) != null) {
            throw new IllegalArgumentException("Користувач з таким email вже існує");
        }
        
        // Валідація пароля
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Пароль повинен містити мінімум 4 символи");
        }
        
        // Створення користувача
        User user = new User(0, name.trim(), email.trim().toLowerCase(), password, imageUrl);
        boolean created = userDAO.create(user);
        
        if (created) {
            return user;
        }
        
        return null;
    }
    
    // Legacy support or default image
    public User registerUser(String name, String email, String password) 
            throws IllegalArgumentException, IOException {
        return registerUser(name, email, password, null);
    }

    /**
     * Шукає користувачів за запитом.
     * @param query пошуковий запит
     * @return список знайдених користувачів
     */
    public List<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return userDAO.findAll();
        }
        return userDAO.searchUsers(query.trim());
    }

    /**
     * Автентифікує користувача за email та паролем.
     * 
     * @param email email адреса користувача
     * @param password пароль користувача
     * @return користувач, якщо автентифікація успішна, null - інакше
     * @throws IOException якщо виникла помилка при роботі з файлом
     */
    public User loginUser(String email, String password) throws IOException {
        if (email == null || password == null) {
            return null;
        }
        
        User user = userDAO.findByEmail(email.trim().toLowerCase());
        if (user != null && user.checkPassword(password)) {
            return user;
        }
        
        return null;
    }

    /**
     * Отримує користувача за ідентифікатором.
     * 
     * @param id ідентифікатор користувача
     * @return користувач або null, якщо не знайдено
     * @throws IOException якщо виникла помилка при роботі з файлом
     */


    /**
     * Отримує всіх користувачів.
     * 
     * @return список всіх користувачів
     * @throws IOException якщо виникла помилка при роботі з файлом
     */
    public List<User> getAllUsers() throws IOException {
        return userDAO.findAll();
    }

    /**
     * Перевіряє валідність email адреси.
     * 
     * @param email email адреса для перевірки
     * @return true, якщо email валідний, false - інакше
     */
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}

