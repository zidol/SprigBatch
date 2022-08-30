package kr.co.imin.batch.job.itemReader;

import kr.co.imin.batch.entity.PayTest;
import kr.co.imin.batch.repository.PayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManagerFactory;

import java.util.Collections;

import static kr.co.imin.batch.job.itemReader.PayPagingFailJobConfiguration.JOB_NAME;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(name = "job.name", havingValue = JOB_NAME)
//@EnableAutoConfiguration
//@EnableBatchProcessing
public class PayPagingFailJobConfiguration {

    public static final String JOB_NAME = "payPagingFailJob";

    private final EntityManagerFactory entityManagerFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final PayRepository payRepository;

    private final int chunkSize = 10;

    @Bean
    public Job payPagingJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(payPagingStep())
                .build();
    }

    @Bean
    @JobScope
    public Step payPagingStep() {
        return stepBuilderFactory.get("payPagingStep")
                .<PayTest, PayTest>chunk(chunkSize)
                .reader(payPagingReader())
                .processor(payPagingProcessor())
                .writer(writer())
                .build();
    }

    //실패 코드
//    @Bean
//    @StepScope
//    public JpaPagingItemReader<PayTest> payPagingReader() {
//        return new JpaPagingItemReaderBuilder<PayTest>()
//                .name("payPagingReader")
//                .entityManagerFactory(entityManagerFactory)
//                .pageSize(chunkSize)
//                .queryString("SELECT pt FROM PayTest pt WHERE pt.successStatus = false")
//                .build();
//    }

    //update 하지 않고 insert 한다.
    @Bean
    @StepScope
    public JpaPagingItemReader<PayTest> payPagingReader() {

        JpaPagingItemReader<PayTest> reader = new JpaPagingItemReader<PayTest>() {
            @Override
            public int getPage() {
                return 0;
            }
        };

        reader.setQueryString("SELECT p FROM PayTest p WHERE p.successStatus = false");
        reader.setPageSize(chunkSize);
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setName("payPagingReader");

        return reader;
    }

    /**
     * 근래 버전 방법
     * @return
     */
//    @Bean
//    public ItemReader<PayTest> repositoryItemReader() {
//        return new RepositoryItemReaderBuilder<PayTest>()
//                .repository(payRepository)
//                .methodName("findAll")
//                .pageSize(chunkSize)
//                .saveState(false)
//                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
//                .name("payPagingReader")
//                .build();
//    }

    @Bean
    @StepScope
    public ItemProcessor<PayTest, PayTest> payPagingProcessor() {
        return item -> {
            item.success();
            return item;
        };
    }

    @Bean
    @StepScope
    public JpaItemWriter<PayTest> writer() {
        JpaItemWriter<PayTest> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
