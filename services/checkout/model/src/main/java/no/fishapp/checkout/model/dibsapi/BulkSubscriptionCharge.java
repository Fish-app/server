package no.fishapp.checkout.model.dibsapi;

import lombok.Data;

import java.util.List;


@Data
public class BulkSubscriptionCharge {

    /**
     * A uniqe string provided to identify the bulk charge
     */
    private String externalBulkChargeId;


    /**
     * List of the subscriptions to renew
     */
    private List<BulkSubscription> subscriptions;
}


/*



{
  "externalBulkChargeId":"Mybulkcharge1",
  "notifications":null,
  // The subscriptions you would like to charge

"subscriptions":[
    // Example of subscription with a single product

// Multiple subscription payments can be used within the same file    {

      "subscriptionId":" b9b691c8cc8a4e429e6e5c86e58f34fc ",
      // Select only one of these parameters!
      "externalReference" : "External psp reference",

      "order":{
        "items":[
          {
            "reference":"Product1",
            "name":"Bulk Test Subscription 1",
            "quantity":1.0,
            "unit":"unit",
            "unitPrice":800,
            "taxRate":2500,
            "taxAmount":200,
            "grossTotalAmount":1000,
            "netTotalAmount":800
          }
        ],
        "amount":1000,
        "currency":"NOK",
        "reference":"My Bulk Charge 1"
      }
    },
    // Example of subscription with multiple products    {

      "subscriptionId":"523198375a4b4804901f7a003d2c40bf",
       // Select only one of these parameters!
      "externalReference" : "External psp reference",

      "order":{
        "items":[
          {
            "reference":"Product6a",
            "name":"Bulk Test Subscription 6a",
            "quantity":1.0,
            "unit":"unit",
            "unitPrice":800,
            "taxRate":2500,
            "taxAmount":200,
            "grossTotalAmount":1000,
            "netTotalAmount":800
          },
          {
            "reference":"Productb6",
            "name":"Bulk Test Subscription 6b",
            "quantity":1.0,
            "unit":"unit",
            "unitPrice":800,
            "taxRate":2500,
            "taxAmount":200,
            "grossTotalAmount":1000,
            "netTotalAmount":800
          }
        ],
        "amount":2000,
        "currency":"NOK",
        "reference":"Mike Bulk Charge 6"
      }
    }
  ]
}


 */