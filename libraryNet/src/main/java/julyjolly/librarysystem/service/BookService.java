package julyjolly.librarysystem.service;

import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.BookQueryDto;
import julyjolly.librarysystem.pojo.entity.Book;
import julyjolly.librarysystem.pojo.vo.BookVO;
import julyjolly.librarysystem.pojo.vo.ChartVO;

import java.util.List;

/**
 * 书籍业务逻辑接口
 */
public interface BookService {

    Result<Void> save(Book book);

    Result<Void> batchDelete(List<Integer> ids);

    Result<Void> update(Book book);

    Result<List<BookVO>> query(BookQueryDto bookQueryDto);

    Result<List<ChartVO>> daysQuery(Integer day);

}
