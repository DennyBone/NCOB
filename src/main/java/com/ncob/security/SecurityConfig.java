package com.ncob.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
    Class that configures spring security
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{

    @Autowired
    UserDetailsServiceImpl userDetailsService; // Needed for Spring Security authentication

    @Autowired
    private PasswordEncoder passwordEncoder; // Bcrypt object to encode passwords; autogenerates SALT code

    // configure spring security to allow static resources
    @Override
    public void configure(WebSecurity web) throws Exception
    {
        web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/content/**", "/webjars/**");
        // "content" is not autoconfigured by spring boot; could rename to 'images' and it would be autoconfigured like 'css' and 'js' are
        //web.ignoring().antMatchers("/content/**");
    }

    // add mongodb config here
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        // set the auth provider to use BCrypt pw encryption
        auth.authenticationProvider(authProvider());

        // use the custom userdetailsservice that checks the mongo db
        auth.userDetailsService(userDetailsService);

        //auth.inMemoryAuthentication()
        //        .withUser("user").password("pass").roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
            .authorizeRequests()
                .antMatchers("/login*", "/register*", "/home*").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/loginProcessor") // changed so end users won't know the app is secured with spring security
                .defaultSuccessUrl("/user.html", false)
                .failureUrl("/login.html?error=true")
                .and()
            .logout()
                .logoutSuccessUrl("/login.html"); //login.html?logout=true
    }

    // Method to return an auth provider object that uses the BCrypt encoder bean defined in the Application class
    private DaoAuthenticationProvider authProvider()
    {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}
