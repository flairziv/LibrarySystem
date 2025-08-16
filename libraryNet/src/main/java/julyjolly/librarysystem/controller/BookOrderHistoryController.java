package julyjolly.librarysystem.controller;

import julyjolly.librarysystem.aop.Log;
import julyjolly.librarysystem.aop.Pager;
import julyjolly.librarysystem.context.LocalThreadHolder;
import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.BookOrderHistoryQueryDto;
import julyjolly.librarysystem.pojo.entity.BookOrderHistory;
import julyjolly.librarysystem.pojo.vo.BookOrderHistoryVO;
import julyjolly.librarysystem.service.BookOrderHistoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 图书借阅历史数据的 Controller
 */
@RestController
@RequestMapping(value = "/bookOrderHistory")
public class BookOrderHistoryController {

    @Resource
    private BookOrderHistoryService bookOrderHistoryService;

    /**
     * 图书借阅历史数据新增
     *
     * @param bookOrderHistory 新增数据
     * @return Result<Void> 通用响应体
     */
    @Log(content = "借阅书籍")
    @PostMapping(value = "/save")
    public Result<Void> save(@RequestBody BookOrderHistory bookOrderHistory) {
        return bookOrderHistoryService.save(bookOrderHistory);
    }

    /**
     * 图书借阅历史数据删除
     *
     * @param ids 要删除的图书借阅历史数据ID列表
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Integer> ids) {
        return bookOrderHistoryService.batchDelete(ids);
    }

    /**
     * 图书借阅历史数据修改
     *
     * @param bookOrderHistory 参数
     * @return Result<Void> 响应
     */
    @Log(content = "归还书籍")
    @PutMapping(value = "/update")
    public Result<Void> update(@RequestBody BookOrderHistory bookOrderHistory) {
        return bookOrderHistoryService.update(bookOrderHistory);
    }

    /**
     * 查询用户全部的书籍预约记录
     *
     * @return Result<List < BookOrderHistoryVO>> 通用响应
     */
    @Pager
    @PostMapping(value = "/queryUser")
    public Result<List<BookOrderHistoryVO>> queryUser(@RequestBody BookOrderHistoryQueryDto dto) {
        dto.setUserId(LocalThreadHolder.getUserId());
        return bookOrderHistoryService.query(dto);
    }

    /**
     * 图书借阅历史数据查询
     *
     * @param dto 查询参数
     * @return Result<List < BookOrderHistoryVO>> 通用响应
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<BookOrderHistoryVO>> query(@RequestBody BookOrderHistoryQueryDto dto) {
        return bookOrderHistoryService.query(dto);
    }

}
