package com.example.springbatch.job.itemWriter.db;

import com.example.springbatch.job.itemReader.db.cursor.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JdbcItemWriter {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private int chunkSize = 10;
    private final DataSource dataSource;

    @Bean
    public Job jdbcItemWriterJob() throws Exception {
        return jobBuilderFactory.get("jdbc-writer")
                .incrementer(new RunIdIncrementer())
                .start(jdbcItemWriterStep())
                .build();
    }

    @Bean
    public Step jdbcItemWriterStep() throws Exception {
        return stepBuilderFactory.get("jdbc-writer-step")
                .<Customer, Customer>chunk(chunkSize)
                .reader(jdbcWriterItemReader())
                .writer(jdbcInsertItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> jdbcInsertItemWriter() {
        return new JdbcBatchItemWriterBuilder<>()
                .dataSource(dataSource)
                .sql("insert into customer2 values (:id, :firstName, :lastName, :birthdate)")
                .beanMapped()
                .build();
    }

    @Bean
    public ItemReader<Customer> jdbcWriterItemReader() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "A%");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("jdbcWriterItemReader")
                .pageSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .selectClause("id,firstname,lastname,birthdate")
                .fromClause("from customer")
                .whereClause("where firstname like :firstname")
                .sortKeys(sortKeys)
                .parameterValues(parameters)
                .build();
    }
}
