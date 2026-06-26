package com.tondo;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestRedisMockConfig.class)
class TondoApplicationTests {

    @Test
    void contextLoads() {
    }

}
