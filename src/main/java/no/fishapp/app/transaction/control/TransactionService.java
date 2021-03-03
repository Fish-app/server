package no.fishapp.app.transaction.control;

import no.fishapp.app.transaction.entity.Transaction;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class TransactionService {

    @PersistenceContext
    EntityManager entityManager;

    public Transaction getTransaction(long id) {
        Transaction transaction = entityManager.find(Transaction.class, id);

        return transaction;

    }

}
