package no.fishapp.checkout.model.dibsapi;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebHook {

    private String eventName;
    private String url;
    private String authorization;
    private HashMap<String, String> headers;


}
