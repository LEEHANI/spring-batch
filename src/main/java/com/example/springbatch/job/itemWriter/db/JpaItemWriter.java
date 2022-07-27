package com.example.springbatch.job.itemWriter.db;

import com.example.springbatch.job.itemReader.db.cursor.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
@RequiredArgsConstructor
public class JpaItemWriter {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private int chunkSize = 10;
    private final EntityManagerFactory entityManagerFactory;
    private final ItemReader jpaCursorItemReader;

    @Bean
    public Job jdbcPagingJob2() throws Exception {
        return jobBuilderFactory.get("jpa-writer")
                .incrementer(new RunIdIncrementer())
                .start(jdbcPagingStep2())
                .build();
    }

    @Bean
    public Step jdbcPagingStep2() throws Exception {
        return stepBuilderFactory.get("jdbc-writer-step")
                .<Customer, Customer2>chunk(chunkSize)
                .reader(jpaCursorItemReader)
                .processor(itemWriterProcessor())
                .writer(jpaInsertItemWriter())
                .build();
    }

    @Bean
    public ItemProcessor itemWriterProcessor() {
        return new JpaItemWriterProcessor();
    }

    @Bean
    public ItemWriter<? super Customer2> jpaInsertItemWriter() {
        return new JpaItemWriterBuilder<>()
                .usePersist(true)
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

}
