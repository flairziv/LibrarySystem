package julyjolly.librarysystem.mapper;

import julyjolly.librarysystem.pojo.dto.query.extend.BookRssHistoryQueryDto;
import julyjolly.librarysystem.pojo.entity.BookRssHistory;
import julyjolly.librarysystem.pojo.vo.BookRssHistoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 书籍订阅历史持久化接口
 */
@Mapper
public interface BookRssHistoryMapper {

    void save(BookRssHistory bookRssHistory);

    void update(BookRssHistory bookRssHistory);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<BookRssHistoryVO> query(BookRssHistoryQueryDto dto);

    Integer queryCount(BookRssHistoryQueryDto dto);

}
