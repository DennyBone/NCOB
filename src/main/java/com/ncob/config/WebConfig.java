package com.ncob.config;

import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
//@EnableWebMvc // This disables all the auto configuration that spring boot does for us
public class WebConfig extends WebMvcConfigurerAdapter
{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/img/**", "/css/**", "/js/**", "/webjars/**", "/sb-dashboard/**", "/scss/**")
                .addResourceLocations("classpath:/static/img/", "classpath:/static/css/",
                        "classpath:/static/js/", "classpath:/META-INF/resources/webjars/",
                        "classpath:/static/sb-dashboard/", "classpath:/static/scss/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry)
    {
        registry.addRedirectViewController("/", "home.html");
        //registry.addViewController("/").setViewName("forward:/home.html");
        //registry.addViewController("/").setViewName("redirect:/home.html");
    }
}
