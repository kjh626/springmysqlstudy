package com.gdu.app09.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

public interface EmployeeListService {
	// 직접 이동 시키는 방식으로
	public void getEmployeeListUsingPagination(HttpServletRequest request, Model model);
	public Map<String, Object> getEmployeeListUsingScroll(HttpServletRequest request);
	public void getEmployeeListUsingSearch(HttpServletRequest request, Model model);
	// 이 서비스는 AJAX로 동작시킬 서비스다 (JSON으로 반환해야 해서 MAP타입.. 가지고 온 데이터 그대로 반환) => ResponseEntity 쓸 거 아니면 Map 쓰면 무난하다.(ResponseEntity쓸 거면 여기 안에 맵 넣으면 되지)
	public Map<String, Object> getAutoComplete(HttpServletRequest request);
}
