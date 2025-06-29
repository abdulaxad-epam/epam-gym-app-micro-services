package epam;

import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.springframework.test.context.ActiveProfiles;

@Suite
@SelectPackages("epam")
@ActiveProfiles("test")
@CucumberContextConfiguration
@SuiteDisplayName("All Application Tests")
public class MainTest {
}
