package co.pes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * src/main/java : 패키지와 클래스 파일
 * src/main/resource
 *  -PES : Performance Evaluation System (인사평가시스템)
 *  -static : class/js 파일
 *  -templates : html파일(표준)
 *  -application.yml : 환경설정 파일
 */
@SpringBootApplication
public class PesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PesApplication.class, args);
	}

}
