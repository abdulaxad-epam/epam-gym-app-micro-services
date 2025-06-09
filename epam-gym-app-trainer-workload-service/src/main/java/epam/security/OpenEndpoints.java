package epam.security;

import lombok.Getter;

@Getter
public enum OpenEndpoints {
    ACTUATOR("/actuator/**");

    private final String url;

    OpenEndpoints(String name) {
        this.url = name;
    }

}
