<?php

namespace App\Models;

use App\Core\Database;
use DateTime;

class Comment
{
    protected Database $db;

    public function __construct(Database $db)
    {
        $this->db = $db;
    }

    /**
     * Добавляет новый комментарий к статье.
     *
     * @param int $articleId ID статьи.
     * @param int $authorId ID автора комментария.
     * @param string $content Содержимое комментария.
     * @return int|false ID нового комментария или false при ошибке.
     */
    public function addComment(int $articleId, int $authorId, string $content): int|false
    {
        $now = (new DateTime())->format('Y-m-d H:i:s');
        $sql = "INSERT INTO comments (article_id, author_id, content, created_at)
                VALUES (:article_id, :author_id, :content, :created_at)";
        $params = [
            ':article_id' => $articleId,
            ':author_id' => $authorId,
            ':content' => $content,
            ':created_at' => $now
        ];
        try {
            $this->db->query($sql, $params);
            return (int)$this->db->lastInsertId();
        } catch (\PDOException $e) {
            error_log("Error adding comment: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Получает все комментарии для конкретной статьи.
     *
     * @param int $articleId ID статьи.
     * @return array Массив комментариев.
     */
    public function getCommentsForArticle(int $articleId): array
    {
        $sql = "SELECT c.*, u.name as author_name
                FROM comments c
                JOIN users u ON c.author_id = u.id
                WHERE c.article_id = :article_id
                ORDER BY c.created_at ASC";
        $stmt = $this->db->query($sql, [':article_id' => $articleId]);
        return $stmt->fetchAll();
    }

    /**
     * Удаляет комментарий по ID.
     *
     * @param int $id ID комментария.
     * @return bool Успех удаления.
     */
    public function deleteComment(int $id): bool
    {
        $sql = "DELETE FROM comments WHERE id = :id";
        $stmt = $this->db->query($sql, [':id' => $id]);
        return $stmt->rowCount() > 0;
    }
}