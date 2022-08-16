package cn.silver.framework.core.api;

import cn.silver.framework.core.model.ApproveModel;

import java.util.Collection;

public interface IBussApi {

    void approveFlow(String tableName, ApproveModel model);

    void delete(String tableName, String id);

    void deleteBatch(String tableName, Collection<String> ids);
}
