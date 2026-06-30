package da_figma.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import da_figma.dtos.BookingRequestDto;
import da_figma.dtos.BookingServiceRequestDto;
import da_figma.models.Event;
import da_figma.models.PendingBooking;
import da_figma.models.TeamService;
import da_figma.repositories.EventRepository;
import da_figma.repositories.TeamServiceRepository;

@Service
public class StripeService {

    @Autowired
    private PendingBookingService pendingBookingService;

    private final UserContextService userContextService;
    private final BookingPricingService bookingPricingService;
    private final EventRepository eventRepository;
    private final TeamServiceRepository serviceRepository;

    public StripeService(
            UserContextService userContextService,
            BookingPricingService bookingPricingService,
            EventRepository eventRepository,
            TeamServiceRepository serviceRepository) {

        this.userContextService = userContextService;
        this.bookingPricingService = bookingPricingService;
        this.eventRepository = eventRepository;
        this.serviceRepository = serviceRepository;
    }

    public String createCheckoutSession(
            BookingRequestDto request)
            throws StripeException {

        System.out.println("\n==========================================");
        System.out.println("ENTRATO IN StripeService.createCheckoutSession()");
        System.out.println("==========================================");

        System.out.println("EVENT ID = " + request.getEventId());

        System.out.println("PAYMENT METHOD = " + request.getPaymentMethod());

        if (request.getServices() == null) {

            System.out.println("SERVICES = NULL");

        } else {

            System.out.println("NUMERO SERVIZI = " + request.getServices().size());

            for (BookingServiceRequestDto s : request.getServices()) {

                System.out.println("--------------------------------");
                System.out.println("SERVICE ID = " + s.getServiceId());
                System.out.println("BIKE ID = " + s.getBikeId());
                System.out.println("DATE = " + s.getSelectedDates());
            }
        }

        Event event =
                eventRepository.findById(request.getEventId())
                        .orElseThrow();

        System.out.println("EVENT TROVATO = " + event.getName());

        String keycloakId =
                userContextService.getCurrentKeycloakId();

        System.out.println("KEYCLOAK USER = " + keycloakId);

        BigDecimal totalPrice =
                bookingPricingService.calculatePrice(request);

        System.out.println("PREZZO TOTALE = " + totalPrice);

        PendingBooking pendingBooking =
                pendingBookingService.create(
                        keycloakId,
                        request,
                        totalPrice);

        System.out.println("\nPENDING BOOKING CREATA");
        System.out.println("ID = " + pendingBooking.getId());

        System.out.println("BOOKING JSON:");
        System.out.println(pendingBooking.getBookingJson());

        SessionCreateParams.Builder params =
                SessionCreateParams.builder()

                        .setMode(
                                SessionCreateParams.Mode.PAYMENT)

                        .setCustomerEmail(
                                userContextService.getCurrentEmail())

                        .setSuccessUrl(
                                "http://localhost:8080/payment/success")

                        .setCancelUrl(
                                "http://localhost:8080/payment/cancel");

        params.putMetadata(
                "pendingBookingId",
                pendingBooking.getId().toString());

        System.out.println("METADATA AGGIUNTA:");
        System.out.println("pendingBookingId = " + pendingBooking.getId());

        if ("KLARNA".equalsIgnoreCase(
                request.getPaymentMethod())) {

            params.addPaymentMethodType(
                    SessionCreateParams.PaymentMethodType.KLARNA);

            System.out.println("METODO PAGAMENTO = KLARNA");

        } else {

            params.addPaymentMethodType(
                    SessionCreateParams.PaymentMethodType.CARD);

            System.out.println("METODO PAGAMENTO = CARD");
        }

        for (BookingServiceRequestDto serviceRequest
                : request.getServices()) {

            TeamService service =
                    serviceRepository.findById(
                            serviceRequest.getServiceId())
                            .orElseThrow();

            BigDecimal price =
                    bookingPricingService
                            .calculateSingleServicePrice(
                                    event,
                                    serviceRequest);

            System.out.println("--------------------------------");
            System.out.println("SERVIZIO = " + service.getName());
            System.out.println("PREZZO = " + price);

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
                                                    price.multiply(
                                                            BigDecimal.valueOf(100))
                                                            .longValue())

                                            .setProductData(

                                                    SessionCreateParams
                                                            .LineItem
                                                            .PriceData
                                                            .ProductData
                                                            .builder()

                                                            .setName(
                                                                    service.getName())

                                                            .build())

                                            .build())

                            .build());
        }

        System.out.println("\nCREAZIONE CHECKOUT SESSION...");

        Session session =
                Session.create(params.build());

        System.out.println("CHECKOUT SESSION CREATA");

        System.out.println("SESSION ID = " + session.getId());

        System.out.println("SESSION URL = " + session.getUrl());

        System.out.println("SESSION METADATA = " + session.getMetadata());

        pendingBooking.setStripeSessionId(
                session.getId());

        pendingBookingService.save(
                pendingBooking);

        System.out.println("SESSION ID SALVATA NELLA PendingBooking");

        System.out.println("==========================================");
        System.out.println("FINE StripeService");
        System.out.println("==========================================\n");

        return session.getUrl();
    }
}