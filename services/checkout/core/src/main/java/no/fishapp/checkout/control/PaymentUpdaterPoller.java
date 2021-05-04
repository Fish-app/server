package no.fishapp.checkout.control;

import no.fishapp.checkout.model.notGreatSolutions.SimpleDbUpdatePollTicket;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;


public class PaymentUpdaterPoller {

    @PersistenceContext
    EntityManager entityManager;


    public static String GET_ALL_POLL_TICKETS = "select tk from SimpleDbUpdatePollTicket tk";

    @Asynchronous
    public Future<SimpleDbUpdatePollTicket> pollForUpdater() throws InterruptedException {
        Random random     = new Random();
        long   pollNumber = random.nextLong();

        SimpleDbUpdatePollTicket ticket = new SimpleDbUpdatePollTicket();
        ticket.setTicketNumber(pollNumber);
        ticket.setUpdaterStatus(SimpleDbUpdatePollTicket.UpdaterStatus.Polling);
        entityManager.persist(ticket);

        // wait for everyone to have submitted their ticket
        Thread.sleep(1000);


        List<SimpleDbUpdatePollTicket> ticketList = entityManager
                .createQuery(GET_ALL_POLL_TICKETS, SimpleDbUpdatePollTicket.class).getResultList();

        ticketList.sort(Comparator.comparingLong(SimpleDbUpdatePollTicket::getTicketNumber));

        if (ticketList.size() == 1 || ticketList.get(0).getTicketNumber() == ticket.getTicketNumber()) {
            ticket.setUpdaterStatus(SimpleDbUpdatePollTicket.UpdaterStatus.Chosen);
            entityManager.persist(ticket);
            return new AsyncResult<>(ticket);
        } else if (ticketList.get(1).getTicketNumber() == ticket.getTicketNumber()) {
            ticket.setUpdaterStatus(SimpleDbUpdatePollTicket.UpdaterStatus.Validator);
            entityManager.persist(ticket);
            return new AsyncResult<>(ticket);
        } else {
            ticket.setUpdaterStatus(SimpleDbUpdatePollTicket.UpdaterStatus.ignore);
            entityManager.remove(ticket);
            return new AsyncResult<>(ticket);
        }
    }

    public void IsDone(SimpleDbUpdatePollTicket completedTicket) {
        entityManager.remove(completedTicket);

    }

}
