package no.fishapp.store.transaction.control;


public class TransactionService {
//
//    @PersistenceContext
//    EntityManager entityManager;
//
//    @Inject
//    ListingService listingService;
//
//    @Inject
//    BuyerService buyerService;
//
//    @Inject
//    SellerService sellerService;
//
//    @Inject
//    UserService userService;
//
//    private static final String RATING_EXISTS_QUERY = "select ts from Transaction ts where ts.seller.id = :uid or ts.buyer.id = :uid";
//
//
//    public List<Transaction> getUserTransactions() {
//        var query = entityManager.createQuery(RATING_EXISTS_QUERY, Transaction.class);
//        query.setParameter("uid", userService.getLoggedInUser().getId());
//
//        try {
//            var transactions = query.getResultList();
//
//            return transactions;
//        } catch (NoResultException ignore) {
//        }
//        return null;
//    }
//
//
//    public Transaction getTransaction(long id) {
//        Transaction transaction = entityManager.find(Transaction.class, id);
//
//        return transaction;
//    }
//
//    public Transaction newTransaction(StartTransactionData transactionData) {
//        Listing listing = listingService.findListingById(transactionData.getListingId());
//
//        if (listing != null) {
//            Transaction transaction = new Transaction();
//            transaction.setAmount(transactionData.getAmount());
//            transaction.setPrice(listing.getPrice());
//            transaction.setSeller(sellerService.getSeller(listing.getCreator().getId()));
//            transaction.setBuyer(buyerService.getLoggedInBuyer());
//            transaction.setListing(listing);
//            entityManager.persist(transaction);
//            return transaction;
//
//        }
//        return null;
//    }

}
