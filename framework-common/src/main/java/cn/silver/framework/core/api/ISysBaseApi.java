package cn.silver.framework.core.api;

import cn.silver.framework.core.bean.LoginLog;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.bean.OperLog;
import cn.silver.framework.core.model.DictModel;
import cn.silver.framework.core.model.ExternalLinkModel;

import java.util.Collection;
import java.util.List;

/**
 * 系统管理通用接口
 *
 * @author Administrator
 */
public interface ISysBaseApi {
    /**
     * 插入登录日志
     *
     * @param logininfor
     */
    void insertLogininfor(LoginLog logininfor);

    /**
     * 插入操作日志
     *
     * @param operLog
     */
    void insertOperlog(OperLog operLog);

    /**
     * 根据字典类型编码查询字典数据
     *
     * @param type
     * @return
     */
    List<DictModel> selectDictByType(String type);

    String selectCategoryByCodes(String area);

    /**
     * 批量添加外链
     *
     * @param models
     */
    void produceLinkBatch(List<ExternalLinkModel> models);

    /**
     * 根据业务编码生成序列号
     *
     * @param businessKey
     * @return
     */
    String getCode(String businessKey);

    String selectTagByNames();

    String getConfigByCode(String s);

    void handle(String name, String id, String handleType, String handleInfo);

    List<LoginUser> selectUserByIds(Collection<String> collect);
}
