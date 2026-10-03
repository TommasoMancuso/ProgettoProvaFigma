package da_figma.controllers;

import org.springframework.beans.factory.annotation.Value;
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


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public StripeWebhookController(
            PendingBookingRepository pendingRepository,
            PendingBookingService pendingService,
            BookingManagerService bookingManagerService) {

        this.pendingRepository = pendingRepository;
        this.pendingService = pendingService;
        this.bookingManagerService = bookingManagerService;
    }


    // =========================================================
    // STRIPE WEBHOOK
    // =========================================================

    @PostMapping("/webhook")
    public String handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {

            // =====================================================
            // VERIFICA WEBHOOK STRIPE
            // =====================================================

            Event event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    endpointSecret
            );


            // =====================================================
            // CI INTERESSA SOLO CHECKOUT COMPLETED
            // =====================================================

            if (!"checkout.session.completed".equals(
                    event.getType())) {

                return "OK";
            }


            // =====================================================
            // SESSION STRIPE
            // =====================================================

            Session session =
                    (Session) event
                            .getDataObjectDeserializer()
                            .deserializeUnsafe();


            // =====================================================
            // CONTROLLO PAGAMENTO
            // =====================================================

            if (!"paid".equalsIgnoreCase(
                    session.getPaymentStatus())) {

                return "OK";
            }


            // =====================================================
            // METADATA
            // =====================================================

            if (session.getMetadata() == null) {

                throw new RuntimeException(
                        "Metadata Stripe nullo");
            }


            String pendingIdString =
                    session.getMetadata()
                            .get("pendingBookingId");


            if (pendingIdString == null) {

                throw new RuntimeException(
                        "pendingBookingId non trovato "
                        + "nei metadata Stripe");
            }


            Long pendingId =
                    Long.valueOf(pendingIdString);


            // =====================================================
            // PENDING BOOKING
            // =====================================================

            PendingBooking pending =
                    pendingRepository.findById(pendingId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "PendingBooking non trovata: "
                                            + pendingId));


            // =====================================================
            // SICUREZZA
            // =====================================================

            if (pending.getStripeSessionId() == null) {

                throw new RuntimeException(
                        "Stripe Session ID mancante "
                        + "nella PendingBooking");
            }


            if (!pending.getStripeSessionId()
                    .equals(session.getId())) {

                throw new RuntimeException(
                        "Stripe Session ID non corrisponde "
                        + "alla PendingBooking");
            }


            // =====================================================
            // IDEMPOTENZA
            // =====================================================

            if ("PAID".equalsIgnoreCase(
                    pending.getPaymentStatus())) {

                return "OK";
            }


            // =====================================================
            // RECUPERO REQUEST DAL JSON
            // =====================================================

            BookingRequestDto request =
                    pendingService.getRequest(pending);


            // =====================================================
            // CREATE
            // =====================================================

            if ("CREATE".equalsIgnoreCase(
                    pending.getOperationType())) {

                bookingManagerService.createPaidBooking(
                        request,
                        pending.getUserKeycloakId()
                );


                pending.setPaymentStatus("PAID");

                pendingRepository.save(pending);


                return "OK";
            }


            // =====================================================
            // UPDATE
            // =====================================================

            if ("UPDATE".equalsIgnoreCase(
                    pending.getOperationType())) {

                if (pending.getBookingId() == null) {

                    throw new RuntimeException(
                            "PendingBooking UPDATE senza bookingId");
                }


                bookingManagerService.applyPendingModification(
                        pending.getBookingId(),
                        request,
                        pending.getUserKeycloakId(),
                        pending.getDifferenceAmount()
                );


                pending.setPaymentStatus("PAID");

                pendingRepository.save(pending);


                return "OK";
            }


            // =====================================================
            // OPERAZIONE NON RICONOSCIUTA
            // =====================================================

            throw new RuntimeException(
                    "OperationType non riconosciuto: "
                    + pending.getOperationType());


        } catch (Exception e) {

            e.printStackTrace();

            return "ERROR";
        }
    }
}
