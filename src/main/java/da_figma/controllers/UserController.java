package da_figma.controllers;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.stripe.exception.StripeException;

import da_figma.dtos.BookingDetailDto;
import da_figma.dtos.BookingRequestDto;
import da_figma.dtos.BookingReviewDto;
import da_figma.enums.BookingPriceDifferenceType;
import da_figma.models.Booking;
import da_figma.repositories.BookingRepository;
import da_figma.services.BookingFacadeService;
import da_figma.services.BookingManagerService;
import da_figma.services.BookingReviewService;
import da_figma.services.CircuitService;
import da_figma.services.EventService;
import da_figma.services.StripeService;
import da_figma.services.TeamUserService;


@Controller
public class UserController {

    @Autowired
    private TeamUserService teamUserService; 

    @Autowired
    private EventService eventService;

    @Autowired
    private CircuitService circuitService;

    @Autowired
    private BookingFacadeService bookingFacadeService;

    @Autowired 
    private BookingManagerService bookingManagerService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingReviewService bookingReviewService;

    @Autowired
    private StripeService stripeService;



    // =========================
    // PUBLIC PAGES
    // =========================

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("circuits", circuitService.getActiveCircuits());
        return "home";
    }

    @GetMapping("/services")
    public String services(Model model) {

        model.addAttribute("circuits", circuitService.getActiveCircuits());
        return "services";
    }

    @GetMapping("/aboutus")
    public String aboutUs(Model model) {

        model.addAttribute("users", teamUserService.getAllUsers());

        return "aboutus";
    }

    // ROTTA PER ENTRARE NELLA PAGINA DEGLI EVENTI
    @GetMapping("/events")
    public String events(Model model){

        model.addAttribute("events", eventService.getAllEvents());

        return "events";
    }

    // ROTTA PER PAGINA DI DETTAGLIO EVENTI
    @GetMapping("/user/eventDetail/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {

        model.addAttribute("event", eventService.getEventById(id));
        
        return "user/eventDetail";
    }

    // ROTTA PER FORM PRENOTAZIONE
    @GetMapping("/user/booking/{eventId}")
    public String bookingForm(@PathVariable Long eventId, Model model) {

        model.addAttribute("bookingForm", bookingFacadeService.buildBookingForm(eventId));

        model.addAttribute("bookingRequest", new BookingRequestDto());

        return "user/bookingForm";
    }

    // ROTTA PER LE TUE PRENOTAZIONI
    @GetMapping ("/user/myBookings")
    public String myBookings(Model model){

        model.addAttribute("bookings", bookingManagerService.getMyBookings());

        return "user/bookings/myBookings";
    }

    // Dettaglio proprie prenotazioni
    @GetMapping("/user/myBookingDetail/{id}")
    public String myBookingDetail(@PathVariable Long id, Model model){

        model.addAttribute("booking", 
                            bookingManagerService.getBookingDetail(id));

        return "user/bookings/myBookingDetail";
    }

    // RAGGIUNGI FORM DI MODIFICA PROPRIA PRENOTAZIONE
    @GetMapping("/user/myBookingEdit/{id}")
    public String myBookingEdit(@PathVariable Long id, Model model) {

        BookingDetailDto booking =
                bookingManagerService.getBookingDetail(id);

        Long eventId =
                booking.getBooking().getEventId();

        model.addAttribute(
                "bookingForm",
                bookingFacadeService.buildBookingFormForEdit(
                        eventId,
                        id
                )
        );

        model.addAttribute(
                "bookingRequest",
                bookingManagerService.buildBookingRequestForEdit(id)
        );

        // ID della prenotazione che stiamo modificando
        model.addAttribute(
                "bookingId",
                id
        );

        return "user/bookings/myBookingEdit";
    }



    // VAI A PAGINA RIEPILOGO MODIFICA
   @PostMapping("/user/myBookingUpdate/{id}")
    public String myBookingUpdate(
                                    @PathVariable Long id,
                                    @RequestBody BookingRequestDto request,
                                    Model model) {

        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Prenotazione non trovata"));

        BookingReviewDto review = bookingReviewService.buildModificationReview(id, request);

        model.addAttribute("bookingId", id);
        model.addAttribute("bookingRequest", request);
        model.addAttribute("review", review);
        model.addAttribute("currentTotalPrice", booking.getTotalPrice());

        return "user/bookings/myBookingReview";
    }

    // CONFERMA DOPO IL RIEPILGO E RINVIO A STRIPE
    @PostMapping("/user/myBookingUpdate/{id}/confirm")
    public String confirmMyBookingUpdate(
                                        @PathVariable Long id, Model model,
                                        @ModelAttribute BookingRequestDto request)
                                        throws StripeException {

        // =====================================================
        // TIPO DIFFERENZA PREZZO
        // =====================================================

        BookingPriceDifferenceType differenceType = 
                    bookingManagerService.determineBookingPriceDifference(id, request);

        // =====================================================
        // PREZZO AUMENTATO
        // =====================================================

        if (differenceType == BookingPriceDifferenceType.INCREASE) {

            String checkoutUrl =
                    stripeService.createModificationCheckoutSession(id, request);

            return "redirect:" + checkoutUrl;
        }

        // =====================================================
        // PREZZO UGUALE
        // =====================================================

        if (differenceType == BookingPriceDifferenceType.NO_CHANGE) {

            bookingManagerService.updateBooking(id, request);

            model.addAttribute("bookingId", id);
            model.addAttribute("difference", BigDecimal.ZERO);

            return "user/bookings/modificationSuccess";
        }



        // =====================================================
        // PREZZO DIMINUITO
        // =====================================================

        if (differenceType == BookingPriceDifferenceType.DECREASE) {

            BigDecimal difference =
                    bookingManagerService.calculateBookingModificationDifference(id,  request);

            bookingManagerService.applyBookingCredit(id, request, difference);

            model.addAttribute("bookingId", id);

            model.addAttribute("difference", difference);

            return "user/bookings/modificationSuccess";
        }


        // =====================================================
        // CASO IMPOSSIBILE
        // =====================================================

        throw new IllegalStateException(
                "Tipo di differenza prezzo non gestito"
        );
    }

    // CONFERMA MODIFICA PRENOTAZIONE
    @GetMapping("/user/myBookingModificationSuccess/{id}")
    public String myBookingModificationSuccess(
            @PathVariable Long id,
            Model model) {

        model.addAttribute("bookingId", id);

        return "user/bookings/modificationSuccess";
    }


    // Soft delete propria prenotazione
    @GetMapping("/user/myBookingDelete/{id}")
    public String myBookingDelete(){
        
        return "redirect:/user/myBookings";
    }
    

}