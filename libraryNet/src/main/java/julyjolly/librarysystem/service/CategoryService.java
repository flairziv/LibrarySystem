package julyjolly.librarysystem.service;

import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.CategoryQueryDto;
import julyjolly.librarysystem.pojo.entity.Category;

import java.util.List;

/**
 * 书籍种类业务逻辑接口
 */
public interface CategoryService {

    Result<Void> save(Category category);

    Result<Void> batchDelete(List<Integer> ids);

    Result<Void> update(Category category);

    Result<List<Category>> query(CategoryQueryDto categoryQueryDto);

}
