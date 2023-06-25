package International_Trade_Union.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

public class MultipartConfigElementConfig {
    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse("12MB"));
        factory.setMaxRequestSize(DataSize.parse("12MB"));
        return factory.createMultipartConfig();
    }


}
