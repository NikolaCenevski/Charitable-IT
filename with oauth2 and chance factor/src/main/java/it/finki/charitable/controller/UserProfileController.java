package it.finki.charitable.controller;

import it.finki.charitable.entities.*;
import it.finki.charitable.security.PasswordEncoder;
import it.finki.charitable.services.DonationPostService;
import it.finki.charitable.services.FundsCollectedService;
import it.finki.charitable.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UserProfileController {

    private final UserService userService;
    private final DonationPostService donationPostService;
    private final FundsCollectedService fundsCollectedService;

    public UserProfileController(UserService userService, DonationPostService donationPostService, FundsCollectedService fundsCollectedService) {
        this.userService = userService;
        this.donationPostService = donationPostService;
        this.fundsCollectedService = fundsCollectedService;
    }

    @RequestMapping("/userInformation")
    public String userInformation(Model model) {
        model.addAttribute("userInformation", true);
        return "myProfile";
    }

    @RequestMapping("/myDonations")
    public String myDonations(Model model) {
        model.addAttribute("myDonations", true);
        MainUser user = (MainUser) userService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        List<DonationInformation> donations = user.getDonationInformation();
        model.addAttribute("donations", donations);
        double total = donations.stream().mapToDouble(DonationInformation::getDonatedAmount).sum();
        model.addAttribute("total", total);
        return "myProfile";
    }

    @RequestMapping("/myPosts")
    public String myPosts(Model model) {
        AppUser user = (AppUser) model.getAttribute("user");
        List<DonationPost> posts = donationPostService.findAllByUser(user);
        model.addAttribute("postList", posts);
        model.addAttribute("myPosts", true);
        return "myProfile";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String changePassword(Model model,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword) {

        AppUser user = (AppUser) model.getAttribute("user");
        if(PasswordEncoder.bCryptPasswordEncoder().matches(oldPassword, user.getPassword())) {
            if(newPassword.equals(confirmPassword)) {
                user.setPassword(PasswordEncoder.bCryptPasswordEncoder().encode(newPassword));
                userService.saveUser(user);
                model.addAttribute("changedPassword", true);
                model.addAttribute("userInformation", true);
                return "myProfile";
            }
        }

        model.addAttribute("notChangedPassword", true);
        model.addAttribute("userInformation", true);
        return "myProfile";
    }

    @RequestMapping(value = "/changeCardInfo", method = RequestMethod.POST)
    public String changeCardInfo(Model model,
                                 @RequestParam String cardName,
                                 @RequestParam String cardNumber,
                                 @RequestParam String expiryDate,
                                 @RequestParam String cvv) {

        if(cardName.isEmpty() || cardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
            model.addAttribute("creditCardError", true);
            model.addAttribute("userInformation", true);
            return "myProfile";
        }

        MainUser user = (MainUser) model.getAttribute("user");
        user.setCreditCardInfo(cardName + "," + cardNumber + "," + expiryDate + "," + cvv);
        userService.saveUser(user);

        return "redirect:/userInformation";
    }

    @RequestMapping("/removeCardInfo")
    public String removeCardInfo(Model model) {
        MainUser user = (MainUser) model.getAttribute("user");
        user.setCreditCardInfo(null);
        userService.saveUser(user);
        return "redirect:/userInformation";
    }

    @RequestMapping(value = "/addFunds", method = RequestMethod.POST)
    public String addFunds(@RequestParam Long postid,
                           @RequestParam String type,
                           @RequestParam float amount) {

        DonationPost post = donationPostService.getById(postid);
        if(post.getUser().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            FundsCollected funds = new FundsCollected(type, amount);
            fundsCollectedService.save(funds);
            post.getFundsCollected().add(funds);
            post.setTotalFundsCollected(post.getTotalFundsCollected() + amount);
            post.setRiskFactor(getRisk(post));
            donationPostService.save(post);
        }
        return "redirect:/myPosts";
    }

    @ModelAttribute("user")
    public AppUser addAttributes() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() != "anonymousUser") {

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return userService.loadUserByUsername(email);
        }
        return null;
    }
    private Integer getRisk(DonationPost post) {
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
                float dailyAverage = post.getTotalFundsCollected() / (Duration.between(post.getCreatedAt().atTime(0, 0, 0), LocalDate.now().atTime(0, 0, 0)).toDays()+1);
                    float neededAverage = (post.getFundsNeeded() - post.getTotalFundsCollected()) / (Duration.between(LocalDate.now().atTime(0, 0, 0), post.getDateDue().atTime(0, 0, 0)).toDays()+((24-LocalDateTime.now().getHour())/24f));

                if(Duration.between(LocalDate.now().atTime(0, 0, 0), post.getDateDue().atTime(0, 0, 0)).toDays() == 0) {
                    float hour=(float) LocalDateTime.now().getHour();
                    float mins=(float) LocalDateTime.now().getMinute();
                    hour=hour+(mins/60f);
                    float hourlyAverage=(dailyAverage/24f);
                    float neededhourlyAverage=(post.getFundsNeeded() - post.getTotalFundsCollected())/(24f-hour);
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
        return risk;
    }
}

