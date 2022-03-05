package it.finki.charitable.controller;

import it.finki.charitable.entities.*;
import it.finki.charitable.services.*;
import it.finki.charitable.util.FileUploadUtil;
import org.dom4j.rule.Mode;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DonationPostController {

    private final DonationPostService donationPostService;
    private final UserService userService;
    private final FundsCollectedService fundsCollectedService;
    private final DonationInformationService donationInformationService;
    private final ReportPostService reportPostService;
    private final ReasonService reasonService;

    public DonationPostController(DonationPostService donationPostService, UserService userService, FundsCollectedService fundsCollectedService, DonationInformationService donationInformationService, ReportPostService reportPostService, ReasonService reasonService) {
        this.donationPostService = donationPostService;
        this.userService = userService;
        this.fundsCollectedService = fundsCollectedService;
        this.donationInformationService = donationInformationService;
        this.reportPostService = reportPostService;
        this.reasonService = reasonService;
    }

    @RequestMapping("/upload")
    public String upload() {
        return "upload";
    }

    @RequestMapping(value = "/newPost", method = RequestMethod.POST)
    public String newPost(Model model,
                          @RequestParam String title,
                          @RequestParam String fundsNeeded,
                          @RequestParam String currency,
                          @RequestParam String description,
                          @RequestParam String telekom,
                          @RequestParam String a1,
                          @RequestParam(defaultValue = "2020-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDue,
                          @RequestParam String bankAccount,
                          @RequestParam MultipartFile titleImage,
                          @RequestParam MultipartFile[] images,
                          @RequestParam MultipartFile[] moderatorImages) {

        System.out.println(moderatorImages.length);
        if(titleImage.isEmpty() || (moderatorImages.length == 1 && moderatorImages[0].isEmpty())) {
            model.addAttribute("error", true);
            return "upload";
        }

        if(title.isBlank() || fundsNeeded.isBlank() || currency.isBlank() || description.isBlank() || bankAccount.isBlank() || dateDue.equals(LocalDate.of(2020,1,1))) {
            model.addAttribute("error", true);
            return "upload";
        }
        if (dateDue.isBefore(LocalDate.now()))
        {
            model.addAttribute("error", true);
            return "upload";
        }

        DonationPost post = new DonationPost();
        post.setTitle(title);

        try {
            float funds = Float.parseFloat(fundsNeeded);
            if (funds <= 0) {
                model.addAttribute("error", true);
                return "upload";
            }
            post.setFundsNeeded(funds);
        } catch (NumberFormatException e) {
            model.addAttribute("error", true);
            return "upload";
        }

        post.setCurrency(currency);
        post.setDescription(description);
        post.setDateDue(dateDue);
        post.setBankAccount(bankAccount);
        post.setApproved(false);
        post.setCreatedAt(LocalDate.now());
        long totalDays = Duration.between(post.getCreatedAt().atTime(0, 0, 0), post.getDateDue().atTime(0, 0, 0)).toDays();
        if(totalDays < 10)
            post.setRiskFactor(0);

        List<String> phoneNumbers = Arrays.asList(telekom, a1);

        List<String> photos = new ArrayList<>();
        photos.add(StringUtils.cleanPath(Objects.requireNonNull(titleImage.getOriginalFilename())));
        Arrays.stream(images).filter(i -> !i.isEmpty()).forEach(i -> photos.add(StringUtils.cleanPath(Objects.requireNonNull(i.getOriginalFilename()))));

        List<MultipartFile> files = new ArrayList<>();
        files.add(titleImage);
        files.addAll(Arrays.stream(images).filter(i -> !i.isEmpty()).collect(Collectors.toList()));

        List<String> moderatorPhotos = new ArrayList<>();
        Arrays.stream(moderatorImages).forEach(i -> moderatorPhotos.add(StringUtils.cleanPath(Objects.requireNonNull(i.getOriginalFilename()))));

        post.setPhoneNumbers(phoneNumbers);
        post.setImages(photos);
        post.setModeratorImages(moderatorPhotos);

        AppUser user = (AppUser) model.getAttribute("user");
        post.setUser(user);

        DonationPost savedPost = donationPostService.save(post);

        for (MultipartFile file : files) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String uploadDir = "post-photos/" + savedPost.getId();
            try {
                FileUploadUtil.saveFile(uploadDir, fileName, file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        for (MultipartFile file : moderatorImages) {

            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String uploadDir = "moderator-photos/" + savedPost.getId();
            try {
                FileUploadUtil.saveFile(uploadDir, fileName, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "upload";
    }

    @RequestMapping("/album")
    public String album(Model model,
                        @RequestParam int page,
                        @RequestParam String sort,
                        @RequestParam(required = false, defaultValue = "desc") String order,
                        @RequestParam(required = false, defaultValue = "all") String groupBy) {

        Sort s = Sort.by(sort);
        s = order.equals("asc") ? s.ascending() : s.descending();
        Pageable pageable = PageRequest.of(page - 1, 6, s);
        Page<DonationPost> postList;
        postList = donationPostService.findPaginated(page, 6, sort, order, true);

        if (!groupBy.equals("all")) {
            List<DonationPost> allPosts = donationPostService.findAllByApproved(true);

            if (sort.equals("title")) {
                if (order.equals("asc")) {
                    allPosts.sort(Comparator.comparing(DonationPost::getTitle, (s1, s2) -> s1.compareToIgnoreCase(s2)));
                } else {
                    allPosts.sort(Comparator.comparing(DonationPost::getTitle, (s1, s2) -> s2.compareToIgnoreCase(s1)));
                }
            } else if (sort.equals("dateDue")) {
                if (order.equals("asc")) {
                    allPosts.sort(Comparator.comparing(DonationPost::getDateDue));
                } else {
                    allPosts.sort(Comparator.comparing(DonationPost::getDateDue).reversed());
                }
            } else if (sort.equals("fundsNeeded")) {
                if (order.equals("asc")) {
                    allPosts.sort(Comparator.comparing(DonationPost::getFundsNeeded));
                } else {
                    allPosts.sort(Comparator.comparing(DonationPost::getFundsNeeded).reversed());
                }
            } else if (sort.equals("riskFactor")) {
                if (order.equals("asc")) {
                    allPosts.sort(Comparator.comparing(DonationPost::getRiskFactor));
                } else {
                    allPosts.sort(Comparator.comparing(DonationPost::getRiskFactor).reversed());
                }
            }

            if (groupBy.equals("completed")) {
                List<DonationPost> completed = allPosts.stream()
                        .filter(post -> post.getTotalFundsCollected() >= post.getFundsNeeded())
                        .collect(Collectors.toList());

                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), completed.size());
                if (start <= end) {
                    postList = new PageImpl<>(completed.subList(start, end), pageable, completed.size());
                }
            } else if (groupBy.equals("expired")) {
                List<DonationPost> expired = allPosts.stream().filter(post -> {
                    double fundsCollected = post.getFundsCollected().stream().mapToDouble(FundsCollected::getFunds).sum();
                    return LocalDate.now().isAfter(post.getDateDue()) && fundsCollected < post.getFundsNeeded();
                }).collect(Collectors.toList());

                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), expired.size());
                if (start <= end) {
                    postList = new PageImpl<>(expired.subList(start, end), pageable, expired.size());
                }
            }
        }

        if (postList.getTotalElements() == 0) {
            model.addAttribute("noPosts", true);
            return "album";
        }
        model.addAttribute("totalPages", postList.getTotalPages());
        model.addAttribute("postList", postList);
        return "album";
    }

    @RequestMapping("/post")
    public String showPost(Model model, @RequestParam Long postid) {
        DonationPost post = donationPostService.getById(postid);
        if (post == null) {
            model.addAttribute("notFound", true);
            return "post";
        }

        AppUser currentUser = (AppUser) model.getAttribute("user");
        if (post.getApproved() || (post.getUser().getUsername().equals(currentUser.getUsername()) && !post.getApproved())) {
            AppUser user = post.getUser();
            Moderator moderator = post.getModerator();
            model.addAttribute("post", post);
            model.addAttribute("createdByFirstName", user.getFirstName());
            model.addAttribute("createdByLastName", user.getLastName());
            if (moderator != null) {
                model.addAttribute("moderatorFirstName", moderator.getFirstName());
                model.addAttribute("moderatorLastName", moderator.getLastName());
            }
        } else {
            model.addAttribute("notFound", true);
        }
        return "post";
    }

    @RequestMapping(value="/donate", method = RequestMethod.POST)
    public String donate(Model model, @RequestParam Long postid,
                         @RequestParam String cardName,
                         @RequestParam String cardNumber,
                         @RequestParam String expiryDate,
                         @RequestParam String cvv,
                         @RequestParam String amount) {

        DonationPost post = donationPostService.getById(postid);
        if(post == null || !post.getApproved()) {
            return "index";
        }

        float donatedAmount;
        try {
            donatedAmount = Float.parseFloat(amount);
            if (donatedAmount <= 0) {
                return String.format("redirect:/post?postid=%d&error", postid);
            }
        } catch (NumberFormatException e) {
            return String.format("redirect:/post?postid=%d&error", postid);
        }

        FundsCollected funds = new FundsCollected("Online donation", donatedAmount);
        fundsCollectedService.save(funds);

        post.getFundsCollected().add(funds);
        post.setTotalFundsCollected(post.getTotalFundsCollected() + donatedAmount);

        if(post.getRiskFactor() != 101 && post.getRiskFactor() != 102) {
            post.setRiskFactor(getRisk(post));
        }

        donationPostService.save(post);

        DonationInformation donationInformation = new DonationInformation(donatedAmount, post.getId(), post.getTitle());
        donationInformationService.save(donationInformation);
        MainUser user = (MainUser) userService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        user.getDonationInformation().add(donationInformation);
        userService.saveUser(user);

        return String.format("redirect:/post?postid=%d", postid);
    }

    @RequestMapping(value="/report", method = RequestMethod.POST)
    public String report(@RequestParam Long postid,
                         @RequestParam String description,
                         Model model) {

        DonationPost donationPost = donationPostService.getById(postid);
        ReportPost reportPost = reportPostService.findByDonationPost(donationPost);
        if (reportPost == null) {
            reportPost = new ReportPost();
            reportPost.setDonationPost(donationPost);
        }

        Reason reason = new Reason();
        AppUser user = (AppUser) model.getAttribute("user");
        reason.setUser(user);
        reason.setDescription(description);
        reasonService.save(reason);
        reportPost.getReasons().add(reason);
        reportPost.setNumReports(reportPost.getNumReports() + 1);
        reportPostService.save(reportPost);
        return String.format("redirect:/post?postid=%d", postid);
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
        return risk;
    }
}
