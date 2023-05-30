package com.gdu.app09.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.app09.domain.EmpDTO;

@Mapper
public interface EmployeeListMapper {

	public int getEmployeeCount();
	// 데이터 begin, end 전달하는 방법 우리의 선택은 Map  (+ PageUtil도 가능)
	public List<EmpDTO> getEmployeeListUsingPagination(Map<String, Object> map);
	public List<EmpDTO> getEmployeeListUsingScroll(Map<String, Object> map);
	public List<EmpDTO> getEmployeeListUsingSearch(Map<String, Object> map);
	public int getEmployeeSearchCount(Map<String, Object> map);
	public List<EmpDTO> getAutoComplete(Map<String, Object> map);
}
