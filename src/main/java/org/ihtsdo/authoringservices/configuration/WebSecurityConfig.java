package org.ihtsdo.authoringservices.configuration;

import org.ihtsdo.authoringservices.rest.security.RequestHeaderAuthenticationDecoratorWithOverride;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${ims-security.required-role}")
	private String requiredRole;

	@Value("${authentication.override.username}")
	private String overrideUsername;

	@Value("${authentication.override.roles}")
	private String overrideRoles;

	@Value("${authentication.override.token}")
	private String overrideToken;

	private final String[] excludedUrlPatterns = {
			"/",
			"/version",
			"/ui-configuration",
			"/authoring-services-websocket/**/*",
			// Swagger API Docs:
			"/swagger-ui/**",
			"/v3/api-docs/**"
	};

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.addFilterBefore(new RequestHeaderAuthenticationDecoratorWithOverride(overrideUsername, overrideRoles, overrideToken), FilterSecurityInterceptor.class);

		if (requiredRole != null && !requiredRole.isEmpty()) {
			http.authorizeRequests()
					.antMatchers(excludedUrlPatterns).permitAll()
					.anyRequest().hasAuthority(requiredRole)
					.and().httpBasic();
		} else {
			http.authorizeRequests()
					.antMatchers(excludedUrlPatterns).permitAll()
					.anyRequest().authenticated()
					.and().httpBasic();
		}
	}
}
