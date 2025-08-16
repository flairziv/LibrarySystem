package julyjolly.librarysystem.service.impl;

import julyjolly.librarysystem.mapper.UserOperationLogMapper;
import julyjolly.librarysystem.pojo.api.ApiResult;
import julyjolly.librarysystem.pojo.api.PageResult;
import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.UserOperationLogQueryDto;
import julyjolly.librarysystem.pojo.entity.UserOperationLog;
import julyjolly.librarysystem.pojo.vo.UserOperationLogVO;
import julyjolly.librarysystem.service.UserOperationLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户行为日志业务逻辑实现
 */
@Service
public class UserOperationLogServiceImpl implements UserOperationLogService {

    @Resource
    private UserOperationLogMapper userOperationLogMapper;

    /**
     * 用户行为日志新增
     *
     * @param userOperationLog 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(UserOperationLog userOperationLog) {
        userOperationLog.setCreateTime(LocalDateTime.now());
        List<UserOperationLog> userOperationLogs = new ArrayList<>();
        userOperationLogs.add(userOperationLog);
        userOperationLogMapper.save(userOperationLogs);
        return ApiResult.success();
    }

    /**
     * 用户行为日志删除
     *
     * @param ids 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        userOperationLogMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 用户行为日志修改
     *
     * @param userOperationLog 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> update(UserOperationLog userOperationLog) {
        userOperationLogMapper.update(userOperationLog);
        return ApiResult.success();
    }

    /**
     * 用户行为日志查询
     *
     * @param dto 查询参数
     * @return Result<List < UserOperationLogVO>>
     */
    @Override
    public Result<List<UserOperationLogVO>> query(UserOperationLogQueryDto dto) {
        List<UserOperationLogVO> noticeList = userOperationLogMapper.query(dto);
        Integer totalCount = userOperationLogMapper.queryCount(dto);
        return PageResult.success(noticeList, totalCount);
    }

}
