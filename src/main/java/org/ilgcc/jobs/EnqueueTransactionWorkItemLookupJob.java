package org.ilgcc.jobs;

import org.ilgcc.app.data.Transaction;
import org.springframework.stereotype.Service;

@Service
public class EnqueueTransactionWorkItemLookupJob {

    public void lookupWorkItemIDForTransaction(Transaction transaction) {
        //  Adding implementation logic
        System.out.println("Looking up Work Item ID for transaction: " + transaction.getTransactionId());
    }
}
