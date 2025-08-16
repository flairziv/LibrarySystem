package julyjolly.librarysystem.service.impl;

import julyjolly.librarysystem.mapper.CategoryMapper;
import julyjolly.librarysystem.pojo.api.ApiResult;
import julyjolly.librarysystem.pojo.api.PageResult;
import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.CategoryQueryDto;
import julyjolly.librarysystem.pojo.entity.Category;
import julyjolly.librarysystem.service.CategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 书籍类别业务逻辑实现
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 书籍类别新增
     *
     * @param category 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(Category category) {
        categoryMapper.save(category);
        return ApiResult.success();
    }

    /**
     * 书籍类别删除
     *
     * @param ids 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        categoryMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 书籍类别修改
     *
     * @param category 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> update(Category category) {
        categoryMapper.update(category);
        return ApiResult.success();
    }

    /**
     * 书籍类别查询
     *
     * @param categoryQueryDto 查询参数
     * @return Result<List < Category>>
     */
    @Override
    public Result<List<Category>> query(CategoryQueryDto categoryQueryDto) {
        List<Category> noticeList = categoryMapper.query(categoryQueryDto);
        Integer totalCount = categoryMapper.queryCount(categoryQueryDto);
        return PageResult.success(noticeList, totalCount);
    }


}
