package julyjolly.librarysystem.service;

import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.UserOperationLogQueryDto;
import julyjolly.librarysystem.pojo.entity.UserOperationLog;
import julyjolly.librarysystem.pojo.vo.UserOperationLogVO;

import java.util.List;

/**
 * 用户行为日志的业务逻辑接口
 */
public interface UserOperationLogService {

    Result<Void> save(UserOperationLog userOperationLog);

    Result<Void> batchDelete(List<Integer> ids);

    Result<Void> update(UserOperationLog userOperationLog);

    Result<List<UserOperationLogVO>> query(UserOperationLogQueryDto dto);

}
