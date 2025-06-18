<?php

namespace App\Models;

use App\Core\Database;
use DateTime;

class Report
{
    protected Database $db;

    public function __construct(Database $db)
    {
        $this->db = $db;
    }

    /**
     * Создает новую жалобу.
     *
     * @param string $itemType Тип элемента ('article', 'comment', 'user').
     * @param int $itemId ID элемента, на который подана жалоба.
     * @param int $reporterId ID пользователя, подавшего жалобу.
     * @param string $reason Причина жалобы.
     * @return int|false ID новой жалобы или false при ошибке.
     */
    public function createReport(string $itemType, int $itemId, int $reporterId, string $reason): int|false
    {
        $now = (new DateTime())->format('Y-m-d H:i:s');
        $sql = "INSERT INTO reports (reported_item_type, reported_item_id, reporter_id, reason, created_at, status)
                VALUES (:reported_item_type, :reported_item_id, :reporter_id, :reason, :created_at, 'pending')";
        $params = [
            ':reported_item_type' => $itemType,
            ':reported_item_id' => $itemId,
            ':reporter_id' => $reporterId,
            ':reason' => $reason,
            ':created_at' => $now
        ];
        try {
            $this->db->query($sql, $params);
            return (int)$this->db->lastInsertId();
        } catch (\PDOException $e) {
            error_log("Error creating report: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Получает все ожидающие (pending) жалобы.
     *
     * @return array Массив жалоб.
     */
    public function getAllPendingReports(): array
    {
        $sql = "SELECT r.*, u.name as reporter_name
                FROM reports r
                JOIN users u ON r.reporter_id = u.id
                WHERE r.status = 'pending'
                ORDER BY r.created_at ASC";
        $stmt = $this->db->query($sql);
        return $stmt->fetchAll();
    }

    /**
     * Отмечает жалобу как рассмотренную (resolved).
     *
     * @param int $id ID жалобы.
     * @return bool Успех обновления.
     */
    public function resolveReport(int $id): bool
    {
        $sql = "UPDATE reports SET status = 'resolved' WHERE id = :id";
        try {
            $stmt = $this->db->query($sql, [':id' => $id]);
            return $stmt->rowCount() > 0;
        } catch (\PDOException $e) {
            error_log("Error resolving report: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Удаляет жалобу по ID (возможно, после разрешения или ошибки).
     *
     * @param int $id ID жалобы.
     * @return bool Успех удаления.
     */
    public function deleteReport(int $id): bool
    {
        $sql = "DELETE FROM reports WHERE id = :id";
        $stmt = $this->db->query($sql, [':id' => $id]);
        return $stmt->rowCount() > 0;
    }
}