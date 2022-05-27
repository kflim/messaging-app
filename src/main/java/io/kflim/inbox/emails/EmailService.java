package io.kflim.inbox.emails;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.kflim.inbox.emaillist.EmailListItem;
import io.kflim.inbox.emaillist.EmailListItemKey;
import io.kflim.inbox.emaillist.EmailListItemRepository;
import io.kflim.inbox.folders.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailListItemRepository emailListItemRepository;

    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    public void sendEmail(String from, List<String> to, String subject, String body) {
        Email email = new Email();
        email.setFrom(from);
        email.setTo(to);
        email.setSubject(subject);
        email.setBody(body);
        email.setId(Uuids.timeBased());

        emailRepository.save(email);

        to.forEach(toId -> {
            EmailListItem item = createEmailListItem(to, subject, email, toId, "Inbox");
            emailListItemRepository.save(item);
            unreadEmailStatsRepository.incrementUnreadCount(toId, "Inbox");
        });

        EmailListItem sentItem = createEmailListItem(to, subject, email, from, "Sent");
        sentItem.setUnread(false);
        emailListItemRepository.save(sentItem);
    }

    public boolean checkIfPermitted(Email email, String userId) {
        return userId.equals(email.getFrom()) || email.getTo().contains(userId);
    }

    public String getReplySubject(String subject) {
        return "Re: " + subject;
    }

    public String getReplyBody(Email email) {
        return "\n\n\n------------------------------------------\n"
            + "From: " + email.getFrom() + "\n"
            + "To: " + email.getTo() + "\n\n"
            + email.getBody();
    }

    private EmailListItem createEmailListItem(List<String> to, String subject,
                                              Email email, String itemOwner, String folder) {
        EmailListItemKey key = new EmailListItemKey();
        key.setUserId(itemOwner);
        key.setLabel(folder);
        key.setTimeUUID(email.getId());

        EmailListItem item = new EmailListItem();
        item.setId(key);
        item.setTo(to);
        item.setSubject(subject);
        item.setUnread(true);
        return item;
    }
}
