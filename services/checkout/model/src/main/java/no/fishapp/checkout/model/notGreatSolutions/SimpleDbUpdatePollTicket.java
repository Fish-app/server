package no.fishapp.checkout.model.notGreatSolutions;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class SimpleDbUpdatePollTicket {

    public static enum UpdaterStatus {
        Polling,
        Chosen,
        Validator,
        Done,
        ignore,
    }

    /**
     * planed on using the ticket number as id but the poll wold raise an exception
     * once every 9,223,372,036,854,775,807 time so the id is separate.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    private long ticketNumber;

    @Enumerated(EnumType.ORDINAL)
    private UpdaterStatus updaterStatus;

}
