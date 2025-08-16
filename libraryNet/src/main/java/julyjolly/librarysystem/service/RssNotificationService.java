package julyjolly.librarysystem.service;

import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.RssNotificationQueryDto;
import julyjolly.librarysystem.pojo.entity.RssNotification;
import julyjolly.librarysystem.pojo.vo.RssNotificationVO;

import java.util.List;

/**
 * 订阅通知的业务逻辑接口
 */
public interface RssNotificationService {

    Result<Void> save(RssNotification rssNotification);

    Result<Void> batchDelete(List<Integer> ids);

    Result<Void> update(RssNotification rssNotification);

    Result<List<RssNotificationVO>> query(RssNotificationQueryDto rssNotificationQueryDto);

    Result<Void> postBook(Integer id);

    Result<List<Void>> readDeal();



}
