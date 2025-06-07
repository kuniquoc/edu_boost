package quochung.server.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import quochung.server.service.UserDetailsServiceImplement;
import quochung.server.util.JwtAuthenticationFilter;
import quochung.server.util.JwtUtils;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        String[] publicUrl = { "/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/api/subject-types" };

        private final UserDetailsServiceImplement userDetailsService;

        private final JwtUtils jwtUtils;

        private final PasswordEncoder passwordEncoder;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.authorizeHttpRequests(authz -> authz
                                .requestMatchers(publicUrl).permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/study-methods/public/**").permitAll()
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/mod/**").hasRole("MOD")
                                .anyRequest().authenticated());
                http.sessionManagement(sessionManagement -> sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                http.csrf(csrf -> csrf.disable());
                http.addFilterBefore(new JwtAuthenticationFilter(jwtUtils, userDetailsService),
                                UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        @Bean
        public CorsFilter corsFilter() {
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.addAllowedOrigin("*");
                corsConfiguration.addAllowedMethod("*");
                corsConfiguration.addAllowedHeader("*");

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", corsConfiguration);

                return new CorsFilter(source);
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
                AuthenticationManagerBuilder authManagerBuilder = httpSecurity
                                .getSharedObject(AuthenticationManagerBuilder.class);
                authManagerBuilder.userDetailsService(userDetailsService)
                                .passwordEncoder(passwordEncoder);
                return authManagerBuilder.build();
        }
}

@Configuration
class
PasswordEncoderConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
