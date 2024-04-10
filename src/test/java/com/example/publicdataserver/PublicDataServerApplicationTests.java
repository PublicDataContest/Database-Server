package com.example.publicdataserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EnableScheduling
class PublicDataServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
