package it.finki.charitable.security;

import it.finki.charitable.entities.UserRole;
import it.finki.charitable.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserO2AuthService userO2AuthService;
    @Autowired
    private O2AuthSuccessHandler o2AuthSuccessHandler;

    private final UserService userService;
    private BCryptPasswordEncoder passwordEncoder() {
        return PasswordEncoder.bCryptPasswordEncoder();
    }

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    private final static String[] publicMatchers = {
            "/css/**",
            "/js/**",
            "/image/**",
            "/",
            "/login",
            "/register",
            "/validate",
            "/album/**",
            "/post",
            "/post-photos/**",
            "/oauth2/authorization/google"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(publicMatchers).permitAll()
                .antMatchers("/moderator-photos/**", "/moderator/**").hasAuthority(UserRole.MODERATOR.name())
                .anyRequest().hasAuthority(UserRole.USER.name());

        http
                .csrf().disable()
                .cors().disable()
                .formLogin().loginPage("/login")
                .successHandler(authenticationSuccessHandler)
                .and()
                .oauth2Login()
                .loginPage("/login")
                .userInfoEndpoint()
                .userService(userO2AuthService)
                .and()
                .successHandler(o2AuthSuccessHandler)
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/").deleteCookies("remember-me")
                .and()
                .rememberMe();
    }

    AuthenticationSuccessHandler authenticationSuccessHandler = (httpServletRequest, httpServletResponse, authentication) -> {
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        if(authentication.getAuthorities().toString().contains("MODERATOR")) {
            redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/moderator/approval?page=1&sort=id");
        } else {
            redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/");
        }
    };



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }
}
