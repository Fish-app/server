package no.fishapp.checkout.model.dibsapi;

import lombok.Data;

import java.util.List;

@Data
public class Notifications {

    private List<WebHook> webHooks;
}
