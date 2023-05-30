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

	// 취소하면 이전 페이지로 돌아간다.
  function fnCancel(){
    $('#btnCancel').on('click', function(){
			history.back();
    })
  }
	
	// 모두 동의
	function fnCheckAll() {
		$('#checkAll').on('click', function(){
			$('.checkOne').prop('checked', $(this).prop('checked'));
		})
	}
	
	// 개별 선택
	function fnCheckOne() {
		// 개별 선택 체크박스를 처음부터 끝까지 다 본다. 1ㅁ,2ㅁ,3ㅁ 를 누구든 클릭하면 이 펑션에 걸린다. 그러면 처음부터 끝까지 다 본다. 체크됐는지 안됐는지 전부 확인! 누구든 상관없다.
		// chkCnt에 누적을 시켜서 확인해본다
		let chkOne = $('.checkOne');  // 모든 개별선택.  많이 쓰여서 변수처리 해줌
		$('.checkOne').on('click', function(){
			let chkCnt = 0;
			for(let i = 0; i < chkOne.length; i++){      // 배열이름이 $('.chk_one')이다.
				chkCnt += $(chkOne[i]).prop('checked');  // 값은 false 또는 true(false=0, true=1)
			}
			$('#checkAll').prop('checked', chkCnt == chkOne.length);  // 값이 같으면 모두 선택(true) 아니면 false겠지 
		})
	}
	
	
	// 가입 페이지로 이동하기(frmAgree의 submit)
	 function fnFrmAgreeSubmit(){
	  $('#frmAgree').on('submit', function(event){
		  if( $('#service').is(':checked') == false || $('#privacy').is(':checked') == false ){
			  alert('필수 약관에 동의해야만 가입할 수 있습니다.');
			  event.preventDefault();
			  return;
		  }
	  })
  }
	
	// 함수 호출
	$(function(){
		fnCancel();
		fnCheckAll();
		fnCheckOne();
		fnFrmAgreeSubmit();
	})
	
</script>
</head>
<body>

  <div>
  
    <h1>약관 동의하기</h1>
    
    <form id="frmAgree" action="${contextPath}/user/join.form">
    
      <div>
        <input type="checkbox" id="checkAll">
        <label for="checkAll">모두 동의</label>
      </div>
      
      <hr>
      <%-- 필수 약관들은 name이 없다. 파라미터 필수로 무조건 넘어가기 때문에 --%>
      <div>
        <input type="checkbox" id="service" class="checkOne">
        <label for="service" >이용약관 동의(필수)</label>
        <div>
          <textarea>본 약관은 ...</textarea>
        </div>
      </div>
      
      <div>
        <input type="checkbox" id="privacy" class="checkOne">
        <label for="privacy">개인정보수집 동의(필수)</label>
        <div>
          <textarea>개인정보보호법에 따라 ...</textarea>
        </div>
      </div>
      
      <%-- 가입화면으로 넘어갈 때(join.jsp) 파라미터 location, event 넘어간다.
           체크박스의 value속성이 없다. 체크박스에 value가 없을 때 그러면 체크를 하면 on이라는 값으로 넘어간다.
           체크를 안 하면 location이라는 파라미터가 애초에 전달되지 않는다.(off값 전달 x)
           => location값이 넘어갈 수도 있고, 안 넘어갈 수도 있는 것
           => 파라미터 location은 필수가 아니다. => @RequestParam 에 required=false를 작성해줘야 함! --%>
      <div>
      	<!-- 
      		1. 체크한 경우 : 파라미터 location이 on값을 가지고 전달된다.
      		2. 체크 안 한 경우 : 파라미터 location이 전달되지 않는다.
      	-->
        <input type="checkbox" id="location" name="location" class="checkOne">
        <label for="location">위치정보수집 동의(선택)</label>
        <div>
          <textarea>위치정보 ...</textarea>
        </div>
      </div>
      
      <div>
      	<!-- 
      		1. 체크한 경우 : 파라미터 event가 on값을 가지고 전달된다.
      		2. 체크 안 한 경우 : 파라미터 event가 전달되지 않는다.
      	-->
        <input type="checkbox" id="event" name="event" class="checkOne">
        <label for="event">이벤트 동의(선택)</label>
        <div>
          <textarea>이벤트 ...</textarea>
        </div>
      </div>
      
      <hr>
      
      <div>
        <input type="button" value="취소" id="btnCancel">
        <button>다음</button>
      </div>
    
    </form>
    
  </div>
  
</body>
</html>