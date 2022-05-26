package io.kflim.inbox.emaillist;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailListItemRepository extends CassandraRepository<EmailListItem, EmailListItemKey> {

    List<EmailListItem> findAllById_UserIdAndId_Label(String userId, String label);
}
