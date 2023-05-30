package com.gdu.app12.util;

import java.security.MessageDigest;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
  
  // 크로스 사이트 스크립팅(Cross Site Scripting) 방지하기
  public String preventXSS(String str) { // 전달된 String을 가공을 해서 반환을 해준다(간단). "<" 로 들어오면 &lt;("<") 코드로 바꿔서 반환한다. <script> -> &lt;script&gt; -> 웹화면에서는 <script>로 보임
    str = str.replace("<", "&lt;");
    str = str.replace(">", "&gt;");
    return str;
  }
  
  // 인증코드 반환하기
  public String getRandomString(int count, boolean letters, boolean numbers) {
    return RandomStringUtils.random(count, letters, numbers);
  }
  
  // SHA-256 암호화하기
  /*
    1. 입력 값을 256비트(32바이트) 암호화 처리하는 해시 알고리즘
    2. 암호화는 할 수 있지만 복호화는 불가능한 알고리즘
    3. 암호화된 결과를 저장하기 위한 32바이트 byte 배열이 필요하다.
    4. 1바이트 -> 16진수로 변환해서 암호화된 문자열을 만든다. (1바이트=8비트 10001010-> 앞4자리, 뒤4자리를 16진수로 바꿔서 표현 => 8A, 1바이트→16진수 문자열 2개 나옴)
    5. 32바이트 -> 16진수로 변환하면 64글자가 생성된다. (DB 칼럼의 크기를 VARCHAR2(64 BYTE)로 설정한다.)
    6. java.security 패키지를 이용한다.(별도 라이브러리를 받아온 것이 아님)
  */
  public String getSha256(String str) {
    MessageDigest messageDigest = null;
    try {
      messageDigest = MessageDigest.getInstance("SHA-256"); // 알고리즘의 이름은 SHA-256. 이름 잘못 썼을 때 try-catch필요. 필수로 써줘야 함
      messageDigest.update(str.getBytes()); // 해당 비밀번호를 받아와서(str) 바이트 배열로 바꿔주면 알아서 변환한다. (messageDigest에 str바이트배열을 넣어준다?)
    } catch (Exception e) {
      e.printStackTrace();
    }
    byte[] b = messageDigest.digest();  // 암호화된 32바이트 크기의 byte 배열 b가 생성된다.
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < b.length; i++) {
      sb.append( String.format("%2X", b[i]) );  // %X : 16진수를 의미, %2X : 2자리의 16진수를 의미(X:대문자로 표현, x:소문자) -> 여기서 64글자 생성됨.
    }
    return sb.toString();
  }
  
  
  
}
