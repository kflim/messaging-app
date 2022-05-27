package io.kflim.inbox.controllers;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.kflim.inbox.emaillist.EmailListItem;
import io.kflim.inbox.emaillist.EmailListItemKey;
import io.kflim.inbox.emaillist.EmailListItemRepository;
import io.kflim.inbox.emails.Email;
import io.kflim.inbox.emails.EmailRepository;
import io.kflim.inbox.emails.EmailService;
import io.kflim.inbox.folders.Folder;
import io.kflim.inbox.folders.FolderRepository;
import io.kflim.inbox.folders.FolderService;
import io.kflim.inbox.folders.UnreadEmailStatsRepository;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class EmailViewController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailListItemRepository emailListItemRepository;

    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    @GetMapping(value = "/emails/{id}")
    public String emailView(
        @RequestParam String folder,
        @PathVariable UUID id,
        @AuthenticationPrincipal OAuth2User principal,
        Model model) {

        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return "index";
        }

        String userId = principal.getAttribute("login");

        List<Folder> userFolders = folderRepository.findAllById(userId);
        model.addAttribute("userFolders", userFolders);
        List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);
        model.addAttribute("userName", principal.getAttribute("name"));

        Optional<Email> optionalEmail = emailRepository.findById(id);
        if (optionalEmail.isEmpty()) {
            return "inbox-page";
        }

        Email email = optionalEmail.get();
        String toList = String.join(", ", email.getTo());

        // Check permission
        if (!emailService.checkIfPermitted(email, userId)) {
            return "redirect:/";
        }

        model.addAttribute("email", email);
        model.addAttribute("toList", toList);

        EmailListItemKey key = new EmailListItemKey();
        key.setUserId(userId);
        key.setLabel(folder);
        key.setTimeUUID(email.getId());

        Optional<EmailListItem> optionalEmailListItem = emailListItemRepository.findById(key);
        if (optionalEmailListItem.isPresent()) {
            EmailListItem item = optionalEmailListItem.get();
            if (item.isUnread()) {
                item.setUnread(false);
                emailListItemRepository.save(item);
                unreadEmailStatsRepository.decrementUnreadCount(userId, folder);
            }
        }

        model.addAttribute("stats", folderService.mapCountToLabel(userId));

        return "email-page";
    }
}
