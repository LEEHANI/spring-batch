package com.example.springbatch.job.itemWriter.db;

import com.example.springbatch.job.itemReader.db.cursor.Customer;
import org.springframework.batch.item.ItemProcessor;

public class JpaItemWriterProcessor implements ItemProcessor<Customer, Customer2> {

    @Override
    public Customer2 process(Customer customer) throws Exception {
        return new Customer2(customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getBirthdate());
    }
}
