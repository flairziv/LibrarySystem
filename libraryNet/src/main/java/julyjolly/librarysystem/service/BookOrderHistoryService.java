package julyjolly.librarysystem.service;

import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.BookOrderHistoryQueryDto;
import julyjolly.librarysystem.pojo.entity.BookOrderHistory;
import julyjolly.librarysystem.pojo.vo.BookOrderHistoryVO;

import java.util.List;

/**
 * 书籍预约历史业务逻辑接口
 */
public interface BookOrderHistoryService {

    Result<Void> save(BookOrderHistory BookOrderHistory);

    Result<Void> batchDelete(List<Integer> ids);

    Result<Void> update(BookOrderHistory BookOrderHistory);

    Result<List<BookOrderHistoryVO>> query(BookOrderHistoryQueryDto dto);

}
