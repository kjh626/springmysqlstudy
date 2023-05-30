package com.gdu.app13.util;

import java.io.File;
import java.time.LocalDate;
import java.util.UUID;
import java.util.regex.Matcher;

import org.springframework.stereotype.Component;

@Component
public class MyFileUtil {
	
	// 경로 구분자
	private String sep = Matcher.quoteReplacement(File.separator);
	
	// String path 만들기 (기준은 날짜)
	public String getPath() {
		LocalDate now = LocalDate.now();
		// 루트 폴더 하나 만들어줌(/storage)
		// 실제로 만들어지는 경로: 루트/storage/2023/05/08   <- 서버를 쓰면 루트(리눅스 쓸듯)
		return "/storage" + sep + now.getYear() + sep + String.format("%02d", now.getMonthValue()) + sep + String.format("%02d", now.getDayOfMonth());
	}
	
	// String SummernoteImagePath 만들기
	public String getSummernoteImagePath() {
	  return "/storage" + sep + "summernote";
	}
	
	
	// String filesystemName 만들기 (원래이름을 받아오는 이유는? 확장자를 받아오기 위해서)
	public String getFilesystemName(String originName) {
		
		// 원래 첨부 파일의 확장자 꺼내기
		String extName = null;
		
		// 확장자에 마침표(.)가 포함된 예외 상황 처리
		if(originName.endsWith("tar.gz")) {
			extName = "tar.gz";
		} else {
			// split(정규식)
			// 정규식에서 마침표(.)는 모든 문자를 의미하므로 이스케이프 처리하거나 문자 클래스로 처리한다.
			// 이스케이프 처리  : \.
			// 문자 클래스 처리 : [.] 
			String[] array = originName.split("\\.");
			extName = array[array.length - 1];  // 배열의 마지막 인덱스
		}
		
		// 결과 반환
		// UUID.extName
		// 하이픈이 db에 그냥 저장되면 공간낭비니까 하이픈 빼주기 (자바 6장 random에 있음)
		return UUID.randomUUID().toString().replace("-", "") + "." + extName;
		
	}
	
	// String tempPath 만들기
	public String getTempPath() {
		return "/storage" + sep + "temp";
	}
	
	// String tempfileName 만들기 (zip 파일)
	public String getTempFileName() {
		return UUID.randomUUID().toString().replace("-", "") + ".zip";
	}
	
	// String yesterdayPath 만들기
	public String getYesterdayPath() {
		LocalDate date = LocalDate.now();
		date.minusDays(1);  // 1일 전
		return "/storage" + sep + date.getYear() + sep + String.format("%02d", date.getMonthValue()) + sep + String.format("%02d", date.getDayOfMonth());
	}
	
}
