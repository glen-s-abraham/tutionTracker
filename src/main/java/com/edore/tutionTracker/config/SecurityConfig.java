package com.edore.tutionTracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
            ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http.cors(cors -> cors.configurationSource(request -> {
            var corsConfig = new org.springframework.web.cors.CorsConfiguration();
            corsConfig.addAllowedOriginPattern("*"); // Allow all origins
            corsConfig.addAllowedMethod("*"); // Allow all HTTP methods
            corsConfig.addAllowedHeader("*"); // Allow all headers
            corsConfig.setAllowCredentials(true); // Allow credentials (e.g., cookies)
            return corsConfig;
        })).authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                .requestMatchers("/tutor/**").hasAnyRole("TUTOR")
                .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .logout((logout) -> {
                    var logoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(
                            clientRegistrationRepository);
                    logoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");
                    logout.logoutSuccessHandler(logoutSuccessHandler);
                });

        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return jwtAuthenticationConverter;
    }

}
