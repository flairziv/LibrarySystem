package julyjolly.librarysystem.mapper;

import julyjolly.librarysystem.pojo.dto.query.extend.BookQueryDto;
import julyjolly.librarysystem.pojo.entity.Book;
import julyjolly.librarysystem.pojo.vo.BookVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 书籍持久化接口
 */
@Mapper
public interface BookMapper {

    void save(Book book);

    void update(Book book);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<BookVO> query(BookQueryDto bookQueryDto);

    Integer queryCount(BookQueryDto bookQueryDto);

}
