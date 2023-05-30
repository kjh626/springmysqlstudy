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
	var frm;
	$(function(){
		frm = $('#frm');
	})
	function fnEditUpload(){
		frm.prop('action', '${contextPath}/upload/editUpload.do');
		frm.submit();
	}
	function fnRemoveUpload(){
		if(confirm('게시글을 삭제하면 모든 첨부 파일이 함께 삭제됩니다. 그래도 삭제하시겠습니까?') == false){
			return;
		}
		frm.prop('action', '${contextPath}/upload/removeUpload.do');
		frm.submit();
	}
	let modifyResult = '${modifyResult}';
	if(modifyResult != ''){
		if(modifyResult == '1'){
			alert('UPLOAD 게시글 수정 성공');
		} else {
			alert('UPLOAD 게시글 수정 실패');
		}
	}
</script>
</head>
<body>

	<div>
		<h1>${upload.uploadNo}번 UPLOAD 게시글</h1>
		<ul>
			<li>제목 : ${upload.uploadTitle}</li>
			<li>내용 : ${upload.uploadContent}</li>
			<li>작성일자 : ${upload.createdAt}</li>
			<li>수정일자 : ${upload.modifiedAt}</li>
		</ul>
		<form id="frm" method="post">
			<input type="button" value="편집" onclick="fnEditUpload()">
			<input type="hidden" name="uploadNo" value="${upload.uploadNo}">
			<input type="button" value="삭제" onclick="fnRemoveUpload()">
		</form> 
	</div>
	
	<hr>
	
	<div>
		<h4>첨부 목록 및 다운로드</h4>
		<c:if test="${empty attachList}">
			<div>첨부된 파일이 없습니다.</div>
		</c:if>
		<c:if test="${not empty attachList}">
			<div>
				<c:forEach items="${attachList}" var="attach">
					<div>
						<a href="${contextPath}/upload/download.do?attachNo=${attach.attachNo}">
							<c:if test="${attach.hasThumbnail == 1}">
								<%-- 썸네일이 없으면 정적 이미지 파일 가져다 쓰면 되는데, 있을 경우가 어떻게 표시할 지가 문제다. 
									이전에 배움-> 3장에서 이미지 경로와 이름을 받아와서 바이트배열로 데이터를 받아와서 뿌려주는 형식으로 연습했었음. --%>
								<%-- 경로,이름 정보 넘겼었는데, 첨부번호만 넘기면 다 알 수 있으니 이거 하나만 넘기자.
									 /upload/display?attachNo 이 서비스가 어떻게 동작되는 지를 잘 이해해봐라 --%>
								<img src="${contextPath}/upload/display.do?attachNo=${attach.attachNo}">
							</c:if>
							<c:if test="${attach.hasThumbnail == 0}">
							<%-- servlet-context.xml에 resources관련 태그 있음. /resources/ 매핑은 우리가 이미지 저장해놓은 웹앱의 resources폴더로 연결해준다. --%>
								<img src="${contextPath}/resources/images/attach1.png" width="50px">
							</c:if>
							${attach.originName}
						</a>
						<%-- 1.각 첨부의 옆에 삭제버튼 만들어서 삭제버튼 누르면 각 첨부 지우기 (수정화면 넘어갔을 때 수정할 수 있게 되었을 때 하면 됨, 제목,내용 수정, 첨부 수정), 
						     2. 게시글의 삭제 누르면 첨부 지워지게 하기 (오늘 함)--%>
					</div>
				</c:forEach>
				<div>
					<a href="${contextPath}/upload/downloadAll.do?uploadNo=${upload.uploadNo}">모두 다운로드</a>
				</div>
			</div>
		</c:if>
	</div>
	
</body>
</html>