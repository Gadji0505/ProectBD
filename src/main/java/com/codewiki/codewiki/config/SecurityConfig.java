package com.codewiki.codewiki.config;

import com.codewiki.codewiki.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Включаем Pre/Post аннотации для безопасности методов
public class SecurityConfig {

    private final AuthService authService; // Используем наш UserDetailsService
    // private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler; // Если используете свой обработчик успеха

    @Autowired
    public SecurityConfig(AuthService authService) {
        this.authService = authService;
        // this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler; // Если используете
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Разрешить доступ к статическим ресурсам
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                // Разрешить доступ к страницам регистрации и входа
                .requestMatchers("/", "/home", "/login", "/register").permitAll()
                // Разрешить доступ к просмотру статей и тегов без аутентификации
                .requestMatchers("/articles", "/articles/view/**", "/tags", "/tags/view/**", "/categories", "/categories/view/**").permitAll()
                // Требовать аутентификацию для создания статей
                .requestMatchers("/articles/new", "/articles/new/**").authenticated()
                // Требовать ROLE_USER или ROLE_ADMIN для комментирования (если хотите)
                .requestMatchers("/comments/add/**").hasAnyRole("USER", "ADMIN")
                // Требовать ROLE_ADMIN для доступа к админке и определенным действиям
                .requestMatchers("/admin/**", "/articles/edit/**", "/articles/delete/**", "/comments/delete/**", "/users/**", "/reports/**").hasRole("ADMIN")
                // Все остальные запросы требуют аутентификации
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // Указываем страницу входа
                .loginProcessingUrl("/login") // URL, на который отправляется форма входа
                .defaultSuccessUrl("/profile", true) // Перенаправление после успешного входа
                // .successHandler(customAuthenticationSuccessHandler) // Если есть свой обработчик
                .failureUrl("/login?error=true") // Перенаправление при неудачном входе
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL для выхода
                .logoutSuccessUrl("/login?logout") // Перенаправление после успешного выхода
                .invalidateHttpSession(true) // Инвалидировать HTTP сессию
                .deleteCookies("JSESSIONID") // Удалить куки
                .permitAll()
            )
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/access-denied") // Страница при отсутствии прав
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(authService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}