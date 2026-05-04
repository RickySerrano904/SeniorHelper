package seniorhelper.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/public/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward browser refreshes on Angular routes to index.html.
        registry.addViewController("/{path:^(?!api$|v3$|swagger-ui$|actuator$|h2-console$|error$)[^.]*$}")
                .setViewName("forward:/index.html");
        registry.addViewController("/{path:^(?!api$|v3$|swagger-ui$|actuator$|h2-console$|error$)[^.]*$}/**")
                .setViewName("forward:/index.html");
    }
}
