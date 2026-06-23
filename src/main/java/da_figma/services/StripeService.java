package da_figma.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import da_figma.dtos.BookingRequestDto;
import da_figma.dtos.BookingServiceRequestDto;
import da_figma.models.Event;
import da_figma.models.TeamService;
import da_figma.repositories.EventRepository;
import da_figma.repositories.TeamServiceRepository;

@Service
public class StripeService {

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

    public String createCheckoutSession(BookingRequestDto request)
            throws StripeException {

        Event event = eventRepository.findById(request.getEventId()).orElseThrow();

        SessionCreateParams.Builder params = SessionCreateParams.builder()

                        .setMode(
                                SessionCreateParams.Mode.PAYMENT)

                        .setCustomerEmail(
                                userContextService.getCurrentEmail())

                        .setSuccessUrl(
                                "http://localhost:8080/payment/success")

                        .setCancelUrl(
                                "http://localhost:8080/payment/cancel");

        if ("KLARNA".equalsIgnoreCase(
                request.getPaymentMethod())) {

            params.addPaymentMethodType(
                    SessionCreateParams.PaymentMethodType.KLARNA);

        } else {

            params.addPaymentMethodType(
                    SessionCreateParams.PaymentMethodType.CARD);
        }

        for (BookingServiceRequestDto serviceRequest
                : request.getServices()) {

            TeamService service =
                    serviceRepository
                            .findById(
                                    serviceRequest.getServiceId())
                            .orElseThrow();

            BigDecimal price =
                    bookingPricingService
                            .calculateSingleServicePrice(
                                    event,
                                    serviceRequest);

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

        Session session = Session.create(params.build());

        return session.getUrl();
    }

}
