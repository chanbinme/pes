# 인사평가시스템 (Performance Evaluation System)


## 목차

- [프로젝트 소개](#프로젝트-소개)
- [주요 기능](#주요-기능)
- [개선 사항](#개선-사항)
- [기술 스택](#기술-스택)
- [설치 및 실행](#설치-및-실행) 
- [연락처](#연락처)
<br>

## 프로젝트 소개

기업에 재직 당시 사내용 업무 평가 시스템을 개발해야 할 수도 있어서, 이를 연습하기 위해 만든 프로젝트입니다.<br>
부문장과 대표가 팀의 성과를 평가하고, 평가 결과로 인해 팀장과 부문장의 총 평가 점수가 결정됩니다.<br>
<br>
웹 퍼블리싱을 제외한 DB 설계부터 구현까지 전부 혼자 진행하였습니다.<br>
사내 기술에 익숙해지기 위해 Mybatis, JSP를 사용하였고 JPA로 마이그레이션하는 것을 목표로 하고 있습니다.<br>
초기 개발 버전은 회사 정보가 포함되어 있어 Github에 업로드하지 않았습니다.
<br>
<br>

## 주요 기능

- 팀 성과 평가
- 업무 평가 결과에 따른 팀장, 부문장 총 평가 점수 결정
- 업무의 담당 팀장, 부문장 지정
- 평가 결과 엑셀 다운로드
- 팀장, 부문장 평가 결과 확인
<br>

## 개선 사항

- 테스트 코드 작성
- 로그 설정 개선
- 예외 처리 개선
- Spring Security를 포함하여 인증 처리 방식 개선
- Mybatis -> JPA 마이그레이션
<br>

## 기술 스택
- 백엔드: Spring Boot, Mybatis, JSP, H2
- 인증: Session
- 기타: Lombok, Gradle, Jstree
<br>

## 설치 및 실행

```bash
git clone https://github.com/chanbinme/Performance-evaluation-System.git
cd yourproject
./gradlew build
./gradlew bootRun
```
<br>

## 연락처

- Email: chanbin.backend@gmail.com
- GitHub: [chanbinme](https://github.com/chanbinme)
