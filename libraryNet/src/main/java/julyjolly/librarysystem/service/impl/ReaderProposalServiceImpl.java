package julyjolly.librarysystem.service.impl;

import julyjolly.librarysystem.context.LocalThreadHolder;
import julyjolly.librarysystem.mapper.ReaderProposalMapper;
import julyjolly.librarysystem.mapper.UserMapper;
import julyjolly.librarysystem.pojo.api.ApiResult;
import julyjolly.librarysystem.pojo.api.PageResult;
import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.ReaderProposalQueryDto;
import julyjolly.librarysystem.pojo.dto.query.extend.UserQueryDto;
import julyjolly.librarysystem.pojo.entity.ReaderProposal;
import julyjolly.librarysystem.pojo.entity.User;
import julyjolly.librarysystem.pojo.vo.ReaderProposalVO;
import julyjolly.librarysystem.service.ReaderProposalService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 读者建议业务逻辑实现
 */
@Service
public class ReaderProposalServiceImpl implements ReaderProposalService {

    @Resource
    private ReaderProposalMapper readerProposalMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * 读者建议新增
     *
     * @param readerProposal 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(ReaderProposal readerProposal) {
        // 如果用户已经被禁言，不能留言
        UserQueryDto userQueryDto = new UserQueryDto();
        userQueryDto.setId(LocalThreadHolder.getUserId());
        List<User> userList = userMapper.query(userQueryDto);
        User user = userList.get(0);
        if (user.getIsWord()) {
            return ApiResult.error("账号已被禁言，请联系管理员");
        }
        readerProposal.setUserId(LocalThreadHolder.getUserId());
        readerProposal.setIsReply(false);
        readerProposal.setCreateTime(LocalDateTime.now());
        readerProposalMapper.save(readerProposal);
        return ApiResult.success();
    }

    /**
     * 读者建议删除
     *
     * @param ids 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        readerProposalMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 读者建议修改
     *
     * @param readerProposal 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> update(ReaderProposal readerProposal) {
        // 证明管理员回复
        if (readerProposal.getReplyContent() != null) {
            readerProposal.setReplyTime(LocalDateTime.now());
        }
        readerProposal.setIsReply(true);
        readerProposalMapper.update(readerProposal);
        return ApiResult.success();
    }

    /**
     * 读者建议查询
     *
     * @param readerProposalQueryDto 查询参数
     * @return Result<List < ReadingProposalVO>>
     */
    @Override
    public Result<List<ReaderProposalVO>> query(ReaderProposalQueryDto readerProposalQueryDto) {
        List<ReaderProposalVO> noticeList = readerProposalMapper.query(readerProposalQueryDto);
        Integer totalCount = readerProposalMapper.queryCount(readerProposalQueryDto);
        return PageResult.success(noticeList, totalCount);
    }
}
