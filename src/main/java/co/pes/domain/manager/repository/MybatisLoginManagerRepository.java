package co.pes.domain.manager.repository;

import co.pes.domain.manager.service.dto.LoginDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MybatisLoginManagerRepository {

    int login(@Param("loginInfo") LoginDto loginDto);

}
