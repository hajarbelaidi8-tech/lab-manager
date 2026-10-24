package ma.fsm.lab_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // On désactive CSRF pour que le formulaire de candidature marche sans login
                .authorizeHttpRequests(auth -> auth
                        // 1. ON OUVRE LES PORTES PUBLIQUES (Section 2.1 PDF) - MODIFIÉ ⭐
                        .requestMatchers("/", "/login", "/postuler", "/postuler/**",
                                "/membres", "/membres/details/**", // Ajout de /membres ⭐
                                "/publications", "/publications/**", "/publication/**",
                                "/evenements", "/evenements/details/**", // Ajout de /evenements ⭐
                                "/equipes/**", "/archives",
                                "/css/**", "/js/**", "/images/**", "/uploads/**",
                                "/api/files/view/**", "/api/documents/download/**",
                                "/error", "/favicon.ico"
                        ).permitAll()
                        // 2. ON RÉSERVE CERTAINES PAGES AU DIRECTEUR
                        // Match-i hadوك l-stora f line 42-44
                        .requestMatchers("/directeur/**", "/gouvernance/**").hasAnyRole("DIRECTEUR", "ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 3. TOUT LE RESTE DEMANDE UNE CONNEXION
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true) // Redirige vers le dashboard après connexion
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // Retour à l'accueil public après déconnexion
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
