package cn.silver.framework.system.service.impl;

import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.system.domain.SysNotice;
import cn.silver.framework.system.mapper.SysNoticeMapper;
import cn.silver.framework.system.service.ISysNoticeService;
import org.springframework.stereotype.Service;

/**
 * 公告 服务层实现
 *
 * @author hb
 */
@Service
public class SysNoticeServiceImpl extends BaseServiceImpl<SysNoticeMapper, SysNotice> implements ISysNoticeService {


//    /**
//     * 查询公告信息
//     *
//     * @param noticeId 公告ID
//     * @return 公告信息
//     */
//    @Override
//    public SysNotice selectNoticeById(String noticeId) {
//        return noticeMapper.selectNoticeById(noticeId);
//    }
//
//    /**
//     * 查询公告列表
//     *
//     * @param notice 公告信息
//     * @return 公告集合
//     */
//    @Override
//    public List<SysNotice> selectNoticeList(SysNotice notice) {
//        return noticeMapper.selectNoticeList(notice);
//    }
//
//    /**
//     * 新增公告
//     *
//     * @param notice 公告信息
//     * @return 结果
//     */
//    @Override
//    public int insertNotice(SysNotice notice) {
//        return noticeMapper.insertNotice(notice);
//    }
//
//    /**
//     * 修改公告
//     *
//     * @param notice 公告信息
//     * @return 结果
//     */
//    @Override
//    public int updateNotice(SysNotice notice) {
//        return noticeMapper.updateNotice(notice);
//    }
//
//    /**
//     * 删除公告对象
//     *
//     * @param noticeId 公告ID
//     * @return 结果
//     */
//    @Override
//    public int deleteNoticeById(String noticeId) {
//        return noticeMapper.deleteNoticeById(noticeId);
//    }
//
//    /**
//     * 批量删除公告信息
//     *
//     * @param noticeIds 需要删除的公告ID
//     * @return 结果
//     */
//    @Override
//    public int deleteNoticeByIds(String[] noticeIds) {
//        return noticeMapper.deleteNoticeByIds(noticeIds);
//    }
}
