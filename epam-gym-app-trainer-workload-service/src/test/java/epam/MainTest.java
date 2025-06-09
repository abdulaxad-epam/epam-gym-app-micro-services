package epam;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@Suite
@SpringBootTest
@SelectPackages("epam")
public class MainTest {

    @Test
    void contextLoads() {
    }

}
