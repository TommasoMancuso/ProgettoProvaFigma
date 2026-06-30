package da_figma.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import da_figma.dtos.BookingRequestDto;
import da_figma.models.PendingBooking;
import da_figma.repositories.PendingBookingRepository;
import da_figma.services.BookingManagerService;
import da_figma.services.PendingBookingService;

@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    private final PendingBookingRepository pendingRepository;

    private final PendingBookingService pendingService;

    private final BookingManagerService bookingManagerService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    public StripeWebhookController(
            PendingBookingRepository pendingRepository,
            PendingBookingService pendingService,
            BookingManagerService bookingManagerService) {

        this.pendingRepository = pendingRepository;
        this.pendingService = pendingService;
        this.bookingManagerService = bookingManagerService;
    }

    @PostMapping("/webhook")
        public String handleWebhook(
                @RequestBody String payload,
                @RequestHeader("Stripe-Signature") String sigHeader) {

            try {

                System.out.println("\n================ WEBHOOK =================");

                Event event = Webhook.constructEvent(
                        payload,
                        sigHeader,
                        endpointSecret);

                System.out.println("EVENT TYPE = " + event.getType());

                // Ci interessa soltanto checkout.session.completed
                if (!"checkout.session.completed".equals(event.getType())) {

                    System.out.println("EVENTO IGNORATO");
                    return "OK";
                }

                System.out.println("CHECKOUT SESSION COMPLETED RICEVUTO");

                // Stampo il JSON esatto della Session ricevuta
                String json =
                        event.getDataObjectDeserializer()
                                .getRawJson();

                System.out.println(json);

                Session session =
                        (Session) event
                                .getDataObjectDeserializer()
                                .deserializeUnsafe();

                System.out.println("SESSION ID = " + session.getId());

                System.out.println("PAYMENT STATUS = " + session.getPaymentStatus());

                System.out.println("METADATA = " + session.getMetadata());

                if (session.getMetadata() == null) {
                    throw new RuntimeException("Metadata nullo");
                }

                String pendingIdString =
                        session.getMetadata().get("pendingBookingId");

                System.out.println("pendingBookingId = " + pendingIdString);

                if (pendingIdString == null) {
                    throw new RuntimeException("pendingBookingId non trovato");
                }

                Long pendingId = Long.valueOf(pendingIdString);

                PendingBooking pending =
                        pendingRepository.findById(pendingId)
                                .orElseThrow();

                System.out.println("Pending trovato");

                BookingRequestDto request =
                        pendingService.getRequest(pending);

                System.out.println("BookingRequest ricostruita");

                bookingManagerService.createPaidBooking(
                                        request,
                                        pending.getUserKeycloakId());

                System.out.println("Booking salvata");

                pending.setPaymentStatus("PAID");

                pendingRepository.save(pending);

                System.out.println("Pending aggiornata");

                System.out.println("================ FINE WEBHOOK ================\n");

                return "OK";

            } catch (Exception e) {

                System.out.println("ERRORE WEBHOOK");

                e.printStackTrace();

                return "ERROR";
            }
        }           
}