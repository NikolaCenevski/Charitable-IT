package it.finki.charitable.security;

import it.finki.charitable.entities.AppUser;
import it.finki.charitable.entities.MainUser;
import it.finki.charitable.entities.UserRole;
import it.finki.charitable.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class O2AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;

    public O2AuthSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        UserO2Auth userO2Auth = (UserO2Auth) authentication.getPrincipal();
        String email = userO2Auth.getName();
        AppUser user = userService.loadUserByUsername(email);
        if(user == null) {
            AppUser newUser = new MainUser();
            String[] name = userO2Auth.getAttribute("name").toString().split(" ");
            newUser.setFirstName(name[0]);
            newUser.setLastName(name[1]);
            newUser.setEmail(email);
            newUser.setPassword(PasswordEncoder.bCryptPasswordEncoder().encode(UUID.randomUUID().toString()));
            newUser.setUserRole(UserRole.USER);
            newUser.setEnabled(true);
            userService.saveUser(newUser);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
