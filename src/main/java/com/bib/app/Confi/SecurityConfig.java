package com.bib.app.Confi;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.bib.app.service.MyUserDetailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired 
    private MyUserDetailService userDetailService;
    
    @Bean 
    public UserDetailsService userDetailsService() { 
        return userDetailService; 
    } 
    
    @Bean 
    public AuthenticationProvider authenticationProvider() { 
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); 
        provider.setUserDetailsService(userDetailService); 
        provider.setPasswordEncoder(passwordEncoder()); 
        return provider; 
    } 
    
    @Bean 
    public PasswordEncoder passwordEncoder() { 
        return new BCryptPasswordEncoder(); 
    } 

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/User/**","/Session/**","/events/**", "/cohort/**", "/project/**", "/company/**").permitAll()
                .requestMatchers(
                	    "/swagger-ui/**",
                	    "/v3/api-docs/**",
                	    "/swagger-ui.html",
                	    "/swagger-resources/**",
                	    "/webjars/**"
                	).permitAll() // http://localhost:8080/swagger-ui/index.html
                .requestMatchers(
                    "/api/admin/**"
                ).hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
            	    .loginProcessingUrl("/login")
            	    .successHandler((request, response, authentication) -> {
            	        response.setStatus(HttpStatus.OK.value());
            	        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            	        
            	        // Clear response buffer first
            	        response.resetBuffer();
            	        
            	        // Write single JSON response
            	        response.getWriter().print("{\"status\":\"Login successful\"}");
            	        response.flushBuffer();
            	    })
            	    .failureHandler((request, response, exception) -> {
            	        response.setStatus(HttpStatus.UNAUTHORIZED.value());
            	        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            	        response.getWriter().write("{\"error\":\"Invalid credentials\"}");
            	    }))
            .logout(logout -> logout
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true))
            .exceptionHandling(handling -> handling
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Unauthorized\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Access denied\"}");
                }));

        return http.build();
    }

    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"message\":\"Authentication successful\", \"redirectUrl\":\"/\"}");
        };
    }

    private AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Authentication failed\", \"message\":\"" + exception.getMessage() + "\"}");
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Add the hippo-api-version header to the allowed headers
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "hippo-api-version"));
        configuration.setAllowCredentials(true);
        // Allow preflight requests to be cached for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}