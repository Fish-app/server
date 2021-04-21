package no.fishapp.checkout.model.dibsapi;

import lombok.Data;

@Data
public class Checkout {

    /**
     * The integration type of the checkout i.e. how to display the
     * payment page.
     * Ether hostedPaymentPage or the default EmbeddedCheckout,
     * we use hostedPaymentPage
     */
    private String integrationType;

    /**
     * The url to return to after the payment is complete
     */
    private String returnUrl;

    /**
     * The url for the terms of service
     */
    private String termsUrl;

    /**
     * Whether or not to charge the user at once
     */
    private boolean charge;
}


/*
   "checkout":{
		  "integrationType":"hostedPaymentPage",
      "returnUrl":"https://dev.epayment.nets.eu",
      "termsUrl":"http://foo.bar",
      "shippingCountries":null,
      "shipping":{
         "countries":[

         ],
         "merchantHandlesShippingCost":false
      },
      "consumerType":null,
      "charge":false,
      "appearance":{
         "textOptions":{
            "completePaymentButtonText":"Subscribe"
         }
      }
   },
 */