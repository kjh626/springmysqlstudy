package com.gdu.app09.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.gdu.app09.domain.EmpDTO;
import com.gdu.app09.mapper.EmployeeListMapper;
import com.gdu.app09.util.PageUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor    // field에 @Autowired 처리가 된다.
@Service
public class EmployeeListServiceImpl implements EmployeeListService {
	
	// field 빈이 2개 적혀있으면, 일반적으로 생성자나 메소드로 처리
	private EmployeeListMapper employeeListMapper;
	private PageUtil pageUtil;

	@Override
	public void getEmployeeListUsingPagination(HttpServletRequest request, Model model) {
		
		// 파라미터 page가 전달되지 않는 경우 page=1 로 처리한다.
		Optional<String> opt1 = Optional.ofNullable(request.getParameter("page"));
		int page = Integer.parseInt(opt1.orElse("1"));
		
		// 전체 레코드 개수를 구한다.(DB에서 구해온다 => 불러주기만 하면 끝남)
		int totalRecord = employeeListMapper.getEmployeeCount();
		
		// 세션에 있는 recordPerPage를 가져온다. 세션에 없는 경우 recordPerPage=10으로 처리한다. 세션에 있는 데이터는 Object타입
		HttpSession session = request.getSession();
		Optional<Object> opt2 = Optional.ofNullable(session.getAttribute("recordPerPage"));
		int recordPerPage = (int) (opt2.orElse(10));
		
		// 파라미터 order (DESC가 넘어왔다고 생각해라)
		// 항상 오지 않음. 안 눌렀을 때 오지 않는다. 페이지처럼 첫 화면일 때 오지 않는다. => 첫 기본 정렬은 오름차순으로 하자 (우리가 정한다 ASC)
		// 파라미터 order가 전달되지 않는 경우 page=ASC 로 처리한다.
		Optional<String> opt3 = Optional.ofNullable(request.getParameter("order"));
		String order = opt3.orElse("ASC");    // 이 받은 파라미터를 DB로 보내야한다. -> DB로 보낼 Map 에서 작업(map.put())
		
		// 파라미터 column이 전달되지 않는 경우 column=EMPLOYEE_ID로 처리한다.
		Optional<String> opt4 = Optional.ofNullable(request.getParameter("column"));
		String column = opt4.orElse("EMPLOYEE_ID");
		
		// PageUtil(Pagination에 필요한 모든 정보(9개 필드값)) 계산하기
		pageUtil.setPageUtil(page, totalRecord, recordPerPage);
		
		// DB로 보낼 Map 만들기(PageUtil에서 계산한 begin과 end값 가져다 map에 저장)
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("begin", pageUtil.getBegin());
		map.put("end", pageUtil.getEnd());
		map.put("order", order);
		map.put("column", column);
		
		// begin ~ end 사이의 목록 가져오기
		List<EmpDTO> employees = employeeListMapper.getEmployeeListUsingPagination(map); 
		
		// pagination.jsp로 전달할(forward)할 정보를 저장하기
		model.addAttribute("employees", employees);
		// jsp에서 쓸 ${pagination} 만들어서 보내줘야한다.
		model.addAttribute("pagination", pageUtil.getPagination(request.getContextPath() + "/employees/pagination.do?&column=" + column + "&order=" + order));
		/*
			순번 붙이기 - 맨 처음이 107, 첫번째 사람의 번호만 구하면 된다. 그리고 인덱스를 빼면 됨
			beginNo = totalRecord - (page - 1) * recordPerPage 
						107 - (1-1)*10 = 107 
						107 - (2-1)*10 = 97
						107 - 20       = 87
		*/
		model.addAttribute("beginNo", totalRecord - (page - 1) * recordPerPage);
		
		// ServiceImpl에서 jsp로 전달할 데이터 작성(ASC(service)→jsp로 DESC 전달)
		switch(order) {
		case "ASC" : model.addAttribute("order", "DESC"); break;   // 현재 ASC 정렬이므로 다음 정렬은 DESC이라고 Jsp에 알려준다.
		case "DESC" : model.addAttribute("order", "ASC"); break;   // 현재 DESC 정렬이므로 다음 정렬은 ASC이라고 Jsp에 알려준다.
		}
		model.addAttribute("page", page);
	}

	// 실무처럼 하면 리스트로 반환하지 않고, 맵에 리스트를 담는다. 그러면 추가할 리스트가 있으면 맵에 더 담아주면 된다.(확장 가능!)
	@Override
	public Map<String, Object> getEmployeeListUsingScroll(HttpServletRequest request) {
		
		// 파라미터 page가 전달되지 않는 경우 page=1 로 처리한다. 
		Optional<String> opt1 = Optional.ofNullable(request.getParameter("page"));
		int page = Integer.parseInt(opt1.orElse("1"));
		
		// 전체 레코드 개수를 구한다.(DB에서 구해온다 => 불러주기만 하면 끝남)
		int totalRecord = employeeListMapper.getEmployeeCount();
		
		// recordPerPage = 9로 처리한다.
		int recordPerPage = 9;
		
		// PageUtil(Pagination에 필요한 모든 정보) 계산하기
		pageUtil.setPageUtil(page, totalRecord, recordPerPage);
		
		// DB로 보낼 Map 만들기
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("begin", pageUtil.getBegin());
		map.put("end", pageUtil.getEnd());
		
		// begin ~ end 사이의 목록 가져오기
		List<EmpDTO> employees = employeeListMapper.getEmployeeListUsingScroll(map); 
		
		// scroll.jsp 로 응답할 데이터
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("employees", employees);
		resultMap.put("totalPage", pageUtil.getTotalPage());
		
		// 응답
		return resultMap;
		
		/* 
			resultMap이 json으로 변환될 때의 모습
			                         -> 이게 resData의 모습이 된다.
			{ 
				"employees": [
					{
						"employeeId": 100,
						"firstName": "Steven",
						"lastName": "King",
						...,
						"deptDTO": {
							"departmentId": 90,
							"departmentName": "Executive"
							...
						}
					},
					{
						"employeeId": 101,
						"firstName": "Neena",
						"lastName": "Kochhar",
						...,
						"deptDTO": {
							"departmentId": 90,
							"departmentName": "Executive"
							...
						}
					},
					...
				],
				"totalPage": 12
			}
		*/
	}
	
	
	
	@Override
	public void getEmployeeListUsingSearch(HttpServletRequest request, Model model) {
		
		// 파라미터 column이 전달되지 않는 경우 column=""로 처리한다. (column이 없으면 null값, 처음 화면이동 요청..)
		Optional<String> opt1 = Optional.ofNullable(request.getParameter("column"));
		String column = opt1.orElse("");
		
		// 파라미터 query가 전달되지 않는 경우 query=""로 처리한다. (query가 없으면 null값, 처음 화면이동 요청..)
		Optional<String> opt2 = Optional.ofNullable(request.getParameter("query"));
		String query = opt2.orElse("");
		
		// DB로 보낼 Map에 column,query 추가하기
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("column", column);
		map.put("query", query);
		
		// 파라미터 page가 전달되지 않는 경우 page=1 로 처리한다.
		Optional<String> opt3 = Optional.ofNullable(request.getParameter("page"));
		int page = Integer.parseInt(opt3.orElse("1"));
		
		// 검색된 레코드 개수를 구한다.(DB에서 구해온다 => 불러주기만 하면 끝남)
		int totalRecord = employeeListMapper.getEmployeeSearchCount(map);
		
		// recordPerPage = 10으로 처리한다.
		int recordPerPage = 10;
		
		// PageUtil(Pagination에 필요한 모든 정보(9개 필드값)) 계산하기
		pageUtil.setPageUtil(page, totalRecord, recordPerPage);

		// DB로 보낼 Map에 begin,end 추가하기
		map.put("begin", pageUtil.getBegin());
		map.put("end", pageUtil.getEnd());

		// begin ~ end 사이의 목록 가져오기
		List<EmpDTO> employees = employeeListMapper.getEmployeeListUsingSearch(map); 
		
		// search.jsp로 전달할(forward)할 정보를 저장하기
		model.addAttribute("employees", employees);
		model.addAttribute("pagination", pageUtil.getPagination(request.getContextPath() + "/employees/search.do?column=" + column + "&query=" + query));
		model.addAttribute("beginNo", totalRecord - (page - 1) * recordPerPage);
	}
	
	@Override
	public Map<String, Object> getAutoComplete(HttpServletRequest request) {

		// 파라미터 column이 전달되지 않는 경우 column=""로 처리한다.
		Optional<String> opt1 = Optional.ofNullable(request.getParameter("column"));
		String column = opt1.orElse("");
		
		// 파라미터 query가 전달되지 않는 경우 query=""로 처리한다.
		Optional<String> opt2 = Optional.ofNullable(request.getParameter("query"));
		String query = opt2.orElse("");
		
		// DB로 보낼 Map에 column,query 추가하기
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("column", column);
		map.put("query", query);
		
		// 검색 결과 목록 가져오기
		List<EmpDTO> employees = employeeListMapper.getAutoComplete(map);
		
		// scroll.jsp 로 응답할 데이터
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("employees", employees);
		
		// 응답
		return resultMap;
	}
	
	
	
}
