package io.kflim.inbox;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.kflim.inbox.emaillist.EmailListItem;
import io.kflim.inbox.emaillist.EmailListItemKey;
import io.kflim.inbox.emaillist.EmailListItemRepository;
import io.kflim.inbox.emails.Email;
import io.kflim.inbox.emails.EmailRepository;
import io.kflim.inbox.emails.EmailService;
import io.kflim.inbox.folders.Folder;
import io.kflim.inbox.folders.FolderRepository;
import io.kflim.inbox.folders.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.Arrays;

@SpringBootApplication
@RestController
public class InboxApp {

	@Autowired
	private FolderRepository folderRepository;

	@Autowired
	private EmailListItemRepository emailListItemRepository;

	@Autowired
	private EmailService emailService;

	public static void main(String[] args) {
		SpringApplication.run(InboxApp.class, args);
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}
	
	@PostConstruct
	public void init() {
		folderRepository.save(new Folder("kflim", "Work", "blue"));
		folderRepository.save(new Folder("kflim", "Home", "green"));
		folderRepository.save(new Folder("kflim", "Drafts", "yellow"));

		for (int i = 0; i < 10; i++) {
			emailService.sendEmail("kflim", Arrays.asList("kflim", "jack"),
				"Hello " + i, "Body " + i);
		}
	}
}
