package co.pes.unit.domain.manager.repository;

import static co.pes.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import co.pes.domain.manager.repository.MybatisLoginManagerRepository;
import co.pes.domain.manager.service.dto.LoginDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("test")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
class MybatisLoginManagerRepositoryTest {

    @Autowired
    private MybatisLoginManagerRepository mybatisLoginManagerRepository;

    @Test
    @DisplayName("로그인 시 입력한 아이디와 비밀번호가 일치하는지 확인한다. 일치하면 1, 불일치하면 0을 반환한다.")
    void login() {
        // given
        LoginDto successLoginDto = createDummyLoginDto();
        LoginDto failLoginDto = createDummyFailLoginDto();

        // when
        int successLogin = mybatisLoginManagerRepository.login(successLoginDto);
        int failLogin = mybatisLoginManagerRepository.login(failLoginDto);

        // then
        assertAll(
            () -> assertEquals(1, successLogin),
            () -> assertEquals(0, failLogin)
        );
    }
}