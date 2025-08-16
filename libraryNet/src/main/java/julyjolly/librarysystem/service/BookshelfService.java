package julyjolly.librarysystem.service;

import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.BookshelfQueryDto;
import julyjolly.librarysystem.pojo.entity.BookShelf;

import java.util.List;

/**
 * 书架业务逻辑接口
 */
public interface BookshelfService {

    Result<Void> save(BookShelf bookShelf);

    Result<Void> batchDelete(List<Integer> ids);

    Result<Void> update(BookShelf bookShelf);

    Result<List<BookShelf>> query(BookshelfQueryDto bookshelfQueryDto);

}
