package no.fishapp.checkout.model.dibsapi;


import lombok.Data;

@Data
public class WebHook {
    private String eventName;
    private String url;
    private String authorization;
}
