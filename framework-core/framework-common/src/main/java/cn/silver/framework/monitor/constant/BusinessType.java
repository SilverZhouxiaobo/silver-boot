package cn.silver.framework.monitor.constant;

import cn.silver.framework.core.constant.BaseContant;

/**
 * 业务操作类型
 *
 * @author hb
 */
public enum BusinessType implements BaseContant {
    /**
     *
     */
    ALL("00", "查询全部数据"),

    PAGE("01", "分页列表查询"),

    DETAIL("02", "查看数据详情"),

    TREE("03", "查询树结构数据"),

    ROOT("04", "查询根节点数据"),

    CHILD("05", "查询子节点数据"),

    EXISTS("06", "数据重复性校验"),

    INSERT("11", "新增数据"),

    UPDATE("12", "修改数据"),

    DELETE("13", "删除数据"),

    CLEAN("14", "清空数据"),

    EXPORT("15", "数据导出"),

    IMPORT_TEMPLATE("16", "下载导入模板"),

    IMPORT("17", "导入数据"),

    GRANT("20", "数据授权"),

    GENCODE("30", "代码生成"),

    FORCE("90", "强行登出系统"),

    OTHER("99", "其他操作");
    /**
     * 编码
     */
    private final String code;
    /**
     * 名称
     */
    private final String name;

    BusinessType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }
}
