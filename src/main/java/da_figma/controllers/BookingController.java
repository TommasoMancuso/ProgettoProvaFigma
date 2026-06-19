package da_figma.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import da_figma.dtos.BookingRequestDto;
import da_figma.dtos.BookingReviewDto;
import da_figma.models.Booking;
import da_figma.services.BookingManagerService;
import da_figma.services.BookingReviewService;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingReviewService bookingReviewService;

    @Autowired
    private BookingManagerService bookingManagerService;

    @PostMapping("/review")
    public String reviewBooking(@RequestBody BookingRequestDto request, Model model) {

        BookingReviewDto review = bookingReviewService.buildReview(request);

        model.addAttribute("review", review);

        model.addAttribute( "bookingRequest", request);

        return "user/bookingReview";
    }

    @PostMapping("/confirm")
    public String confirmBooking(@ModelAttribute BookingRequestDto request) {

        Booking booking = bookingManagerService.createBooking(request);

        return "redirect:/booking/success/" + booking.getId();
    }
}
