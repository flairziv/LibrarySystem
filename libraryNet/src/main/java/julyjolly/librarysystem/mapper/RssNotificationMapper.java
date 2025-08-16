package julyjolly.librarysystem.mapper;

import julyjolly.librarysystem.pojo.dto.query.extend.RssNotificationQueryDto;
import julyjolly.librarysystem.pojo.entity.RssNotification;
import julyjolly.librarysystem.pojo.vo.RssNotificationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订阅通知持久化接口
 */
@Mapper
public interface RssNotificationMapper {

    void save(@Param(value = "list") List<RssNotification> list);

    void update(RssNotification rssNotification);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<RssNotificationVO> query(RssNotificationQueryDto dto);

    List<RssNotificationVO> queryByUserIds(@Param(value = "userIds") List<Integer> userIds);

    Integer queryCount(RssNotificationQueryDto dto);

    void readDeal(@Param(value = "userId") Integer userId);

}
