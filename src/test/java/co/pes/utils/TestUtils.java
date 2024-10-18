package co.pes.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author cbkim
 * @PackageName: co.pes.utils
 * @FileName : TestUtils.java
 * @Date : 2024. 10. 18.
 * @프로그램 설명 : 테스트 유틸리티 클래스
 */
public class TestUtils {

    /**
     * JSON 파일을 읽어서 문자열로 반환합니다.
     *
     * @param path JSON 파일 경로
     * @return JSON 파일 문자열
     * @throws IOException
     */
    public static String readJson(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/json/" + path)));
    }
}
