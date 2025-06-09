package epam;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectPackages("epam")
@SuiteDisplayName("All Application Tests")
public class MainTest {
}
