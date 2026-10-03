package da_figma.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import da_figma.dtos.BookingRequestDto;
import da_figma.dtos.BookingServiceRequestDto;
import da_figma.models.Booking;
import da_figma.models.Event;
import da_figma.models.PendingBooking;
import da_figma.models.TeamService;
import da_figma.repositories.BookingRepository;
import da_figma.repositories.EventRepository;
import da_figma.repositories.TeamServiceRepository;

@Service
public class StripeService {

    @Autowired
    private PendingBookingService pendingBookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private BookingPricingService bookingPricingService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TeamServiceRepository serviceRepository;

    @Autowired
    private BookingManagerService bookingManagerService;


    // =========================================================
    // NUOVA PRENOTAZIONE
    // =========================================================

    public String createCheckoutSession(
            BookingRequestDto request)
            throws StripeException {

        System.out.println("\n==========================================");
        System.out.println("ENTRATO IN StripeService.createCheckoutSession()");
        System.out.println("==========================================");

        System.out.println("EVENT ID = " + request.getEventId());

        System.out.println(
                "PAYMENT METHOD = " + request.getPaymentMethod());


        // =====================================================
        // CONTROLLO SERVIZI
        // =====================================================

        if (request.getServices() == null) {

            System.out.println("SERVICES = NULL");

        } else {

            System.out.println(
                    "NUMERO SERVIZI = "
                    + request.getServices().size());

            for (BookingServiceRequestDto serviceRequest
                    : request.getServices()) {

                System.out.println("--------------------------------");
                System.out.println(
                        "SERVICE ID = "
                        + serviceRequest.getServiceId());

                System.out.println(
                        "BIKE ID = "
                        + serviceRequest.getBikeId());

                System.out.println(
                        "DATE = "
                        + serviceRequest.getSelectedDates());
            }
        }


        // =====================================================
        // EVENTO
        // =====================================================

        Event event =
                eventRepository.findById(
                        request.getEventId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Evento non trovato"));

        System.out.println(
                "EVENT TROVATO = " + event.getName());


        // =====================================================
        // UTENTE
        // =====================================================

        String keycloakId =
                userContextService.getCurrentKeycloakId();

        System.out.println(
                "KEYCLOAK USER = " + keycloakId);


        // =====================================================
        // CALCOLO PREZZO
        // =====================================================

        BigDecimal totalPrice =
                bookingPricingService.calculatePrice(request);

        System.out.println(
                "PREZZO TOTALE = " + totalPrice);


        // =====================================================
        // PENDING BOOKING
        // =====================================================

        PendingBooking pendingBooking =
                pendingBookingService.create(
                        keycloakId,
                        request,
                        totalPrice);

        System.out.println("\nPENDING BOOKING CREATA");

        System.out.println(
                "ID = " + pendingBooking.getId());

        System.out.println(
                "BOOKING JSON = "
                + pendingBooking.getBookingJson());


        // =====================================================
        // CHECKOUT STRIPE
        // =====================================================

        SessionCreateParams.Builder params =
                SessionCreateParams.builder()

                        .setMode(
                                SessionCreateParams.Mode.PAYMENT)

                        .setCustomerEmail(
                                userContextService
                                        .getCurrentEmail())

                        .setSuccessUrl(
                                "http://localhost:8080/payment/success")

                        .setCancelUrl(
                                "http://localhost:8080/payment/cancel");


        // =====================================================
        // METADATA
        // =====================================================

        params.putMetadata(
                "pendingBookingId",
                pendingBooking.getId().toString());


        // =====================================================
        // METODO DI PAGAMENTO
        // =====================================================

        if ("KLARNA".equalsIgnoreCase(
                request.getPaymentMethod())) {

            params.addPaymentMethodType(
                    SessionCreateParams
                            .PaymentMethodType.KLARNA);

            System.out.println(
                    "METODO PAGAMENTO = KLARNA");

        } else {

            params.addPaymentMethodType(
                    SessionCreateParams
                            .PaymentMethodType.CARD);

            System.out.println(
                    "METODO PAGAMENTO = CARD");
        }


        // =====================================================
        // LINE ITEMS
        // =====================================================

        for (BookingServiceRequestDto serviceRequest
                : request.getServices()) {

            TeamService service =
                    serviceRepository.findById(
                            serviceRequest.getServiceId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Servizio non trovato"));

            BigDecimal price =
                    bookingPricingService
                            .calculateSingleServicePrice(
                                    event,
                                    serviceRequest);

            System.out.println("--------------------------------");
            System.out.println(
                    "SERVIZIO = " + service.getName());

            System.out.println(
                    "PREZZO = " + price);


            params.addLineItem(

                    SessionCreateParams.LineItem.builder()

                            .setQuantity(1L)

                            .setPriceData(

                                    SessionCreateParams
                                            .LineItem
                                            .PriceData
                                            .builder()

                                            .setCurrency("eur")

                                            .setUnitAmount(
                                                    price
                                                            .multiply(
                                                                    BigDecimal
                                                                            .valueOf(100))
                                                            .longValue()
                                            )

                                            .setProductData(

                                                    SessionCreateParams
                                                            .LineItem
                                                            .PriceData
                                                            .ProductData
                                                            .builder()

                                                            .setName(
                                                                    service.getName())

                                                            .build()
                                            )

                                            .build()
                            )

                            .build()
            );
        }


        // =====================================================
        // CREAZIONE SESSIONE
        // =====================================================

        System.out.println(
                "\nCREAZIONE CHECKOUT SESSION...");

        Session session =
                Session.create(params.build());

        System.out.println(
                "CHECKOUT SESSION CREATA");

        System.out.println(
                "SESSION ID = " + session.getId());

        System.out.println(
                "SESSION URL = " + session.getUrl());


        // =====================================================
        // SALVATAGGIO STRIPE SESSION ID
        // =====================================================

        pendingBooking.setStripeSessionId(
                session.getId());

        pendingBookingService.save(
                pendingBooking);


        System.out.println(
                "SESSION ID SALVATA NELLA PendingBooking");

        System.out.println(
                "==========================================");

        System.out.println(
                "FINE StripeService");

        System.out.println(
                "==========================================\n");


        return session.getUrl();
    }


    // =========================================================
    // MODIFICA PRENOTAZIONE
    // =========================================================

    // =========================================================
// MODIFICA PRENOTAZIONE - AUMENTO DI PREZZO
// =========================================================

        public String createModificationCheckoutSession(
                Long bookingId,
                BookingRequestDto request)
                throws StripeException {

        System.out.println("\n==========================================");
        System.out.println(
                "ENTRATO IN StripeService."
                + "createModificationCheckoutSession()");
        System.out.println("==========================================");

        // =====================================================
        // UTENTE
        // =====================================================

        String keycloakId =
                userContextService.getCurrentKeycloakId();

        // =====================================================
        // EVENTO
        // =====================================================

        Event event =
                eventRepository.findById(
                        request.getEventId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Evento non trovato"));

        // =====================================================
        // PRENOTAZIONE ESISTENTE
        // =====================================================

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Prenotazione non trovata"));

        // =====================================================
        // SICUREZZA
        // =====================================================

        if (!booking.getUserKeycloakId().equals(keycloakId)) {

                throw new IllegalArgumentException(
                        "Non puoi modificare questa prenotazione");
        }

        // =====================================================
        // PREZZO ATTUALE
        // =====================================================

        BigDecimal originalTotal =
                booking.getTotalPrice();

        // =====================================================
        // NUOVO PREZZO
        // =====================================================

        BigDecimal newTotal =
                bookingPricingService.calculatePrice(request);

        // =====================================================
        // DIFFERENZA
        // =====================================================

        BigDecimal difference =
                newTotal.subtract(originalTotal);

        // =====================================================
        // CONTROLLO
        // =====================================================

        if (difference.compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                        "Questa sessione Stripe può essere creata "
                        + "solo per un aumento di prezzo");
        }

        System.out.println("--------------------------------");
        System.out.println(
                "PREZZO ORIGINALE = " + originalTotal);

        System.out.println(
                "NUOVO PREZZO = " + newTotal);

        System.out.println(
                "DIFFERENZA DA PAGARE = " + difference);

        // =====================================================
        // CREA PENDING BOOKING
        // =====================================================

        PendingBooking pending =
                pendingBookingService.createModification(
                        keycloakId,
                        bookingId,
                        request,
                        originalTotal,
                        newTotal,
                        difference
                );

       
        // =====================================================
        // CHECKOUT STRIPE
        // =====================================================

        SessionCreateParams.Builder params =
                SessionCreateParams.builder()

                        .setMode(
                                SessionCreateParams.Mode.PAYMENT)

                        .setCustomerEmail(
                                userContextService
                                        .getCurrentEmail())

                        .setSuccessUrl(
                                "http://localhost:8080/payment/success")

                        .setCancelUrl(
                                "http://localhost:8080/payment/cancel");

        // =====================================================
        // METADATA
        // =====================================================

        params.putMetadata(
                "pendingBookingId",
                pending.getId().toString());

        // =====================================================
        // METODO DI PAGAMENTO
        // =====================================================

        if ("KLARNA".equalsIgnoreCase(
                request.getPaymentMethod())) {

                params.addPaymentMethodType(
                        SessionCreateParams
                                .PaymentMethodType.KLARNA);

        } else {

                params.addPaymentMethodType(
                        SessionCreateParams
                                .PaymentMethodType.CARD);
        }

        // =====================================================
        // LINE ITEM
        // =====================================================

        params.addLineItem(

                SessionCreateParams.LineItem.builder()

                        .setQuantity(1L)

                        .setPriceData(

                                SessionCreateParams
                                        .LineItem
                                        .PriceData
                                        .builder()

                                        .setCurrency("eur")

                                        .setUnitAmount(
                                                difference
                                                        .multiply(
                                                                BigDecimal
                                                                        .valueOf(100))
                                                        .longValue()
                                        )

                                        .setProductData(

                                                SessionCreateParams
                                                        .LineItem
                                                        .PriceData
                                                        .ProductData
                                                        .builder()

                                                        .setName(
                                                                "Differenza modifica "
                                                                + "prenotazione #"
                                                                + bookingId)

                                                        .build()
                                        )

                                        .build()
                        )

                        .build()
        );

        // =====================================================
        // CREAZIONE SESSIONE STRIPE
        // =====================================================

        System.out.println(
                "CREAZIONE CHECKOUT STRIPE...");

        Session session =
                Session.create(params.build());

        System.out.println(
                "CHECKOUT STRIPE CREATO.");

        System.out.println(
                "SESSION ID = " + session.getId());

        // =====================================================
        // SALVATAGGIO DATI STRIPE
        // =====================================================

        pending.setStripeSessionId(
                session.getId());

        pending.setPaymentStatus("PENDING");

        pendingBookingService.save(pending);

        System.out.println(
                "PendingBooking salvata con stato PENDING.");

        System.out.println(
                "==========================================");

        System.out.println(
                "FINE StripeService");

        System.out.println(
                "==========================================\n");

        return session.getUrl();
        }

}