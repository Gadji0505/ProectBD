package com.codewiki.codewiki.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>WebConfig class for Spring MVC configuration.
 * This class implements WebMvcConfigurer to customize the default Spring MVC configuration.</p>
 *
 * <p>Key configurations include:</p>
 * <ul>
 * <li><b>ViewControllerRegistry:</b> Registers simple automated controllers
 * for URL paths that map directly to view names without custom controller logic.</li>
 * <li><b>ResourceHandlerRegistry:</b> Configures how static resources (CSS, JS, images, etc.) are served.</li>
 * </ul>
 */
@Configuration // Indicates that this class provides Spring Bean definitions
@EnableWebMvc  // Enables Spring MVC features and sets up the default configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures view controllers to map URL paths directly to view names.
     * This is useful for simple pages that don't require specific controller logic,
     * such as login or registration forms.
     *
     * @param registry the ViewControllerRegistry to register view controllers.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Map the /login URL path to the auth/login.html Thymeleaf template
        // This means when a GET request comes to /login, Spring will directly render auth/login.html
        registry.addViewController("/login").setViewName("auth/login");

        // Map the /register URL path to the auth/register.html Thymeleaf template
        registry.addViewController("/register").setViewName("auth/register");

        // You might want to remove these mappings if your AuthController
        // handles GET requests for /login and /register with specific model attributes.
        // If AuthController handles it, these will be redundant.
        // For example, if AuthController.showLoginForm() already maps to /login
        // and returns "auth/login", this entry here isn't strictly needed for that URL.
        // However, they can act as a fallback or a clearer declaration for simple views.
    }

    /**
     * Configures resource handlers to serve static resources from specific locations.
     * This ensures that CSS, JavaScript, images, etc., are accessible via the web server.
     *
     * @param registry the ResourceHandlerRegistry to register resource handlers.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Maps requests for /css/** to resources in the classpath:/static/css/ directory.
        // Example: a request to /css/style.css will look for classpath:/static/css/style.css
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        // If you had JavaScript files in static/js/, you would add:
        // registry.addResourceHandler("/js/**")
        //         .addResourceLocations("classpath:/static/js/");

        // If you had image files in static/images/, you would add:
        // registry.addResourceHandler("/images/**")
        //         .addResourceLocations("classpath:/static/images/");

        // You can also add a general handler for all static content if it's
        // directly under static/
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    // You can add other configurations here, such as:
    // - addInterceptors(InterceptorRegistry registry): to register custom interceptors
    // - addFormatters(FormatterRegistry registry): to add custom formatters for data binding
    // - configureContentNegotiation(ContentNegotiationConfigurer configurer): to configure content negotiation
    // - addCorsMappings(CorsRegistry registry): to configure CORS policies
}