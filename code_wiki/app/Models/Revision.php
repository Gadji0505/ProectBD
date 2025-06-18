<?php

namespace App\Models;

use App\Core\Database;
use DateTime;

class Revision
{
    protected Database $db;

    public function __construct(Database $db)
    {
        $this->db = $db;
    }

    /**
     * Добавляет новую ревизию для статьи.
     *
     * @param int $articleId ID статьи.
     * @param int $authorId ID автора ревизии.
     * @param string $changes Описание изменений.
     * @return int|false ID новой ревизии или false при ошибке.
     */
    public function addRevision(int $articleId, int $authorId, string $changes): int|false
    {
        $now = (new DateTime())->format('Y-m-d H:i:s');
        $sql = "INSERT INTO revisions (article_id, author_id, revision_date, changes)
                VALUES (:article_id, :author_id, :revision_date, :changes)";
        $params = [
            ':article_id' => $articleId,
            ':author_id' => $authorId,
            ':revision_date' => $now,
            ':changes' => $changes
        ];
        try {
            $this->db->query($sql, $params);
            return (int)$this->db->lastInsertId();
        } catch (\PDOException $e) {
            error_log("Error adding revision: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Получает все ревизии для конкретной статьи.
     *
     * @param int $articleId ID статьи.
     * @return array Массив ревизий.
     */
    public function getRevisionsForArticle(int $articleId): array
    {
        $sql = "SELECT r.*, u.name as author_name
                FROM revisions r
                JOIN users u ON r.author_id = u.id
                WHERE r.article_id = :article_id
                ORDER BY r.revision_date DESC";
        $stmt = $this->db->query($sql, [':article_id' => $articleId]);
        return $stmt->fetchAll();
    }

    /**
     * Удаляет ревизию по ID (используется редко, обычно ревизии не удаляют).
     *
     * @param int $id ID ревизии.
     * @return bool Успех удаления.
     */
    public function deleteRevision(int $id): bool
    {
        $sql = "DELETE FROM revisions WHERE id = :id";
        $stmt = $this->db->query($sql, [':id' => $id]);
        return $stmt->rowCount() > 0;
    }
}