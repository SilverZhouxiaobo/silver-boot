package cn.silver.framework.core.constant;

public enum FileType implements BaseContant {
    STOCK_DATA("01", "股东数据文件"),
    RESEARCH_REPORT("10", "研报"),
    ANNOUNCEMENT("11", "公司公告"),
    ANNUAL_REPORT("12", "企业年报"),
    ESG_REPORT("13", "ESG报告");

    private final String code;
    private final String name;

    FileType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
