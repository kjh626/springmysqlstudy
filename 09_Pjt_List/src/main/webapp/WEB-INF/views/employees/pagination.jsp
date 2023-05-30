<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="${contextPath}/resources/js/lib/jquery-3.6.4.min.js"></script>
<script>
	$(function(){
		// recordPerPage의 변경
		$('#recordPerPage').on('change', function(){
			// alert($(this).val());
			location.href = '${contextPath}/employees/change/record.do?recordPerPage=' + $(this).val();  // session에 recordPerPage 올리기
		})
		
		// 세션에 저장된 recordPerPage값으로 <select> 태그의 값을 세팅
		let recordPerPage = '${sessionScope.recordPerPage}' == '' ? '10' : '${sessionScope.recordPerPage}';
		$('#recordPerPage').val(recordPerPage);
		
		// 제목을 클릭하면 정렬 방식을 바꿈
		// 지금 내가 무슨 정렬이라는 것을 넘기는 게 아니라, 해야될 일을 넘긴다.(오름차순 정렬(기본)이면 내림차순(DESC)값을 넘겨야 한다.)
		// 이 값을 누가 알고 있어야 한다. 태그에 data속성으로 값을 저장해준다.(data-order="DESC").
		// data속성값을 꺼내서 파라미터 값으로 추가시켜줘야 한다. data 속성 쓰는방법 => jquery 함수로 가능.data('order')
		// pagination.do로 넘어가는 정보가 하나 더 생겼다......................... 파라미터 order를 받아준다.(서비스임플에서)
		// 현재가 ASC값을 가지고 있다면 view쪽으로는 DESC를 전달해주겠다. 반대도 똑같다.
		// 사원번호 클릭 -> DESC가 파라미터로 넘어간다. DESC를 받으면 이제 jsp쪽으로 ASC를 보내준다.
		// 상황: 현재 정렬이 ASC이면 DESC를 전달해줘야한다. 그래야 정렬이 바뀌니까
		// 다시 서비스에서 이미 ASC를 처리했다. 그러면 jsp쪽에는 DESC를 전달할 준비를 하고 있어야 한다. ServiceImpl에서 jsp로 전달할 데이터 작성(ASC(service)→jsp로 DESC 전달)
		// 서비스가 알려준 정렬방식(model에 저장) : data-order속성에 EL값으로 order(ASC 또는 DESC)값을 불러올 수 있다.
		// 지금은 파라미터가 2개가 전달되고 있는데 page, order -> 지금은 order값만 넘겨주고 있고 page는 전달x 그래서 1 => 서비스임플은 알고 있으니 page가 몇인지 받아오면 된다.
		// 밑에 붙은 pagination 태그의 a링크에 order값 파라미터로 보내고 있지 않기 때문에 order가 풀리게 된다.(기본값 ASC로 되어버림)
		// 그래서 서비스임플의 model.addAttribute("pagination", pageUtil.getPagination(request.getContextPath() + "/employees/pagination.do"));에서 order값을 전달해줘야 한다.
			// 뒷부분에서 경로 전달하고 있는데 여기서 파라미터도 전달할 수 있게 바꿔준다. ?order=..
		$('.title').on('click', function(){
			location.href = '${contextPath}/employees/pagination.do?column=' + $(this).data('column') + '&order=' + $(this).data('order') + '&page=${page}';
		})
	})
</script>
<style>
	.title {
		cursor: pointer;
	}
	.title:hover {
		color: gray;
	}
	.title:active {
		color: silver;
	}
	.pagination {
		width: 350px;
		margin: 0 auto;
		text-align: center;
	}
	.pagination span, .pagination a {
		display: inline-block;
		margin: 0 15px;
	}
	.hidden {
		visibility: hidden;
	}
	.strong {
		font-weight: 900;
	}
	.link {
		color: orange;
	}
	table {
		width: 1500px;
	}
	table td:nth-of-type(1) { width: 80px; }
	table td:nth-of-type(2) { width: 80px; }
	table td:nth-of-type(3) { width: 200px; }
</style>
</head>
<body>
	
	<%-- 셀렉트 써서 한번에 몇개를 보여줄 것인지 --%>
	<div>
		<h1>사원 목록</h1>
		<div>
			<select id="recordPerPage">
				<option value="10">10개</option>
				<option value="20">20개</option>
				<option value="30">30개</option>
			</select>
		</div>
		<%-- select값이 change 되었다는 이벤트메소드가 필요하다. (recordPerPage)
		     그리고 이 정보를 어디에다가 저장해둘 것인가? 
		     파라미터는x => session으로 할 예정. 세션으로 저장하면 어디서든 빼서 쓸 수 있다.(페이지가 이동하더라도.. (10개) 여전히 유지하고 있을 것) 
		--%>
		
		<%-- 프로젝트 할 때는 <hr>쓰지 말고 css로 이쁘게 해라 --%>
		<hr>
		<table border="1">
			<thead>
				<tr>
					<td>순번</td>
					<td><span class="title" data-column="E.EMPLOYEE_ID" data-order="${order}">사원번호</span></td>
					<td><span class="title" data-column="E.FIRST_NAME" data-order="${order}">사원명</span></td>
					<td><span class="title" data-column="E.EMAIL" data-order="${order}">이메일</span></td>
					<td><span class="title" data-column="E.PHONE_NUMBER" data-order="${order}">전화번호</span></td>
					<td><span class="title" data-column="E.HIRE_DATE" data-order="${order}">입사일자</span></td>
					<td><span class="title" data-column="E.JOB_ID" data-order="${order}">직업</span></td>
					<td><span class="title" data-column="E.SALARY" data-order="${order}">연봉</span></td>
					<td><span class="title" data-column="E.COMMISSION_PCT" data-order="${order}">커미션</span></td>
					<td><span class="title" data-column="E.MANAGER_ID" data-order="${order}">매니저</span></td>
					<td><span class="title" data-column="E.DEPARTMENT_ID" data-order="${order}">부서번호</span></td>
					<td><span class="title" data-column="D.DEPARTMENT_NAME" data-order="${order}">부서명</span></td>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${employees}" var="emp" varStatus="vs">
					<tr>
						<td>${beginNo - vs.index}</td>
						<%-- empDTO에 있는 필드이름을 가져다 쓰는 거니까 잘 매칭해라 --%>
						<td>${emp.employeeId}</td>
						<td>${emp.firstName} ${emp.lastName}</td>
						<td>${emp.email}</td>
						<td>${emp.phoneNumber}</td>
						<td>${emp.hireDate}</td>
						<td>${emp.jobId}</td>
						<td>${emp.salary}</td>
						<td>${emp.commissionPct}</td>
						<td>${emp.managerId}</td>
						<td>${emp.deptDTO.departmentId}</td>
						<td>${emp.deptDTO.departmentName}</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="12">
						<%-- 숫자 1 2 3 이렇게 만들어서 한번에 뿌리려고 이렇게 함 --%>
						${pagination}
					</td>
				</tr>
			</tfoot>
		</table>
	</div>
	
	
	
	
	
</body>
</html>