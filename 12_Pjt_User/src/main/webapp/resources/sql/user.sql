-- 스키마
USE gdj61;

-- 테이블 삭제 순서는 테이블 생성 순서의 역순이다.
DROP TABLE IF EXISTS SLEEP_USER_T;
DROP TABLE IF EXISTS LEAVE_USER_T;
DROP TABLE IF EXISTS USER_ACCESS_T;
DROP TABLE IF EXISTS USER_T;

-- 회원
CREATE TABLE USER_T (
    USER_NO        INT          NOT NULL AUTO_INCREMENT,
    ID             VARCHAR(40)  NOT NULL UNIQUE,  -- ID 정규식에 반영. 40자 넘었는지 안 넘었는지
    PW             VARCHAR(64)  NOT NULL,         -- SHA-256 암호화 방식 사용 글자수에 상관없이 64자로 암호화된다.그래서 64BYTE
    NAME           VARCHAR(40),                  -- 이름
    GENDER         VARCHAR(2),                   -- M, F, NO
    EMAIL          VARCHAR(100) NOT NULL UNIQUE, -- 이메일
    MOBILE         VARCHAR(15),                  -- 하이픈 제외(-) 후 저장
    BIRTHYEAR      VARCHAR(4),                   -- 출생년도(YYYY)
    BIRTHDATE      VARCHAR(4),                   -- 출생월일(MMDD)
    POSTCODE       VARCHAR(5),                   -- 우편번호
    ROAD_ADDRESS   VARCHAR(100),                 -- 도로명주소
    JIBUN_ADDRESS  VARCHAR(100),                 -- 지번주소
    DETAIL_ADDRESS VARCHAR(100),                 -- 상세주소
    EXTRA_ADDRESS  VARCHAR(100),                 -- 참고항목
    AGREE_CODE     TINYINT      NOT NULL,        -- 동의여부(0:필수, 1:위치, 2:이벤트, 3:위치+이벤트)
    JOINED_AT      DATETIME,                     -- 가입일자
    PW_MODIFIED_AT DATETIME,                     -- 비밀번호변경일
    AUTOLOGIN_ID   VARCHAR(32),                  -- 자동로그인할 때 사용하는 ID(SESSION_ID를 사용함)   -- 1. 자동로그인할 아이디 2. 자동로그인 만료일
    AUTOLOGIN_EXPIRED_AT DATETIME,               -- 자동로그인 ID
	CONSTRAINT PK_USER PRIMARY KEY(USER_NO)    
);

-- 회원 접속 기록(회원마다 마지막 로그인 날짜 1개만 기록)
CREATE TABLE USER_ACCESS_T (
    ID            VARCHAR(40) NOT NULL UNIQUE,   -- 로그인한 사용자 ID. UNIQUE달아준 이유는 성능때문에,속도 향상
    LAST_LOGIN_AT DATETIME,                      -- 마지막 로그인 날짜
    CONSTRAINT FK_USER_ACCESS FOREIGN KEY(ID) REFERENCES USER(ID) ON DELETE CASCADE
);

-- 탈퇴 (탈퇴한 아이디로 재가입이 불가능)
CREATE TABLE LEAVE_USER_T (
    ID        VARCHAR(40) NOT NULL UNIQUE,
    EMAIL     VARCHAR(100) NOT NULL UNIQUE,
    JOINED_AT DATETIME, -- 가입일
    LEAVED_AT DATETIME  -- 탈퇴일
);

-- 휴면 (1년 이상 로그인을 안하면 휴면 처리)
CREATE TABLE SLEEP_USER_T (
    USER_NO        INT          NOT NULL,         -- PK
    ID             VARCHAR(40)  NOT NULL UNIQUE,  -- ID 정규식에 반영
    PW             VARCHAR(64)  NOT NULL,         -- SHA-256 암호화 방식 사용
    NAME           VARCHAR(40),                   -- 이름
    GENDER         VARCHAR(2),                    -- M, F, NO
    EMAIL          VARCHAR(100) NOT NULL UNIQUE,  -- 이메일
    MOBILE         VARCHAR(15),                   -- 하이픈 제외(-) 후 저장
    BIRTHYEAR      VARCHAR(4),                    -- 출생년도(YYYY)
    BIRTHDATE      VARCHAR(4),                    -- 출생월일(MMDD)
    POSTCODE       VARCHAR(5),                    -- 우편번호
    ROAD_ADDRESS   VARCHAR(100),                  -- 도로명주소
    JIBUN_ADDRESS  VARCHAR(100),                  -- 지번주소
    DETAIL_ADDRESS VARCHAR(100),                  -- 상세주소
    EXTRA_ADDRESS  VARCHAR(100),                  -- 참고항목
    AGREECODE      TINYINT       NOT NULL,        -- 동의여부(0:필수, 1:위치, 2:이벤트, 3:위치+이벤트)
    JOINED_AT      DATETIME,                      -- 가입일
    PW_MODIFIED_AT DATETIME,                      -- 비밀번호변경일
    AUTOLOGIN_ID   VARCHAR(32),                   -- 자동로그인할 때 사용하는 ID(SESSION_ID를 사용함)
    AUTOLOGIN_EXPIRED_AT DATETIME,                -- 자동로그인 만료일
    SLEPT_AT       DATETIME,                      -- 휴면일
    CONSTRAINT PK_SLEEP_USER PRIMARY KEY(USER_NO)
);

-- user1/1111, user2/2222, user3/3333
INSERT INTO USER_T(ID, PW, NAME, GENDER, EMAIL, MOBILE, BIRTHYEAR, BIRTHDATE, POSTCODE, ROAD_ADDRESS, JIBUN_ADDRESS, DETAIL_ADDRESS, EXTRA_ADDRESS, AGREECODE, JOINED_AT, PW_MODIFIED_AT, AUTOLOGIN_ID, AUTOLOGIN_EXPIRED_AT) VALUES('user1', ' FFE1ABD1A 8215353C233D6E0 9613E95EEC4253832A761AF28FF37AC5A15 C', '고길동', 'M', 'gildong@naver.com', '01011111111', '2000', '0101', '12345', '서울시', '서울시', '가산동', '가산동', 0, STR_TO_DATE('20220101', '%Y%m%d'), NULL, NULL, NULL);
INSERT INTO USER_T(ID, PW, NAME, GENDER, EMAIL, MOBILE, BIRTHYEAR, BIRTHDATE, POSTCODE, ROAD_ADDRESS, JIBUN_ADDRESS, DETAIL_ADDRESS, EXTRA_ADDRESS, AGREECODE, JOINED_AT, PW_MODIFIED_AT, AUTOLOGIN_ID, AUTOLOGIN_EXPIRED_AT) VALUES('user2', 'EDEE29F882543B956620B26D EE0E7E950399B1C4222F5DE 5E06425B4C995E9', '홍길순', 'F', 'gilsoon@naver.com', '01022222222', '2000', '0101', '12345', '서울시', '서울시', '가산동', '가산동', 0, STR_TO_DATE('20220101', '%Y%m%d'), NULL, NULL, NULL);
INSERT INTO USER_T(ID, PW, NAME, GENDER, EMAIL, MOBILE, BIRTHYEAR, BIRTHDATE, POSTCODE, ROAD_ADDRESS, JIBUN_ADDRESS, DETAIL_ADDRESS, EXTRA_ADDRESS, AGREECODE, JOINED_AT, PW_MODIFIED_AT, AUTOLOGIN_ID, AUTOLOGIN_EXPIRED_AT) VALUES('user3', '318AEE3FED8C9D 4 D35A7FC1FA776FB31303833AA2DE885354DDF3D44D8FB69', '최자두', 'F', 'jadoo@naver.com', '01033333333', '2000', '0101', '12345', '서울시', '서울시', '가산동', '가산동', 0, STR_TO_DATE('20220101', '%Y%m%d'), NULL, NULL, NULL);

INSERT INTO USER_ACCESS_T VALUES('user1', STR_TO_DATE('20230501', '%Y%m%d'));  -- user1 정상 회원
INSERT INTO USER_ACCESS_T VALUES('user2', STR_TO_DATE('20220501', '%Y%m%d'));  -- user2 휴면 대상(12개월 이상 로그인 이력 없음)
-- user3 휴면 대상(12개월 이상 로그인 이력이 아예 없음)

COMMIT;


-- 간편 가입 서비스를 하고 싶은 사람들은 일단 서비스API문서를 먼저 봐야한다. 그래야 뭐가 더 필요한 지 알 수 있다. 지금 있는 정보로 네이버 로그인은 가능할 듯?






