package com.gdu.app09.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdu.app09.service.EmployeeListService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class EmployeeController {
	
	private final EmployeeListService employeeListService;
	
	@GetMapping("/employees/pagination.do")
	public String pagination(HttpServletRequest request, Model model) {
		employeeListService.getEmployeeListUsingPagination(request, model);
		return "employees/pagination";
	}
	
	// 세션쓰고싶으면 세션으로 매개변수 받으면 됨, @RequestParm 생략 => request 같이 들어가도 된다. 그래도 일단 적어놔라
	@GetMapping("/employees/change/record.do")
	public String changeRecord(HttpSession session
							  , HttpServletRequest request
			                  , @RequestParam(value="recordPerPage", required=false, defaultValue="10") int recordPerPage) {
	session.setAttribute("recordPerPage", recordPerPage);
	return "redirect:" + request.getHeader("referer");    // 현재 주소(/employees/change/record.do)의 이전 주소(Referer값)로 이동하시오.     
		
	}
	
	@GetMapping("/employees/scroll.page")
	public String scrollPage() {
		return "employees/scroll";
	}
	
	@ResponseBody
	@GetMapping(value="/employees/scroll.do", produces="application/json")
	public Map<String, Object> scroll(HttpServletRequest request){
		return employeeListService.getEmployeeListUsingScroll(request); 
	}
	
	// 서치 화면에서도 회원 목록을 뿌려줄 필요가 있어서 pagination에서 목록 불러온 것 거의 다 복사해서 작업해줘야한다.
	// 추가 파라미터를 통해 검색을 구현해야 함
	@GetMapping("/employees/search.do")
	public String search(HttpServletRequest request, Model model) {
		employeeListService.getEmployeeListUsingSearch(request, model);
		return "employees/search";
	}
	
	@ResponseBody
	@GetMapping(value="/employees/AutoComplete.do", produces="application/json")
	public Map<String, Object> AutoComplete(HttpServletRequest request){
		return employeeListService.getAutoComplete(request); 
	}
	
}
