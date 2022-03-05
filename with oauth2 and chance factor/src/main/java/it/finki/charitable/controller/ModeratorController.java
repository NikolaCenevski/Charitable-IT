package it.finki.charitable.controller;

import it.finki.charitable.entities.*;
import it.finki.charitable.services.DonationPostService;
import it.finki.charitable.services.EmailService;
import it.finki.charitable.services.ReasonService;
import it.finki.charitable.services.ReportPostService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ModeratorController {

    private final DonationPostService donationPostService;
    private final EmailService emailService;
    private final ReportPostService reportPostService;
    private final ReasonService reasonService;

    public ModeratorController(DonationPostService donationPostService, EmailService emailService, ReportPostService reportPostService, ReasonService reasonService) {
        this.donationPostService = donationPostService;
        this.emailService = emailService;
        this.reportPostService = reportPostService;
        this.reasonService = reasonService;
    }

    @RequestMapping("moderator/approval")
    public String approval(Model model,
                           @RequestParam int page,
                           @RequestParam String sort,
                           @RequestParam(required = false,defaultValue = "") String order) {
        Page<DonationPost> postList = donationPostService.findPaginated(page, 6, sort, order, false);
        if (postList.getTotalElements() == 0) {
            model.addAttribute("noPosts", true);
            return "postApproval";
        }
        model.addAttribute("totalPages", postList.getTotalPages());
        model.addAttribute("postList", postList);
        return "postApproval";
    }

    @RequestMapping("moderator/post")
    public String post(Model model, @RequestParam Long postid) {
        DonationPost post = donationPostService.getById(postid);
        if (post == null) {
            model.addAttribute("notFound", true);
            return "post";
        }
        AppUser user = post.getUser();
        model.addAttribute("post", post);
        model.addAttribute("createdByFirstName", user.getFirstName());
        model.addAttribute("createdByLastName", user.getLastName());
        if (post.getApproved()) {
            model.addAttribute("approved", true);
        }
        return "moderatorPost";
    }

    @RequestMapping("/moderator/approvePost")
    public String approvePost(@RequestParam Long postid) {
        DonationPost post = donationPostService.getById(postid);
        post.setApproved(true);
        post.setModerator((Moderator) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        donationPostService.save(post);
        emailService.sendApprovalEmail(post.getUser().getEmail(), "CharitAbleMk: " + post.getTitle() + " has been approved", postid);
        return "redirect:/moderator/approval?page=1&sort=id";
    }

    @RequestMapping("/moderator/dontApprove")
    public String dontApprove(@RequestParam Long postid,
                              @RequestParam String description) {
        DonationPost post = donationPostService.getById(postid);
        emailService.sendNoApprovalEmail(post.getUser().getEmail(), "CharitAbleMk: " + post.getTitle() + " has not been approved", description);
        deleteDonationPost(post);
        return "redirect:/moderator/approval?page=1&sort=id";
    }

    @RequestMapping("/moderator/myApprovedPosts")
    public String myApprovedPosts(Model model,
                                  @RequestParam int page,
                                  @RequestParam String sort,
                                  @RequestParam(required = false,defaultValue = "") String order) {
        Moderator moderator = (Moderator) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<DonationPost> postList = donationPostService.findAllByModerator(page, 6, sort, order, moderator);
        if (postList.getTotalElements() == 0) {
            model.addAttribute("noPosts", true);
            return "postApproval";
        }
        model.addAttribute("totalPages", postList.getTotalPages());
        model.addAttribute("postList", postList);
        return "postApproval";
    }

    @RequestMapping("/moderator/report")
    public String reportedPosts(Model model,
                                @RequestParam int page,
                                @RequestParam String sort,
                                @RequestParam(required = false,defaultValue = "") String order) {
        Page<ReportPost> postList = reportPostService.findAll(page, 6, sort, order);
        if (postList.getTotalElements() == 0) {
            model.addAttribute("noPosts", true);
            return "reportedPosts";
        }
        model.addAttribute("totalPages",postList.getTotalPages());
        model.addAttribute("postList", postList);
        return "reportedPosts";
    }

    @RequestMapping("/moderator/reportPost")
    public String report(@RequestParam Long postid, Model model) {
        ReportPost post = reportPostService.findById(postid);
        model.addAttribute("post", post);
        DonationPost donationPost = post.getDonationPost();
        AppUser user = donationPost.getUser();
        model.addAttribute("createdByFirstName", user.getFirstName());
        model.addAttribute("createdByLastName", user.getLastName());
        model.addAttribute("report", true);
        Moderator moderator = post.getDonationPost().getModerator();
        model.addAttribute("moderatorFirstName", moderator.getFirstName());
        model.addAttribute("moderatorLastName", moderator.getLastName());

        return "reportPost";
    }

    @RequestMapping("/moderator/dismiss")
    public String dismiss(@RequestParam Long postid) {
        ReportPost post = reportPostService.findById(postid);
        deleteReportPost(post);
        return "redirect:/moderator/report?page=1&sort=id";
    }

    @RequestMapping("/moderator/deletePost")
    public String deletePost(@RequestParam Long postid,
                             @RequestParam String description) {
        ReportPost post = reportPostService.findById(postid);
        DonationPost donationPost = post.getDonationPost();
        emailService.sendDeletionEmail(donationPost.getUser().getEmail(), "CharitAbleMk: " + donationPost.getTitle() + " has been deleted", description);
        deleteReportPost(post);
        deleteDonationPost(donationPost);
        return "redirect:/moderator/approval?page=1&sort=id";
    }

    public void deleteDonationPost(DonationPost donationPost) {
        List<String> fileForDeletion = donationPost.getPhotosForDeletion();
        for (String f : fileForDeletion) {
            File file = new File(f);
            file.delete();
        }
        donationPostService.delete(donationPost);
    }

    public void deleteReportPost(ReportPost reportPost) {
        List<Reason> reasons = new ArrayList<>(reportPost.getReasons());
        if (reportPost.getReasons().size() > 0) {
            reportPost.getReasons().subList(0, reportPost.getReasons().size()).clear();
        }
        reportPostService.save(reportPost);
        for(Reason r: reasons) {
            reasonService.delete(r);
        }
        reportPostService.delete(reportPost);
    }

    @ModelAttribute("user")
    public AppUser addAttributes() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() != "anonymousUser") {
            return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }
}
