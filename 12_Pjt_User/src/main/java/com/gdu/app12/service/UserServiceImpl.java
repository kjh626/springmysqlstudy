package com.gdu.app12.service;

import java.io.PrintWriter;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gdu.app12.domain.LeaveUserDTO;
import com.gdu.app12.domain.UserDTO;
import com.gdu.app12.mapper.UserMapper;
import com.gdu.app12.util.JavaMailUtil;
import com.gdu.app12.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor  // field에 @Autowired 처리를 위해서
@Service
public class UserServiceImpl implements UserService {

  // field
  private final UserMapper userMapper;
  private final JavaMailUtil javaMailUtil;
  private final SecurityUtil securityUtil;
  
  @Override
  public Map<String, Object> verifyId(String id) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("enableId", userMapper.selectUserById(id) == null && userMapper.selectSleepUserById(id) == null && userMapper.selectLeaveUserById(id) == null);
    return map;
  }
  
  @Override
  public Map<String, Object> verifyEmail(String email) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("enableEmail", userMapper.selectUserByEmail(email) == null && userMapper.selectSleepUserByEmail(email) == null && userMapper.selectLeaveUserByEmail(email) == null);
    return map;
  }
  
  @Override
  public Map<String, Object> sendAuthCode(String email) {
    
    // 사용자에게 전송할 인증코드 6자리
    String authCode = securityUtil.getRandomString(6, true, true);  // 6자리, 문자사용, 숫자사용
    
    // 사용자에게 메일 보내기
    javaMailUtil.sendJavaMail(email, "[앱이름] 인증요청", "인증번호는 <strong>" + authCode + "</strong>입니다.");
    
    // 사용자에게 전송한 인증코드를 join.jsp로 응답
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("authCode", authCode);
    return map;
    
  }
  
  @Override
  public void join(HttpServletRequest request, HttpServletResponse response) {
    
    // 요청 파라미터
    String id = request.getParameter("id");
    String pw = request.getParameter("pw");
    String name = request.getParameter("name");
    String gender = request.getParameter("gender");
    String email = request.getParameter("email");
    String mobile = request.getParameter("mobile");
    String birthyear = request.getParameter("birthyear");
    String birthmonth = request.getParameter("birthmonth");
    String birthdate = request.getParameter("birthdate");
    String postcode = request.getParameter("postcode");
    String roadAddress = request.getParameter("roadAddress");
    String jibunAddress = request.getParameter("jibunAddress");
    String detailAddress = request.getParameter("detailAddress");
    String extraAddress = request.getParameter("extraAddress");
    String location = request.getParameter("location");
    String event = request.getParameter("event");
    
    // 비밀번호 SHA-256 암호화
    pw = securityUtil.getSha256(pw);
    
    // 이름 XSS 처리
    name = securityUtil.preventXSS(name);
    
    // 출생월일
    birthdate = birthmonth + birthdate;
    
    // 상세주소 XSS 처리
    detailAddress = securityUtil.preventXSS(detailAddress);
    
    // 참고항목 XSS 처리
    extraAddress = securityUtil.preventXSS(extraAddress);
      
    // agreecode
    int agreecode = 0;
    if(location.isEmpty() == false && event.isEmpty() == false) {
      agreecode = 3;
    } else if(location.isEmpty() && event.isEmpty() == false) {
      agreecode = 2;
    } else if(location.isEmpty() == false && event.isEmpty()) {
      agreecode = 1;
    }
    
    // UserDTO 만들기
    UserDTO userDTO = new UserDTO();
    userDTO.setId(id);
    userDTO.setPw(pw);
    userDTO.setName(name);
    userDTO.setGender(gender);
    userDTO.setEmail(email);
    userDTO.setMobile(mobile);
    userDTO.setBirthyear(birthyear);
    userDTO.setBirthdate(birthdate);
    userDTO.setPostcode(postcode);
    userDTO.setRoadAddress(roadAddress);
    userDTO.setJibunAddress(jibunAddress);
    userDTO.setDetailAddress(detailAddress);
    userDTO.setExtraAddress(extraAddress);
    userDTO.setAgreecode(agreecode);
    
    // 회원가입(UserDTO를 DB로 보내기)
    int joinResult = userMapper.insertUser(userDTO);
    
    // 응답
    try {
      
      response.setContentType("text/html; charset=UTF-8");
      PrintWriter out = response.getWriter();
      out.println("<script>");
      if(joinResult == 1) {
        out.println("alert('회원 가입되었습니다.');");
        out.println("location.href='" + request.getContextPath() + "/index.do';");
      } else {
        out.println("alert('회원 가입에 실패했습니다.');");
        out.println("history.go(-2);");
      }
      out.println("</script>");
      out.flush();
      out.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
  @Override
  public void login(HttpServletRequest request, HttpServletResponse response) {
    
    // 요청 파라미터
    String url = request.getParameter("url");  // 로그인 화면의 이전 주소(로그인 후 되돌아갈 주소)
    String id = request.getParameter("id");
    String pw = request.getParameter("pw");
    
    // 비밀번호 SHA-256 암호화
    pw = securityUtil.getSha256(pw);
    
    // UserDTO 만들기
    UserDTO userDTO = new UserDTO();
    userDTO.setId(id);
    userDTO.setPw(pw);
    
    // DB에서 UserDTO 조회하기
    UserDTO loginUserDTO = userMapper.selectUserByUserDTO(userDTO);
    
    // ID, PW가 일치하는 회원이 있으면 로그인 성공
    // 0. 자동 로그인 처리하기(autoLogin 메소드에 맡기기)
    // 1. session에 ID 저장하기
    // 2. 회원 접속 기록 남기기
    // 3. 이전 페이지로 이동하기
    if(loginUserDTO != null) {
      
      // 자동 로그인 처리를 위한 autoLogin 메소드 호출하기
      autoLogin(request, response);
      
      HttpSession session = request.getSession();
      session.setAttribute("loginId", id);
      
      int updateResult = userMapper.updateUserAccess(id);
      if(updateResult == 0) {
        userMapper.insertUserAccess(id);
      }
      
      try {
        response.sendRedirect(url);
      } catch (Exception e) {
        e.printStackTrace();
      }
      
    }
    // ID, PW가 일치하는 회원이 없으면 로그인 실패
    else {
      
      // 응답
      try {
        
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('일치하는 회원 정보가 없습니다.');");
        out.println("location.href='" + request.getContextPath() + "/index.do';");
        out.println("</script>");
        out.flush();
        out.close();
        
      } catch (Exception e) {
        e.printStackTrace();
      }
      
    }
    
  }
  
  @Override
  public void autoLogin(HttpServletRequest request, HttpServletResponse response) {
    
    /*
      자동 로그인 처리하기
      
      1. 자동 로그인을 체크한 경우
        1) session의 id를 DB의 AUTOLOGIN_ID 칼럼에 저장한다. (자동 로그인은 본인의 아이디를 가지고 로그인을 하는 게 아니다. 
                                                         생각해보면 자동로그인은 비밀번호 입력이 없다. -> 아이디 자체가 나만 알고 있는 아이디여야 한다. 
                                                         비밀번호 없이도 나를 식별할 수 있어야 함. 자동로그인용 아이디는 기본적으로 중복이 없고, 다른사람이 알기 어려운 정보를 이용해서 자동로그인에서 사용할 ID를 결정한다. 
                                                          => 가장 적절한 게 바로 session의 아이디(자동으로 발급되고 브라우저 킬 때마다 자동으로 바뀐다. 그래서 중복되는 데이터가 없을 것))
        2) 자동 로그인을 유지할 기간(예시 : 15일)을 DB의 AUTOLOGIN_EXPIRED_AT 칼럼에 저장한다.
        3) session의 id를 쿠키로 저장한다. (쿠키 : 각 사용자의 브라우저에 저장되는 정보)
          이 때 쿠키의 유지 시간을 자동 로그인을 유지할 기간과 동일하게 맞춘다.
      
      2. 자동 로그인을 체크하지 않은 경우 (지워준다)
        1) DB에 저장된 AUTOLOGIN_ID 칼럼과 AUTOLOGIN_EXPIRED_AT 정보를 삭제한다.
        2) 쿠키를 삭제한다.
    */
    
    // 요청 파라미터 
    String id = request.getParameter("id");
    String chkAutoLogin = request.getParameter("chkAutoLogin");
    
    // 자동 로그인을 체크한 경우
    if(chkAutoLogin != null) {
      
      // session의 id를 가져온다.
      HttpSession session = request.getSession();
      String sessionId = session.getId();   // sessionId : 브라우저가 새롭게 열릴 때마다 자동으로 갱신되는 임의의 값
      
      // DB로 보낼 UserDTO 만들기
      UserDTO userDTO = new UserDTO();
      userDTO.setId(id);
      userDTO.setAutologinId(sessionId);
      userDTO.setAutologinExpiredAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 15));
                                    // 현재 + 15일 : java.sql.Date 클래스를 이용해서 작업을 수행한다.
                                    // java.sql.Date 클래스는 타임스탬프를 이용해서 날짜를 생성한다. 타임스탬프(밀리초:1000분의 1초)
      
      // DB로 UserDTO 보내기 (INSERT가 필요. update해줬음!)
      userMapper.insertAutologin(userDTO);
      
      // session id를 쿠키에 저장하기   .  쿠키의 기본은 서버가 쿠키를 만들어서 사용자(브라우저)에게 보낸다(응답). 그래서 쿠키저장은 응답(response)이 처리.
      Cookie cookie = new Cookie("autologinId", sessionId);
      cookie.setMaxAge(60 * 60 * 24 * 15);       // 초 단위로 15일 지정
      cookie.setPath(request.getContextPath());  // 우리는 언제나 유지하려고 한다. https://localhost:9090/app12/... 로 시작한다면 항상 쿠키값을 해석할 수 있도록 이 경로로 쿠키 경로를 잡아주고자 한다.
                                                 // ※ /app12/user , /app12/board 이렇게 주소 달라질 때마다 쿠키값이 계속 달라진다. <- 어떤경로, 특정주소에 따라 쿠키값이 바뀐다(보여주거나 안 보여준다.)
                                                 // => 쿠키값 주소를 contextPath로 잡아주면 어떤 주소값으로 넘어가든 쿠키값을 확인할 수 있다. 
                                                 // ==> 애플리케이션의 모든 URL에서 autoLoginId 쿠키를 확인할 수 있다.
      response.addCookie(cookie);                // 쿠키값 저장
    }
    // 자동 로그인을 체크하지 않은 경우
    else {
      
      // DB에서 AUTOLOGIN_ID 칼럼과 AUTOLOGIN_EXPIRED_AT 칼럼 정보 삭제하기
      userMapper.deleteAutologin(id);
    
      // autoLoginId 쿠키 삭제하기
      Cookie cookie = new Cookie("autologinId", "");
      cookie.setMaxAge(0);       // 쿠키 유지시간을 0초로 설정
      cookie.setPath(request.getContextPath());   // autoLoginId 쿠키의 path와 동일하게 설정
      response.addCookie(cookie);
      
    }
    
    
  }
  
  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    

    // 1. 자동 로그인을 해제한다.
    
    // DB에서 AUTOLOGIN_ID 칼럼과 AUTOLOGIN_EXPIRED_AT 칼럼 정보 삭제하기
    HttpSession session = request.getSession();
    String id = (String) session.getAttribute("loginId");  // 속성으로 저장할 때는 항상 Object이기 때문에 String으로 바꿔주려면 (String)으로 변환시켜줘야 한다.
    userMapper.deleteAutologin(id);
  
    // autoLoginId 쿠키 삭제하기
    Cookie cookie = new Cookie("autoLoginId", "");
    cookie.setMaxAge(0);       // 쿠키 유지시간을 0초로 설정
    cookie.setPath(request.getContextPath());   // autoLoginId 쿠키의 path와 동일하게 설정
    response.addCookie(cookie);

    // 2. session에 저장된 모든 정보를 지운다. (자동 로그인 먼저 해제.)    
    session.invalidate();
  }
  
  @Transactional
  @Override
  public void leave(HttpServletRequest request, HttpServletResponse response) {
    
    // 탈퇴할 회원의 ID는 session에 loginId 속성으로 저장되어 있다.
    HttpSession session = request.getSession();
    String id = (String) session.getAttribute("loginId");
    
    // 탈퇴할 회원의 정보(ID, EMAIL, JOINED_AT) 가져오기
    UserDTO userDTO = userMapper.selectUserById(id);
    
    // LeaveUserDTO 만들기
    LeaveUserDTO leaveUserDTO = new LeaveUserDTO();
    leaveUserDTO.setId(id);
    leaveUserDTO.setEmail(userDTO.getEmail());
    leaveUserDTO.setJoinedAt(userDTO.getJoinedAt());
    
    // 회원 탈퇴하기
    int insertResult = userMapper.insertLeaveUser(leaveUserDTO);
    int deleteResult = userMapper.deleteUser(id);
    
    // 응답
    try {
      
      response.setContentType("text/html; charset=UTF-8");
      PrintWriter out = response.getWriter();
      out.println("<script>");
      if(insertResult == 1 && deleteResult == 1) {
        
        // session 초기화
        session.invalidate();
        
        out.println("alert('회원 탈퇴되었습니다.');");
        out.println("location.href='" + request.getContextPath() + "/index.do';");
        
      } else {
        out.println("alert('회원 탈퇴에 실패했습니다.');");
        out.println("history.back();");
      }
      out.println("</script>");
      out.flush();
      out.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }

  @Transactional
  @Override
  public void sleepUserHandle() {
    int insertResult = userMapper.insertSleepUser();
    if(insertResult > 0) {
      userMapper.deleteUserForSleep();
    }
    System.out.println(insertResult);
  }
  
}











