/**
 * API-клиент для CodeWiki
 * Базовый URL: /api
 * Все запросы автоматически добавляют JWT-токен из localStorage
 */

const API_BASE_URL = '/api';
const AUTH_TOKEN_KEY = 'codewiki_jwt';

// ======================
// Вспомогательные функции
// ======================

/**
 * Обработка HTTP-ответов
 */
async function handleResponse(response) {
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Request failed');
  }
  return response.json();
}

/**
 * Формирование заголовков запроса
 */
function getHeaders(withAuth = true) {
  const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  };

  if (withAuth) {
    const token = localStorage.getItem(AUTH_TOKEN_KEY);
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
  }

  return headers;
}

// ======================
// API-методы
// ======================

/**
 * Аутентификация
 */
export const authApi = {
  login: async (username, password) => {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: getHeaders(false),
      body: JSON.stringify({ username, password })
    });
    const data = await handleResponse(response);
    localStorage.setItem(AUTH_TOKEN_KEY, data.token);
    return data;
  },

  register: async (userData) => {
    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      headers: getHeaders(false),
      body: JSON.stringify(userData)
    });
    return handleResponse(response);
  },

  logout: () => {
    localStorage.removeItem(AUTH_TOKEN_KEY);
  },

  getCurrentUser: async () => {
    const response = await fetch(`${API_BASE_URL}/users/me`, {
      headers: getHeaders()
    });
    return handleResponse(response);
  }
};

/**
 * Статьи
 */
export const articleApi = {
  getAll: async (page = 1, limit = 10) => {
    const response = await fetch(`${API_BASE_URL}/articles?page=${page}&limit=${limit}`);
    return handleResponse(response);
  },

  getBySlug: async (slug) => {
    const response = await fetch(`${API_BASE_URL}/articles/${slug}`);
    return handleResponse(response);
  },

  create: async (articleData) => {
    const response = await fetch(`${API_BASE_URL}/articles`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(articleData)
    });
    return handleResponse(response);
  },

  update: async (id, articleData) => {
    const response = await fetch(`${API_BASE_URL}/articles/${id}`, {
      method: 'PUT',
      headers: getHeaders(),
      body: JSON.stringify(articleData)
    });
    return handleResponse(response);
  },

  delete: async (id) => {
    const response = await fetch(`${API_BASE_URL}/articles/${id}`, {
      method: 'DELETE',
      headers: getHeaders()
    });
    return handleResponse(response);
  },

  vote: async (id, isUpvote) => {
    const response = await fetch(`${API_BASE_URL}/articles/${id}/vote`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify({ isUpvote })
    });
    return handleResponse(response);
  },

  search: async (query) => {
    const response = await fetch(`${API_BASE_URL}/articles/search?q=${encodeURIComponent(query)}`);
    return handleResponse(response);
  }
};

/**
 * Комментарии
 */
export const commentApi = {
  getByArticle: async (articleId) => {
    const response = await fetch(`${API_BASE_URL}/comments/article/${articleId}`);
    return handleResponse(response);
  },

  create: async (commentData) => {
    const response = await fetch(`${API_BASE_URL}/comments`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(commentData)
    });
    return handleResponse(response);
  },

  delete: async (id) => {
    const response = await fetch(`${API_BASE_URL}/comments/${id}`, {
      method: 'DELETE',
      headers: getHeaders()
    });
    return handleResponse(response);
  }
};

/**
 * Пользователи
 */
export const userApi = {
  getById: async (id) => {
    const response = await fetch(`${API_BASE_URL}/users/${id}`);
    return handleResponse(response);
  },

  updateProfile: async (userData) => {
    const response = await fetch(`${API_BASE_URL}/users/me`, {
      method: 'PUT',
      headers: getHeaders(),
      body: JSON.stringify(userData)
    });
    return handleResponse(response);
  },

  getBookmarks: async () => {
    const response = await fetch(`${API_BASE_URL}/users/me/bookmarks`, {
      headers: getHeaders()
    });
    return handleResponse(response);
  },

  addBookmark: async (articleId) => {
    const response = await fetch(`${API_BASE_URL}/users/me/bookmarks/${articleId}`, {
      method: 'POST',
      headers: getHeaders()
    });
    return handleResponse(response);
  }
};

/**
 * Теги и категории
 */
export const taxonomyApi = {
  getPopularTags: async () => {
    const response = await fetch(`${API_BASE_URL}/tags/popular`);
    return handleResponse(response);
  },

  getCategories: async () => {
    const response = await fetch(`${API_BASE_URL}/categories`);
    return handleResponse(response);
  }
};

/**
 * Модерация
 */
export const moderationApi = {
  createReport: async (reportData) => {
    const response = await fetch(`${API_BASE_URL}/reports`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(reportData)
    });
    return handleResponse(response);
  },

  getReports: async () => {
    const response = await fetch(`${API_BASE_URL}/reports`, {
      headers: getHeaders()
    });
    return handleResponse(response);
  }
};

// ======================
// Интерцептор для JWT
// ======================

/**
 * Обновление токена при 401 ошибке
 */
async function fetchWithAuth(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: getHeaders()
  });

  if (response.status === 401) {
    // Попытка обновить токен
    const refreshResponse = await fetch(`${API_BASE_URL}/auth/refresh`, {
      method: 'POST',
      headers: getHeaders()
    });

    if (refreshResponse.ok) {
      const { token } = await refreshResponse.json();
      localStorage.setItem(AUTH_TOKEN_KEY, token);
      // Повторяем исходный запрос
      return fetch(url, {
        ...options,
        headers: getHeaders()
      });
    } else {
      authApi.logout();
      window.location.href = '/login';
    }
  }

  return response;
}

// ======================
// Инициализация API
// ======================

/**
 * Проверка аутентификации при загрузке страницы
 */
document.addEventListener('DOMContentLoaded', async () => {
  try {
    const token = localStorage.getItem(AUTH_TOKEN_KEY);
    if (token && !window.location.pathname.includes('/auth')) {
      await authApi.getCurrentUser();
    }
  } catch (error) {
    console.error('Auth check failed:', error);
  }
});