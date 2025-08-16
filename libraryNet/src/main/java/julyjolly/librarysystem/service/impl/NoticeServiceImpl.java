package julyjolly.librarysystem.service.impl;

import julyjolly.librarysystem.mapper.NoticeMapper;
import julyjolly.librarysystem.pojo.api.ApiResult;
import julyjolly.librarysystem.pojo.api.PageResult;
import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.NoticeQueryDto;
import julyjolly.librarysystem.pojo.entity.Notice;
import julyjolly.librarysystem.service.NoticeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告业务逻辑实现
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeMapper noticeMapper;

    /**
     * 公告新增
     *
     * @param notice 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(Notice notice) {
        notice.setCreateTime(LocalDateTime.now());
        noticeMapper.save(notice);
        return ApiResult.success();
    }

    /**
     * 公告删除
     *
     * @param ids 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        noticeMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 公告修改
     *
     * @param notice 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> update(Notice notice) {
        System.out.println(notice);
        noticeMapper.update(notice);
        return ApiResult.success();
    }

    /**
     * 公告查询
     *
     * @param noticeQueryDto 查询参数
     * @return Result<List < Notice>>
     */
    @Override
    public Result<List<Notice>> query(NoticeQueryDto noticeQueryDto) {
        List<Notice> noticeList = noticeMapper.query(noticeQueryDto);
        Integer totalCount = noticeMapper.queryCount(noticeQueryDto);
        return PageResult.success(noticeList, totalCount);
    }


}
