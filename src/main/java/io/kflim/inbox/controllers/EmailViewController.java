package io.kflim.inbox.controllers;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.kflim.inbox.emaillist.EmailListItem;
import io.kflim.inbox.emails.Email;
import io.kflim.inbox.emails.EmailRepository;
import io.kflim.inbox.folders.Folder;
import io.kflim.inbox.folders.FolderRepository;
import io.kflim.inbox.folders.FolderService;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    private EmailRepository emailRepository;

    @GetMapping(value = "/emails/{id}")
    public String emailView(@PathVariable UUID id,
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

        Optional<Email> email = emailRepository.findById(id);
        if (email.isEmpty()) {
            return "inbox-page";
        }
        Email emailContent = email.get();
        String toList = String.join(", ", emailContent.getTo());
        model.addAttribute("email", email.get());
        model.addAttribute("toList", toList);

        return "email-page";
    }
}
