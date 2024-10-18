# Java 8을 사용하는 Docker 이미지를 만들기 위한 설정 파일
FROM tomcat:8.5.69-jdk8-openjdk

# Tomcat 이미지의 기본 애플리케이션 삭제
RUN rm -rf /usr/local/tomcat/webapps/*

# 변수를 통해 jar파일을 컨테이너에 복사
COPY build/libs/*.war /usr/local/tomcat/webapps/ROOT.war

# 도커에게 컨테이너가 8080 포트를 외부에 노출할 것이라고 알려주는 명령어
EXPOSE 8080

# 컨테이너가 시작되면서 톰캣을 실행할 명령어
CMD ["catalina.sh", "run"]