package it.finki.charitable.util;

import it.finki.charitable.entities.AppUser;
import it.finki.charitable.entities.DonationPost;
import it.finki.charitable.entities.EmailMessage;
import it.finki.charitable.security.ConfirmationToken;
import it.finki.charitable.services.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AutomaticEvents {

    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final DonationPostService donationPostService;
    private final EmailMessageService emailMessageService;
    private final EmailService emailService;

    public AutomaticEvents(UserService userService, ConfirmationTokenService confirmationTokenService, DonationPostService donationPostService, EmailMessageService emailMessageService, EmailService emailService) {
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
        this.donationPostService = donationPostService;
        this.emailMessageService = emailMessageService;
        this.emailService = emailService;
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

    @Scheduled(cron = "0 0 0 * * *")
    public void setRisk() {
        System.out.println("cron");
        List<DonationPost> donationPosts = donationPostService.findAll();
        donationPosts = donationPosts.stream().filter(post -> {
            long daysToEnd = Duration.between(LocalDate.now().atTime(0, 0, 0), post.getDateDue().atTime(0, 0, 0)).toDays();
            long totalDays = Duration.between(post.getCreatedAt().atTime(0, 0, 0), post.getDateDue().atTime(0, 0, 0)).toDays();
            System.out.println(daysToEnd + " " + totalDays);

            if(totalDays < 10)
                return true;

            return (daysToEnd * 1f/totalDays) * 100 < 75;
        }).collect(Collectors.toList());

        donationPosts.forEach(post -> {
            int risk;
            if(post.getFundsNeeded() <= post.getTotalFundsCollected()) {
                risk = 102;
            }
            else
            {
                if (LocalDate.now().isAfter(post.getDateDue()))
                {
                    risk=0;
                }
                else
                {
                    float dailyAverage = post.getTotalFundsCollected() / (Duration.between(post.getCreatedAt().atTime(0, 0, 0), LocalDate.now().atTime(0, 0, 0)).toDays() + 1);
                    float neededAverage = (post.getFundsNeeded() - post.getTotalFundsCollected()) / (Duration.between(LocalDate.now().atTime(0, 0, 0), post.getDateDue().atTime(0, 0, 0)).toDays()+1);

                    if(Duration.between(LocalDate.now().atTime(0, 0, 0), post.getDateDue().atTime(0, 0, 0)).toDays() == 0) {
                        float hour=(float) LocalDateTime.now().getHour();
                        float mins=(float) LocalDateTime.now().getMinute();
                        hour=hour+(mins/(float)60);
                        float hourlyAverage=(dailyAverage/(float)24);
                        float neededhourlyAverage=(post.getFundsNeeded() - post.getTotalFundsCollected())/((float)24-hour);
                        risk = (int) (hourlyAverage/neededhourlyAverage*100);
                        if (risk>100)
                        {
                            risk=100;
                        }
                    }
                    else
                    {
                    System.out.println(dailyAverage + " " + neededAverage);
                    risk = (int) (dailyAverage / neededAverage * 100);

                    if(risk > 100) {
                        risk = 100;
                    }
                    }
            }
            }
            post.setRiskFactor(risk);
            donationPostService.save(post);
        });
    }

    @Scheduled(cron = "0 0 * * * *")
    public void sendMessages() {
        List<EmailMessage> messages = emailMessageService.findAll();
        for(EmailMessage message: messages) {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(message.getSendTo());
            simpleMailMessage.setSubject(message.getSubject());
            simpleMailMessage.setText(message.getText());
            emailService.sendMail(simpleMailMessage);
            emailMessageService.delete(message);
        }
    }
}
