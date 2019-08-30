package com.jaclon.bootsecurity.config;

import com.jaclon.bootsecurity.page.table.PageTableArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.File;
import java.util.List;

/**
 * @author jaclon
 * @date 2019/8/26
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    /**
     * 添加跨域支持
     * @return
     */
    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedMethods("*");
            }
        } ;
    }

    /**
     * datatable分页解析
     * @return
     */
    @Bean
    public PageTableArgumentResolver tableHandlerMethodArgumentResolver(){
        return new PageTableArgumentResolver();
    }

    /**
     * 将自定义的PageTableArgumentResolver加入Resolver列表中
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(tableHandlerMethodArgumentResolver());
    }

    @Value("${files.path}")
    private String filesPath;

    /**
     * 静态资源文件的外部访问地址映射
     * 直接访问外部文件
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //磁盘文件url 映射
        //配置server虚拟路径，handler为前台访问的目录，locations为files相对应的本地路径
        registry.addResourceHandler("/statics/**")
                .addResourceLocations(ResourceUtils.FILE_URL_PREFIX + filesPath + File.separator);
    }
}
