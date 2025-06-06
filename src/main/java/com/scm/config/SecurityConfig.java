package com.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.scm.services.impl.SecurityCustomUserDetailService;

@Configuration
public class SecurityConfig {

    // user create and login using java code with in memory service
    // @Bean
    // public UserDetailsService userDetailsService()
    // {
    //     UserDetails user1 = User.withUsername("admin123").password("admin123").roles("ADMIN","USER").build();
    //     UserDetails user2 = User.withDefaultPasswordEncoder().username("user123").password("user123").build();     
    //     return new InMemoryUserDetailsManager(user1,user2);
    // }

    @Autowired
    private AuthFailureHandler authFailureHandler;

    @Autowired
    private SecurityCustomUserDetailService userDetailService;

    @Autowired
    private OAuthenticationSuccessHandler handler;


    // Configuration of Authentication provider for spring security
    @Bean
    public DaoAuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailService);

        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception
    {
        // Configuration
        httpSecurity.authorizeHttpRequests(authorize->{
            authorize.requestMatchers("/user/**").authenticated();
            authorize.anyRequest().permitAll();
        });

        // form default login
        httpSecurity.formLogin(formLogin->
        {
            formLogin.loginPage("/login");
            formLogin.loginProcessingUrl("/authenticate");
            formLogin.successForwardUrl("/user/profile");
            // formLogin.failureForwardUrl("/login?error=true");
            formLogin.usernameParameter("email");
            formLogin.passwordParameter("password");

            formLogin.failureHandler(authFailureHandler);
            
        });

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity.oauth2Login(oauth->{
            oauth.loginPage("/login");
            oauth.successHandler(handler);
        });

        httpSecurity.logout(logoutForm->{
            logoutForm.logoutUrl("/logout");
            logoutForm.logoutSuccessUrl("/login?logout=true");
        });
        
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

}
