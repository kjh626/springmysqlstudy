<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<jsp:include page="../layout/header.jsp">
	<jsp:param name="title" value="${blog.blogNo }번 블로그" />
</jsp:include>
	
<style>
	.blind {
		display: none;
	}
</style>

	
<div>

	<h1>${blog.title }</h1>
	
	<div>
		<div>작성자 : ${blog.memberDTO.name }</div>
		<div>작성일 : ${blog.createdAt }</div>
		<div>수정일 : ${blog.modifiedAt }</div>
		<div>조회수 : ${blog.hit }</div>
	</div>
	
	<hr>
	
	<div>${blog.content }</div>

	<!-- onclick 이 아니고 아이디를 부여한 상태 -->
	<!-- 굳이 form을 쓴 이유: post방식으로 이동하기 위해서(주소창을 이용한 접근을 막기 위해, 누가 주소창으로 삭제할 수 없게함) -->
	<!-- GET방식의 대표적인 방법 : <a href=> , location.href 2가지 -->
	<div>
		<form id="frmBtn" method="post">
			<input type="hidden" name="blogNo" value="${blog.blogNo }">
			<c:if test="${sessionScope.loginId eq blog.memberDTO.id }">
				<input type="button" value="편집" id="btnEdit">
				<input type="button" value="삭제" id="btnRemove">
			</c:if>
			<input type="button" value="목록" id="btnList">
		</form>
		<script>
			function fnEdit(){
				$('#btnEdit').on('click', function(){
					$('#frmBtn').attr('action', '${contextPath }/blog/edit.do');
					$('#frmBtn').submit();
				})
			}
			function fnRemove(){
				$('#btnRemove').on('click', function(){
					if(confirm('블로그를 삭제하면 모든 댓글이 함께 삭제됩니다. 삭제할까요?')){
						$('#frmBtn').attr('action', '${contextPath }/blog/remove.do');
						$('#frmBtn').submit();
					}
				})
			}
			// list.do는 GET 매핑이다.
			function fnList(){
				$('#btnList').on('click', function(){
					location.href = '${contextPath }/blog/list.do';
				})
			}
			// 함수 호출
			fnEdit();
			fnRemove();
			fnList();
		</script>
	</div>

	<hr>
	
  <!-- 댓글 구역 -->
  
  <div id="btnGood" style="width: 100px; border: 1px solid silver;">
    <span id="heart"></span>
    <span id="good">좋아요</span>
    <span id="goodCount"></span>
  </div>
  <script>
  
  </script>
  
  <div>
    <form id="frmAddComment">
      <input type="text" name="content" id="content" placeholder="댓글 작성해 주세요">
      <input type="hidden" name="blogNo" value="${blog.blogNo}">
      <input type="hidden" name="memberNo" value="${sessionScope.loginNo}">
      <input type="button" value="작성완료" id="btnAddComment">
    </form>
    <script>
      function fnLoginCheck(){
    	  $('#content').on('focus', function(){
    		  if('${sessionScope.loginId}' == ''){
    			  if(confirm('로그인이 필요한 기능입니다. 로그인할까요?')){
    				  location.href = '${contextPath}/index.do';
    			  }
    		  }
    	  })
      }
      function fnAddComment(){
    	  $('#btnAddComment').on('click', function(){
    		  if($('#content').val() == ''){
    			  alert('댓글 내용을 입력하세요.');
    			  return;
    		  }
    		  $.ajax({
    			  type: 'post',
    			  url: '${contextPath}/comment/addComment.do',
    			  data: $('#frmAddComment').serialize(),
    			  dataType: 'json',
    			  success: function(resData){  // resData = {"isAdd": true}
    				  if(resData.isAdd){
    					  alert('댓글이 등록되었습니다.');
    					  $('#content').val('');
    					  fnCommentList();  // 댓글 목록을 가져와서 화면에 만드는 함수
    				  }
    			  }
    		  })
    	  })
      }
      // 페이징할 떄 현재 몇 페이지인가를 알아야 한다.
      // 전역 변수. 이렇게 해주면 page 는 무조건 전달된다고 보면 됨.
      var page = 1;
      
      function fnCommentList(){
    	  $.ajax({
    		  type: 'get',
    		  url: '${contextPath}/comment/list.do',
    		  data: 'blogNo=${blog.blogNo}&page=' + page,
    		  dataType: 'json',
    		  success: function(resData){  // resData = { "commentList": [{}, {}, ...], "pageUtil": {beginPage: 1, endPage: 5, ...} }
    			  $('#commentList').empty();
    			  $.each(resData.commentList, function(i, comment){
    				  var str = '<div>';
    				  if(comment.state == -1){
    					  if(comment.depth == 0){    						  
      					  str += '<span>삭제된 댓글입니다.';
    					  } else {
    						  str += '<span style="margin-left: 30px;">삭제된 답글입니다.';
    					  }
    				  } else {
    					  if(comment.depth == 0){
    						  str += '<span>';
    					  } else {
    						  str += '<span style="margin-left: 30px;>"'
    					  }
      				  str += comment.memberDTO.name;
      				  str += ' - ' + comment.content;
      				  if('${sessionScope.loginId}' != ''){
      					  if('${sessionScope.loginId}' == comment.memberDTO.id && comment.state == 1){
      						  str += '<input type="button" value="삭제" class="btnCommentRemove" data-comment_no="' + comment.commentNo + '">';
      					  } else if('${sessionScope.loginId}' != comment.memberDTO.id && comment.depth == 0){
      						  str += '<input type="button" value="답글" class="btnOpenReply">';
      					  }
      				  }
      				  str += '<div class="replyArea blind">';
      				  str += '  <form class="frmReply">';
      				  str += '    <input type="text" name="content" placeholder="답글 작성해 주세요">';
      				  str += '    <input type="hidden" name="blogNo" value="' + comment.blogNo + '">';
      				  str += '    <input type="hidden" name="memberNo" value="' + comment.memberDTO.memberNo + '">';
      				  str += '    <input type="button" value="답글작성완료" class="btnAddReply">';
      				  str += '  </form>';
      				  str += '</div>';
    				  }
    				  $('#commentList').append(str);
    			  })
    		  }
    	  })
      }
      
      // 이벤트를 이렇게 쓰는 것은 동적으로 만든 객체 => 자바스크립트로 만든 객체들은 이벤트를 이렇게 만들어준다.
      // 토글 jquery 4장 DOM_operate 에 있음
      function fnToggleReplyArea(){
    	  $(document).on('click', '.btnOpenReply', function(){
    		  $(this).next().toggleClass('blind');
    	  })
      }
      
      fnLoginCheck();
      fnAddComment();
      fnCommentList();
	  fnToggleReplyArea();
    </script>
	</div>
	
	<div>
		<div id="commentList"></div>
		<div id="pagination"></div>
	</div>
	<script>
		
	</script>


</div>

</body>
</html>