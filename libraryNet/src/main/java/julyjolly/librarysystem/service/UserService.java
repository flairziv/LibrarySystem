package julyjolly.librarysystem.service;

import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.UserQueryDto;
import julyjolly.librarysystem.pojo.dto.update.UserLoginDTO;
import julyjolly.librarysystem.pojo.dto.update.UserRegisterDTO;
import julyjolly.librarysystem.pojo.dto.update.UserUpdateDTO;
import julyjolly.librarysystem.pojo.entity.User;
import julyjolly.librarysystem.pojo.vo.ChartVO;
import julyjolly.librarysystem.pojo.vo.UserVO;

import java.util.List;
import java.util.Map;

/**
 * 用户服务类
 */
public interface UserService {
    Result<String> register(UserRegisterDTO userRegisterDTO);

    Result<Object> login(UserLoginDTO userLoginDTO);

    Result<UserVO> auth();

    Result<List<User>> query(UserQueryDto userQueryDto);

    Result<String> update(UserUpdateDTO userUpdateDTO);

    Result<String> batchDelete(List<Integer> ids);

    Result<String> updatePwd(Map<String, String> map);

    Result<UserVO> getById(Integer id);

    Result<String> insert(UserRegisterDTO userRegisterDTO);

    Result<String> backUpdate(User user);

    Result<List<ChartVO>> daysQuery(Integer day);

}
