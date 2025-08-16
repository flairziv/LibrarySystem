package julyjolly.librarysystem.controller;

import julyjolly.librarysystem.aop.Pager;
import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.BookQueryDto;
import julyjolly.librarysystem.pojo.entity.Book;
import julyjolly.librarysystem.pojo.vo.BookVO;
import julyjolly.librarysystem.pojo.vo.ChartVO;
import julyjolly.librarysystem.service.BookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 书籍的 Controller
 */
@RestController
@RequestMapping(value = "/book")
public class BookController {

    @Resource
    private BookService bookService;

    /**
     * 书籍新增
     *
     * @param book 新增数据
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/save")
    public Result<Void> save(@RequestBody Book book) {
        return bookService.save(book);
    }

    /**
     * 书籍删除
     *
     * @param ids 要删除的书籍ID列表
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Integer> ids) {
        return bookService.batchDelete(ids);
    }

    /**
     * 书籍修改
     *
     * @param book 参数
     * @return Result<Void> 响应
     */
    @PutMapping(value = "/update")
    public Result<Void> update(@RequestBody Book book) {
        return bookService.update(book);
    }

    /**
     * 书籍查询
     *
     * @param bookQueryDto 查询参数
     * @return Result<List < BookVO>> 通用响应
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<BookVO>> query(@RequestBody BookQueryDto bookQueryDto) {
        return bookService.query(bookQueryDto);
    }

    /**
     * 统计图书存量数据
     *
     * @return Result<List < ChartVO>> 响应结果
     */
    @GetMapping(value = "/daysQuery/{day}")
    @ResponseBody
    public Result<List<ChartVO>> query(@PathVariable Integer day) {
        return bookService.daysQuery(day);
    }


}
