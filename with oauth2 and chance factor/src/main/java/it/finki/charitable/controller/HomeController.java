package it.finki.charitable.controller;

import it.finki.charitable.entities.AppUser;
import it.finki.charitable.entities.MainUser;
import it.finki.charitable.entities.Moderator;
import it.finki.charitable.entities.UserRole;
import it.finki.charitable.security.ConfirmationToken;
import it.finki.charitable.security.PasswordEncoder;
import it.finki.charitable.services.ConfirmationTokenService;
import it.finki.charitable.services.EmailService;
import it.finki.charitable.services.UserService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.UUID;

@Controller
public class HomeController {

    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;

    public HomeController(UserService userService, ConfirmationTokenService confirmationTokenService, EmailService emailService) {
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/login")
    public String login(Principal principal) {
        if(principal != null) {
            return "redirect:/";
        }

        return "login";
    }

    @RequestMapping("/register")
    public String register(Principal principal) {
        if(principal != null) {
            return "redirect:/";
        }
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String newUser(Model model, @RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String confirmPassword
    ) {

        boolean exists = userService.loadUserByUsername(email) != null;
        if(exists) {
            model.addAttribute("userExists", true);
            return "register";
        }

        boolean error = false;
        if(!EmailValidator.getInstance().isValid(email)) {
            model.addAttribute("emailError", true);
            error = true;
        }

        if(!password.equals(confirmPassword)) {
            model.addAttribute("passwordError", true);
            error = true;
        }

        if(firstName.isEmpty() || lastName.isEmpty()) {
            model.addAttribute("nameError", true);
            error = true;
        }

        if(error){
            return "register";
        }

        AppUser user = new MainUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(PasswordEncoder.bCryptPasswordEncoder().encode(password));
        user.setUserRole(UserRole.USER);
        user.setEnabled(false);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, user);

        userService.saveUser(user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        emailService.sendValidationEmail(email, "CharitableMk account validation", token);

        model.addAttribute("success",true);

        return "register";
    }

    @RequestMapping("/validate")
    public String validate(Model model, @RequestParam String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationToken(token);
        if(confirmationToken != null) {
            AppUser user = confirmationToken.getUser();
            user.setEnabled(true);
            userService.saveUser(user);
            model.addAttribute("successValidation", true);
            return "login";
        }

        model.addAttribute("error", true);
        return "login";
    }

    @ModelAttribute("user")
    public AppUser addAttributes() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() != "anonymousUser") {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println(email);
            return userService.loadUserByUsername(email);
        }
        return null;
    }
}
