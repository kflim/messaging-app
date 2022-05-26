package io.kflim.inbox.controllers;

import io.kflim.inbox.folders.Folder;
import io.kflim.inbox.folders.FolderRepository;
import io.kflim.inbox.folders.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ComposeController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @GetMapping(value = "/compose")
    public String getComposePage(@RequestParam(required = false) String to,
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

        if (StringUtils.hasText(to)) {
            String[] splitIds = to.split(",");
            List<String> uniqueIds = Arrays.stream(splitIds)
                                           .map(StringUtils::trimWhitespace)
                                           .filter(StringUtils::hasText)
                                           .distinct().collect(Collectors.toList());

            model.addAttribute("toList", String.join(", ", uniqueIds));
        }

        return "compose-page";
    }
}
