package kr.co.imin.batch.job.itemReader;

import kr.co.imin.batch.entity.PayTest;
import kr.co.imin.batch.repository.PayRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBatchTest
@SpringBootTest(classes = {PayPagingFailJobConfiguration.class, TestBatchConfig.class})
//@ContextConfiguration(classes=PayPagingFailJobConfiguration.class)
@TestPropertySource(properties = {"job.name=" + PayPagingFailJobConfiguration.JOB_NAME})
class PayPagingFailJobConfigurationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private PayRepository payRepository;

    @Test
    @DisplayName("같은조건을읽고 업데이트할때")
    public void equalConditionUpdateTest() throws Exception {

        for (long i = 0; i < 50; i++) {
            payRepository.save(new PayTest(i, false));
        }
        JobParameters jobParameters = this.jobLauncherTestUtils.getUniqueJobParameters();

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(payRepository.findAllSuccess().size()).isEqualTo(50);

    }

}

