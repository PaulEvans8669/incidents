package fr.paulevans.incidents;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IncidentsApplicationTests {

    @Value("${spring.profiles.active:default}")
    String activeProfile;

    @Test
    void testProfile() {
        System.out.println("Active profile is: " + activeProfile);
        Assertions.assertEquals("test", activeProfile);
    }
    @Test
    void contextLoads() {
    }

}
