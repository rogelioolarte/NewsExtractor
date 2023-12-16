package com.NewsExtractor.config;

import com.NewsExtractor.entity.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class ApplicationConfig {

    @Value("${source-url}")
    private String dataUrl;
    @Value("${source-user}")
    private String dataUser;
    @Value("${source-pass}")
    private String dataPass;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource driverManager = new DriverManagerDataSource();
        driverManager.setDriverClassName("com.mysql.cj.jdbc.Driver");
        driverManager.setUrl(dataUrl);
        driverManager.setUsername(dataUser);
        driverManager.setPassword(dataPass);
        return driverManager;
    }

    @Value("${application-description}")
    private String appDescription;
    @Value("${application-version}")
    private String appVersion;
    private final Info customInfo = new Info().title("News Extractor")
            .version(appVersion)
            .description(appDescription)
            .license(new License().name("GNU General Public License v3.0")
                    .url("https://www.gnu.org/licenses/gpl-3.0-standalone.html"));

    @Bean
    public GroupedOpenApi configOpenAPI(){
        SpringDocUtils.getConfig().addResponseWrapperToIgnore(NewsPaper.class);
        SpringDocUtils.getConfig().addResponseWrapperToIgnore(Pattern.class);
        SpringDocUtils.getConfig().addResponseWrapperToIgnore(Proxyy.class);
        SpringDocUtils.getConfig().addResponseWrapperToIgnore(Section.class);
        SpringDocUtils.getConfig().addResponseWrapperToIgnore(Article.class);
        return GroupedOpenApi.builder().group("main")
                .pathsToMatch("/api/**")
                .displayName("News Extractor Components")
                .addOpenApiCustomizer( openApi -> openApi.info(customInfo))
                .build();
    }

    /**
    @Bean
    @Primary
    public SwaggerUiConfigProperties swaggerUiConfig(SwaggerUiConfigProperties config) {
        config.setSupportedSubmitMethods(List.of(""));
        return config;
    } **/
}
