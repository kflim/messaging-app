package io.kflim.inbox.controllers;

import io.kflim.inbox.emails.Email;
import io.kflim.inbox.emails.EmailRepository;
import io.kflim.inbox.emails.EmailService;
import io.kflim.inbox.folders.Folder;
import io.kflim.inbox.folders.FolderRepository;
import io.kflim.inbox.folders.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class ComposeController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailRepository emailRepository;

    @GetMapping(value = "/compose")
    public String getComposePage(
        @RequestParam(required = false) String to,
        @RequestParam(required = false) UUID id,
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
        model.addAttribute("stats", folderService.mapCountToLabel(userId));
        List<String> uniqueIds = splitIds(to);
        model.addAttribute("toList", String.join(", ", uniqueIds));

        Optional<Email> optionalEmail = emailRepository.findById(id);
        if (optionalEmail.isPresent()) {
            Email email = optionalEmail.get();
            if (emailService.checkIfPermitted(email, userId)) {
                model.addAttribute("subject", emailService.getReplySubject(email.getSubject()));
                model.addAttribute("body", emailService.getReplyBody(email));
            }
        }

        return "compose-page";
    }

    private List<String> splitIds(String to) {
        if (!StringUtils.hasText(to)) {
            return new ArrayList<>();
        }

        String[] splitIds = to.split(",");
        return Arrays.stream(splitIds)
                     .map(StringUtils::trimWhitespace)
                     .filter(StringUtils::hasText)
                     .distinct().collect(Collectors.toList());
    }


    @PostMapping("/sendEmail")
    public ModelAndView sendEmail(@RequestBody MultiValueMap<String, String> formData,
                                  @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return new ModelAndView("redirect:/");
        }

        String from = principal.getAttribute("login");
        List<String> toList = splitIds(formData.getFirst("toList"));
        String subject = formData.getFirst("subject");
        String body = formData.getFirst("body");
        emailService.sendEmail(from, toList, subject, body);

        return new ModelAndView("redirect:/");
    }
}
