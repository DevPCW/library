package com.korit.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.path}")
    private String filePath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://127.0.0.1:5500");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) { // Ctrl + o
        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry.addResourceHandler("/image/**") // 1) 풀로 경로 다 적어야 하는 것을 간략화 함.(mapping 주소를 바꿈) // /image/ 요청이 들어오면
                .addResourceLocations("file:///" + filePath) // 2) 이 경로를 참조를 해라
                .resourceChain(true)
                // 'addResolver' 에서 'PathResourceResolver' 를 생성하고,
                // 'Chain' 에 연결을 시켜주면 원하는 경로에 'mapping' 을 걸어줄 수 있다.
                // 프로젝트 경로가 아닌 경로를 'Resolver' 해주겠다.
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        resourcePath = URLDecoder.decode(resourcePath, StandardCharsets.UTF_8);
                        return super.getResource(resourcePath, location); // 한글 인코딩이 깨질 때 이 방법을 쓰면 됨
                    }
                    // 익명 클래스 -> 원래 'URL' 의 자원 부분을
                    // 'PathResourceResolver' 를 상속받고 있는 'getResource' 를
                    // 오버라이드 해서 자원 부분을 한글이 들어와도 되도록
                    // 디코딩을 해서 다시 원래 자원 경로에 잡아줌.
                });
    }
}
