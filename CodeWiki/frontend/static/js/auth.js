/**
 * Модуль аутентификации CodeWiki
 * Управление входом/выходом, токенами и защитой маршрутов
 */

const AUTH_TOKEN_KEY = 'codewiki_jwt';
const LOGIN_PAGE_URL = '/login.html';
const DEFAULT_REDIRECT_URL = '/index.html';

// ======================
// Основные функции
// ======================

/**
 * Вход пользователя
 * @param {string} email 
 * @param {string} password 
 * @returns {Promise<{user: object, token: string}>}
 */
export async function login(email, password) {
  try {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email, password })
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Login failed');
    }

    const data = await response.json();
    localStorage.setItem(AUTH_TOKEN_KEY, data.token);
    return data;
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
}

/**
 * Регистрация нового пользователя
 * @param {object} userData 
 * @returns {Promise<object>}
 */
export async function register(userData) {
  try {
    const response = await fetch('/api/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(userData)
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Registration failed');
    }

    return await response.json();
  } catch (error) {
    console.error('Registration error:', error);
    throw error;
  }
}

/**
 * Выход пользователя
 */
export function logout() {
  localStorage.removeItem(AUTH_TOKEN_KEY);
  window.location.href = LOGIN_PAGE_URL;
}

/**
 * Проверка аутентификации
 * @returns {boolean}
 */
export function isAuthenticated() {
  return !!localStorage.getItem(AUTH_TOKEN_KEY);
}

/**
 * Получение текущего токена
 * @returns {string|null}
 */
export function getToken() {
  return localStorage.getItem(AUTH_TOKEN_KEY);
}

/**
 * Получение данных текущего пользователя
 * @returns {Promise<object>}
 */
export async function getCurrentUser() {
  try {
    const token = getToken();
    if (!token) return null;

    const response = await fetch('/api/auth/me', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (!response.ok) {
      throw new Error('Failed to fetch user data');
    }

    return await response.json();
  } catch (error) {
    console.error('Failed to get current user:', error);
    logout();
    return null;
  }
}

// ======================
// Защита маршрутов
// ======================

/**
 * Проверка доступа для защищённых страниц
 */
export async function protectRoute() {
  const isAuthPage = window.location.pathname.includes('/auth');

  if (!isAuthenticated() && !isAuthPage) {
    redirectToLogin();
    return false;
  }

  if (isAuthenticated() && isAuthPage) {
    redirectToApp();
    return false;
  }

  try {
    const user = await getCurrentUser();
    if (!user && !isAuthPage) {
      redirectToLogin();
      return false;
    }
    return user;
  } catch (error) {
    redirectToLogin();
    return false;
  }
}

/**
 * Перенаправление на страницу входа
 */
export function redirectToLogin() {
  if (!window.location.pathname.includes(LOGIN_PAGE_URL)) {
    window.location.href = LOGIN_PAGE_URL;
  }
}

/**
 * Перенаправление в приложение
 */
export function redirectToApp() {
  window.location.href = DEFAULT_REDIRECT_URL;
}

// ======================
// Инициализация
// ======================

/**
 * Инициализация аутентификации
 */
export async function initAuth() {
  // Проверяем токен при загрузке страницы
  const token = getToken();
  if (token) {
    try {
      // Валидируем токен
      await getCurrentUser();
    } catch (error) {
      logout();
    }
  }

  // Добавляем обработчик выхода
  const logoutButtons = document.querySelectorAll('[data-logout]');
  logoutButtons.forEach(button => {
    button.addEventListener('click', logout);
  });
}

// Автоматическая инициализация при загрузке
document.addEventListener('DOMContentLoaded', initAuth);