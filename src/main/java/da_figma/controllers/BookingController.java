package da_figma.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.stripe.exception.StripeException;

import da_figma.dtos.BookingRequestDto;
import da_figma.dtos.BookingReviewDto;
import da_figma.services.BookingReviewService;
import da_figma.services.StripeService;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingReviewService bookingReviewService;

    @Autowired
    private StripeService stripeService;


    // =========================================================
    // RIEPILOGO NUOVA PRENOTAZIONE
    // =========================================================

    @PostMapping("/review")
    public String reviewBooking(@RequestBody BookingRequestDto request, Model model) {

        BookingReviewDto review = bookingReviewService.buildReview(request);

        model.addAttribute("review", review);

        model.addAttribute("bookingRequest", request);

        return "user/bookingReview";
    }


    // =========================================================
    // CONFERMA RIEPILOGO -> STRIPE
    // =========================================================

    @PostMapping("/confirm")
    public String confirmBooking( @ModelAttribute BookingRequestDto request)
            throws StripeException {


        String checkoutUrl = stripeService.createCheckoutSession(request);

        return "redirect:" + checkoutUrl;
    }
}
