package org.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Order(1)
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // NoOpPasswordEncoder для незашифрованных паролей
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                //Доступ только для пользователей с ролью Администратор
                .antMatchers("/user/all").hasRole("ADMIN")
                .antMatchers("/user/search").hasRole("ADMIN")
                .antMatchers("/user/operator").hasRole("ADMIN")

                .antMatchers("/application/update").hasAnyRole("OPERATOR", "USER")
                .antMatchers("/application/all").hasAnyRole("OPERATOR", "USER")
                .antMatchers("/application").hasRole("USER")
                .and()
                //Настройка для входа в систему
                .formLogin();
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("lea")
                .password("pass")
                .roles("ADMIN")
                .and()
                .withUser("dasha")
                .password("pass")
                .roles("OPERATOR")
                .and()
                .withUser("valy")
                .password("pass")
                .roles("USER")
        ;
    }
}
