package julyjolly.librarysystem.service;

import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.BookSaveQueryDto;
import julyjolly.librarysystem.pojo.entity.BookSave;
import julyjolly.librarysystem.pojo.vo.BookSaveVO;

import java.util.List;

/**
 * 书籍收藏业务逻辑接口
 */
public interface BookSaveService {

    Result<Void> save(BookSave bookSave);

    Result<Void> batchDelete(List<Integer> ids);

    Result<Void> update(BookSave bookSave);

    Result<List<BookSaveVO>> query(BookSaveQueryDto bookSaveQueryDto);

}
