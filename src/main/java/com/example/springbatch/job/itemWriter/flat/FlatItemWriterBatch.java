package com.example.springbatch.job.itemWriter.flat;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class FlatItemWriterBatch {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private int chunkSize = 10;


    @Bean
    public Job flatItemWriterJob() {
        return jobBuilderFactory.get("flat-writer")
                .incrementer(new RunIdIncrementer())
                .start(flatWriterStep())
                .build();
    }

    @Bean
    public Step flatWriterStep() {
        return stepBuilderFactory.get("flat")
                .<Customer, Customer>chunk(chunkSize)
                .reader(flatItemWriterReader())
                .writer(flatFormatterLineItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> flatDelimitedItemWriter() {
        return new FlatFileItemWriterBuilder<>()
                .name("flatDelimitedItemWriter")
                .resource(new FileSystemResource("src/main/resources/customer.txt"))
                .delimited()
                .delimiter("|")
                .names(new String[]{"id", "name", "age"})
                .append(true)
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> flatFormatterLineItemWriter() {
        return new FlatFileItemWriterBuilder<>()
                .name("flatFormatterLineItemWriter")
                .resource(new FileSystemResource("src/main/resources/customer.txt"))
                .formatted()
                .format("%-2d%-6s%-2d")
                .names(new String[]{"id", "name", "age"})
                .append(true)
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> flatItemWriterReader() {
        List<Customer> customers = Arrays.asList(new Customer(1, "user1", 41),
                new Customer(2, "user2", 42),
                new Customer(3, "user3", 43));

        ListItemReader<Customer> reader = new ListItemReader<>(customers);
        return reader;
    }
}
