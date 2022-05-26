package io.kflim.inbox.controllers;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.kflim.inbox.emaillist.EmailListItem;
import io.kflim.inbox.emaillist.EmailListItemRepository;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class InboxController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailListItemRepository emailListItemRepository;

    @GetMapping(value = "/")
    public String homePage(@RequestParam(required = false) String folder,
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

        if (!StringUtils.hasText(folder)) {
            folder = "inbox";
        }
        model.addAttribute("folderName", folder);

        List<EmailListItem> emailList = emailListItemRepository.findAllById_UserIdAndId_Label(userId, folder);
        PrettyTime p = new PrettyTime();
        emailList.stream().forEach(emailItem -> {
            UUID timeId = emailItem.getId().getTimeUUID();
            Date date = new Date(Uuids.unixTimestamp(timeId));
            emailItem.setTimeSince(p.format(date));
        });
        model.addAttribute("emailList", emailList);


        return "inbox-page";
    }
}
