package julyjolly.librarysystem.service;

import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.BookRssHistoryQueryDto;
import julyjolly.librarysystem.pojo.entity.BookRssHistory;
import julyjolly.librarysystem.pojo.vo.BookRssHistoryVO;

import java.util.List;

/**
 * 书籍订阅业务逻辑接口
 */
public interface BookRssHistoryService {

    Result<Void> save(BookRssHistory bookRssHistory);

    Result<Void> batchDelete(List<Integer> ids);

    Result<Void> update(BookRssHistory bookRssHistory);

    Result<List<BookRssHistoryVO>> query(BookRssHistoryQueryDto dto);

}
