package epam.enums;

import lombok.Getter;

@Getter
public enum OpenEndpoints {
    AUTHENTICATION("/api/v1/auth/**"),
    SWAGGER_UI("/swagger-ui/**"),
    SWAGGER_DOCS("/v3/api-docs/**"),
    ACTUATOR("/actuator/**");

    private final String url;

    OpenEndpoints(String name) {
        this.url = name;
    }

}
