package org.hshekhar.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * @created 1/9/2023'T'3:12 PM
 * @author Himanshu Shekhar (609080540)
 **/

class CustomRoleConverter : Converter<Jwt?, MutableCollection<GrantedAuthority?>?> {

    override fun convert(jwt: Jwt): MutableCollection<GrantedAuthority?> {
        val listOrAuthorities: MutableCollection<GrantedAuthority?> = mutableListOf()
        val rolesAndPermissions = mutableSetOf<String>()
        jwt.claims["groups"]?.let {
            val groups = it as List<*>
            // TODO: check groups and add respective roles
            groups
                .map { group -> group.toString().uppercase() }
                .forEach { group ->
                    rolesAndPermissions.add("ROLE_$group")
                    // TODO: fetch all child roles
                    // TODO: fetch all permissions
                }
        }


        // adding default role
        rolesAndPermissions.forEach {
            listOrAuthorities.add(SimpleGrantedAuthority(it))
        }
        return listOrAuthorities
    }
}

@Configuration
class SecurityConfig {

    @Value("\${api.allowed-origins:*}")
    private val allowedOrigins: String? = null

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {
        //@formatter:off
        http.cors()
                .configurationSource(corsConfigurationSource())
            .and()
                .authorizeRequests()
                .antMatchers("/", "/swagger-ui.html", "/swagger-ui/**", "/v3/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/**").hasRole("USER")
                .anyRequest()
                    .authenticated()
            .and()
                .oauth2ResourceServer()
                    .jwt{
                        it.jwtAuthenticationConverter(jwtAuthenticationConverter())
                    }
        //@formatter:on
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = allowedOrigins?.split(",")
        configuration.allowedMethods = listOf("GET", "POST")
        configuration.allowedHeaders = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    private fun jwtAuthenticationConverter(): Converter<Jwt?, out AbstractAuthenticationToken?>? {
        val jwtConverter = JwtAuthenticationConverter()
        jwtConverter.setJwtGrantedAuthoritiesConverter(CustomRoleConverter())
        return jwtConverter
    }

}