package it.finki.charitable.util;

import it.finki.charitable.entities.AppUser;
import it.finki.charitable.security.ConfirmationToken;
import it.finki.charitable.services.ConfirmationTokenService;
import it.finki.charitable.services.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutomaticEvents {

    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;

    public AutomaticEvents(UserService userService, ConfirmationTokenService confirmationTokenService) {
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteNonEnabledUsers() {
        List<ConfirmationToken> toDelete = confirmationTokenService.getAll();
        for(ConfirmationToken token : toDelete) {
            AppUser user = token.getUser();
            confirmationTokenService.deleteConfirmationToken(token);
            if(!user.getEnabled()) {
                userService.deleteUser(user);
            }
        }
    }
}
