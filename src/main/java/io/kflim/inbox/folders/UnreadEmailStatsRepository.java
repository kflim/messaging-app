package io.kflim.inbox.folders;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnreadEmailStatsRepository extends CassandraRepository<UnreadEmailStats, String> {

    List<UnreadEmailStats> findAllById(String id);

    @Query(value = "update unread_email_stats set unreadCount = unreadCount + 1 where user_id = ?0 and label = ?1")
    void incrementUnreadCount(String id, String label);

    @Query(value = "update unread_email_stats set unreadCount = unreadCount - 1 where user_id = ?0 and label = ?1")
    void decrementUnreadCount(String id, String label);
}
