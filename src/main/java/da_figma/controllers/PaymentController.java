package da_figma.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.stripe.exception.StripeException;

import da_figma.dtos.BookingRequestDto;
import da_figma.services.StripeService;

@Controller
public class PaymentController {

    @Autowired
    private StripeService stripeService;


    // =========================================================
    // CREA CHECKOUT STRIPE
    // =========================================================

    @PostMapping("/payment/create-checkout-session")
    public String createCheckoutSession(
            @ModelAttribute BookingRequestDto request)
            throws StripeException {

        String checkoutUrl =
                stripeService.createCheckoutSession(request);

        return "redirect:" + checkoutUrl;
    }


    // =========================================================
    // PAGAMENTO COMPLETATO
    // =========================================================

    @GetMapping("/payment/success")
    public String success() {

        return "payments/paymentSuccess";
    }


    // =========================================================
    // PAGAMENTO ANNULLATO
    // =========================================================

    @GetMapping("/payment/cancel")
    public String cancel() {

        return "payments/paymentCancel";
    }
}
