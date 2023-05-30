package com.gdu.app11.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class FileConfig {
	
	// 우리가 바꿀 것은 파일 사이즈밖에 없다. 무턱대고 크게 잡지 말고 서버의 크기에 맞춰서 사이즈 잡아라.
	@Bean
	public MultipartResolver multipartResolver() {    // Bean 타입은 MultipartResolver로 설정해야 한다. (인터페이스 타입을 Bean으로 저장해야 좋다)
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setDefaultEncoding("UTF-8");
		multipartResolver.setMaxUploadSize(1024 * 1024 * 100);        // 전체 첨부 파일의 크기 100MB <- 우리는 다중첨부니까
		multipartResolver.setMaxUploadSizePerFile(1024 * 1024 * 10);  // 첨부 파일 하나의 크기 10MB
		// 서버 메모리가 너무 조금임(안 좋음, 값싼 서버), 이럴 때 서버 메모리를 정해주면 된다. 이런 경우 아니면 굳이 메모리 정해줄 필요 없음.
		return multipartResolver;
	}
	
}
