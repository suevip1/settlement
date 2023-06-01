package com.example.settlement.clear.infra.jobs;

import com.example.settlement.SettlementApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author yangwu_i
 * @date 2023/5/12 13:07
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = SettlementApplication.class)
class ClearingJobTest {

    @Resource
    private ClearingJob clearingJob;

    @Test
    void clearingJobHandler() {
        clearingJob.clearingJobHandler();
    }
}