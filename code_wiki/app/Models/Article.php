<?php

namespace App\Models;

use App\Core\Database;
use DateTime; // Для работы с датами

class Article
{
    protected Database $db;

    public function __construct(Database $db)
    {
        $this->db = $db;
    }

    /**
     * Генерирует уникальный slug из заголовка.
     *
     * @param string $title Заголовок статьи.
     * @return string Сгенерированный slug.
     */
    protected function generateSlug(string $title): string
    {
        $slug = strtolower(trim(preg_replace('/[^A-Za-z0-9-]+/', '-', $title), '-'));
        $originalSlug = $slug;
        $counter = 1;

        // Проверяем уникальность slug в БД
        while ($this->findBySlug($slug)) {
            $slug = $originalSlug . '-' . $counter++;
        }
        return $slug;
    }

    /**
     * Создает новую статью в базе данных.
     *
     * @param string $title Заголовок статьи.
     * @param string $content Содержимое статьи (Markdown).
     * @param int $authorId ID автора.
     * @param string $status Статус статьи ('draft', 'published', 'archived').
     * @param array $categoryIds Массив ID категорий.
     * @param array $tagNames Массив названий тегов.
     * @return array|false Ассоциативный массив данных статьи, если успешно, иначе false.
     */
    public function createArticle(string $title, string $content, int $authorId, string $status = 'draft', array $categoryIds = [], array $tagNames = []): array|false
    {
        $slug = $this->generateSlug($title);
        $now = (new DateTime())->format('Y-m-d H:i:s');

        $sql = "INSERT INTO articles (title, slug, content, author_id, created_at, updated_at, status)
                VALUES (:title, :slug, :content, :author_id, :created_at, :updated_at, :status)";

        $params = [
            ':title' => $title,
            ':slug' => $slug,
            ':content' => $content,
            ':author_id' => $authorId,
            ':created_at' => $now,
            ':updated_at' => $now,
            ':status' => $status
        ];

        try {
            $this->db->query($sql, $params);
            $articleId = $this->db->lastInsertId();

            // Привязка категорий
            if (!empty($categoryIds)) {
                $this->linkCategories($articleId, $categoryIds);
            }

            // Привязка тегов
            if (!empty($tagNames)) {
                $this->linkTags($articleId, $tagNames);
            }

            // Создаем первую ревизию
            $revisionModel = new Revision($this->db);
            $revisionModel->addRevision($articleId, $authorId, "Первоначальная версия статьи.");

            // Возвращаем данные только что созданной статьи
            return $this->findById((int)$articleId);
        } catch (\PDOException $e) {
            // В реальном проекте здесь будет логирование ошибки
            error_log("Error creating article: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Обновляет существующую статью.
     *
     * @param int $id ID статьи.
     * @param string $title Заголовок.
     * @param string $content Содержимое.
     * @param string $status Статус.
     * @param int $updaterId ID пользователя, который обновил статью.
     * @param array $categoryIds Массив ID категорий.
     * @param array $tagNames Массив названий тегов.
     * @return bool Успех обновления.
     */
    public function updateArticle(int $id, string $title, string $content, string $status, int $updaterId, array $categoryIds = [], array $tagNames = []): bool
    {
        $existingArticle = $this->findById($id);
        if (!$existingArticle) {
            return false;
        }

        $now = (new DateTime())->format('Y-m-d H:i:s');
        $oldContent = $existingArticle['content'];
        $changes = ($oldContent !== $content) ? "Содержимое изменено." : ""; // Простое отслеживание изменений

        $sql = "UPDATE articles SET title = :title, content = :content, status = :status, updated_at = :updated_at WHERE id = :id";
        $params = [
            ':title' => $title,
            ':content' => $content,
            ':status' => $status,
            ':updated_at' => $now,
            ':id' => $id
        ];

        try {
            $this->db->query($sql, $params);

            // Обновляем категории
            $this->unlinkAllCategories($id);
            if (!empty($categoryIds)) {
                $this->linkCategories($id, $categoryIds);
            }

            // Обновляем теги
            $this->unlinkAllTags($id);
            if (!empty($tagNames)) {
                $this->linkTags($id, $tagNames);
            }

            // Добавляем новую ревизию, если контент изменился
            if ($changes) {
                $revisionModel = new Revision($this->db);
                $revisionModel->addRevision($id, $updaterId, $changes);
            }

            return true;
        } catch (\PDOException $e) {
            error_log("Error updating article: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Удаляет статью по ID.
     * Также удаляет связанные категории, теги, ревизии, комментарии и жалобы.
     *
     * @param int $id ID статьи.
     * @return bool Успех удаления.
     */
    public function deleteArticle(int $id): bool
    {
        try {
            // Удаляем связанные записи из промежуточных таблиц
            $this->unlinkAllCategories($id);
            $this->unlinkAllTags($id);

            // Удаляем связанные ревизии
            $this->db->query("DELETE FROM revisions WHERE article_id = :id", [':id' => $id]);

            // Удаляем связанные комментарии
            $this->db->query("DELETE FROM comments WHERE article_id = :id", [':id' => $id]);

            // Удаляем связанные жалобы на эту статью
            $this->db->query("DELETE FROM reports WHERE reported_item_type = 'article' AND reported_item_id = :id", [':id' => $id]);

            // Удаляем саму статью
            $sql = "DELETE FROM articles WHERE id = :id";
            $stmt = $this->db->query($sql, [':id' => $id]);

            return $stmt->rowCount() > 0;
        } catch (\PDOException $e) {
            error_log("Error deleting article: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Находит статью по ID.
     *
     * @param int $id ID статьи.
     * @return array|false Данные статьи или false, если не найдена.
     */
    public function findById(int $id): array|false
    {
        $sql = "SELECT a.*, u.name as author_name
                FROM articles a
                JOIN users u ON a.author_id = u.id
                WHERE a.id = :id";
        $stmt = $this->db->query($sql, [':id' => $id]);
        return $stmt->fetch();
    }

    /**
     * Находит статью по slug.
     *
     * @param string $slug Slug статьи.
     * @return array|false Данные статьи или false, если не найдена.
     */
    public function findBySlug(string $slug): array|false
    {
        $sql = "SELECT a.*, u.name as author_name
                FROM articles a
                JOIN users u ON a.author_id = u.id
                WHERE a.slug = :slug";
        $stmt = $this->db->query($sql, [':slug' => $slug]);
        return $stmt->fetch();
    }

    /**
     * Получает все опубликованные статьи, отсортированные по дате изменения.
     *
     * @param int $limit Ограничение на количество статей.
     * @return array Массив статей.
     */
    public function getAllPublished(int $limit = 0): array
    {
        $sql = "SELECT a.*, u.name as author_name
                FROM articles a
                JOIN users u ON a.author_id = u.id
                WHERE a.status = 'published'
                ORDER BY a.updated_at DESC";
        if ($limit > 0) {
            $sql .= " LIMIT :limit";
            $stmt = $this->db->query($sql, [':limit' => $limit]);
        } else {
            $stmt = $this->db->query($sql);
        }
        return $stmt->fetchAll();
    }

    /**
     * Получает все статьи (для админ-панели).
     *
     * @return array Массив статей.
     */
    public function getAll(): array
    {
        $sql = "SELECT a.*, u.name as author_name
                FROM articles a
                JOIN users u ON a.author_id = u.id
                ORDER BY a.updated_at DESC";
        $stmt = $this->db->query($sql);
        return $stmt->fetchAll();
    }

    /**
     * Получает статьи по ID категории.
     *
     * @param int $categoryId ID категории.
     * @return array Массив статей.
     */
    public function getArticlesByCategory(int $categoryId): array
    {
        $sql = "SELECT a.*, u.name as author_name
                FROM articles a
                JOIN users u ON a.author_id = u.id
                JOIN article_categories ac ON a.id = ac.article_id
                WHERE ac.category_id = :category_id AND a.status = 'published'
                ORDER BY a.updated_at DESC";
        $stmt = $this->db->query($sql, [':category_id' => $categoryId]);
        return $stmt->fetchAll();
    }

    /**
     * Получает статьи по ID тега.
     *
     * @param int $tagId ID тега.
     * @return array Массив статей.
     */
    public function getArticlesByTag(int $tagId): array
    {
        $sql = "SELECT a.*, u.name as author_name
                FROM articles a
                JOIN users u ON a.author_id = u.id
                JOIN article_tags at ON a.id = at.article_id
                WHERE at.tag_id = :tag_id AND a.status = 'published'
                ORDER BY a.updated_at DESC";
        $stmt = $this->db->query($sql, [':tag_id' => $tagId]);
        return $stmt->fetchAll();
    }

    /**
     * Связывает статью с категориями.
     *
     * @param int $articleId ID статьи.
     * @param array $categoryIds Массив ID категорий.
     */
    public function linkCategories(int $articleId, array $categoryIds): void
    {
        foreach ($categoryIds as $categoryId) {
            $sql = "INSERT IGNORE INTO article_categories (article_id, category_id) VALUES (:article_id, :category_id)";
            $this->db->query($sql, [':article_id' => $articleId, ':category_id' => $categoryId]);
        }
    }

    /**
     * Отвязывает все категории от статьи.
     *
     * @param int $articleId ID статьи.
     */
    public function unlinkAllCategories(int $articleId): void
    {
        $sql = "DELETE FROM article_categories WHERE article_id = :article_id";
        $this->db->query($sql, [':article_id' => $articleId]);
    }

    /**
     * Связывает статью с тегами.
     * Автоматически создает теги, если они не существуют.
     *
     * @param int $articleId ID статьи.
     * @param array $tagNames Массив названий тегов.
     */
    public function linkTags(int $articleId, array $tagNames): void
    {
        $tagModel = new Tag($this->db);
        foreach ($tagNames as $tagName) {
            $tag = $tagModel->findByName($tagName);
            $tagId = $tag ? $tag['id'] : $tagModel->createTag($tagName);

            if ($tagId) {
                $sql = "INSERT IGNORE INTO article_tags (article_id, tag_id) VALUES (:article_id, :tag_id)";
                $this->db->query($sql, [':article_id' => $articleId, ':tag_id' => $tagId]);
            }
        }
    }

    /**
     * Отвязывает все теги от статьи.
     *
     * @param int $articleId ID статьи.
     */
    public function unlinkAllTags(int $articleId): void
    {
        $sql = "DELETE FROM article_tags WHERE article_id = :article_id";
        $this->db->query($sql, [':article_id' => $articleId]);
    }
}