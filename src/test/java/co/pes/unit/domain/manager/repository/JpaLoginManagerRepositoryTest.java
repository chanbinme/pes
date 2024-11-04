package co.pes.unit.domain.manager.repository;

import static co.pes.utils.TestUtils.createDummyFailLoginDto;
import static co.pes.utils.TestUtils.createDummyLoginDto;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.pes.common.config.QueryDslConfig;
import co.pes.domain.manager.repository.JpaLoginManagerRepository;
import co.pes.domain.manager.service.dto.LoginDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("test")
@DataJpaTest
@Import(QueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
class JpaLoginManagerRepositoryTest {

    @Autowired
    private JpaLoginManagerRepository jpaLoginManagerRepository;

    @Test
    @DisplayName("로그인 시 입력한 아이디와 비밀번호가 일치하는지 확인한다. 일치하면 true, 불일치하면 false을 반환한다.")
    void login() {
        // given
        LoginDto successLoginDto = createDummyLoginDto();
        LoginDto failLoginDto = createDummyFailLoginDto();

        // when
        boolean successLogin = jpaLoginManagerRepository.existsByIdAndPassword(successLoginDto.getId(), successLoginDto.getPassword());
        boolean failLogin = jpaLoginManagerRepository.existsByIdAndPassword(failLoginDto.getId(), failLoginDto.getPassword());

        // then
        assertAll(
            () -> assertTrue(successLogin),
            () -> assertFalse(failLogin)
        );
    }
}
