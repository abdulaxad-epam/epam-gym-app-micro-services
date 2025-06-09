package epam.configuration;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class SwaggerConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(SwaggerConfiguration.class);

    @Test
    void shouldCreateOpenAPIDocumentationBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(OpenAPI.class);

            OpenAPI openAPI = context.getBean(OpenAPI.class);
            Info info = openAPI.getInfo();

            assertThat(info).isNotNull();
            assertThat(info.getTitle()).isEqualTo("EPAM API");
            assertThat(info.getDescription()).isEqualTo("EPAM GYM API (TRAINING)");
            assertThat(info.getVersion()).isEqualTo("1.0.0");
        });
    }
}
