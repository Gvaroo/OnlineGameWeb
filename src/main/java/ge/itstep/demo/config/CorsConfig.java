package ge.itstep.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
//
////    @Override
////    public void addCorsMappings(CorsRegistry registry) {
////        registry.addMapping("/**")
////                .allowedOrigins("http://localhost:4200") // Add your client's origin
////                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
////                .allowedHeaders("*")
////                .exposedHeaders("Authorization")
////                .allowCredentials(true)
////                .maxAge(3600); // Set the max age for pre-flight requests
////    }
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("http://localhost:4200")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true);
}
}


