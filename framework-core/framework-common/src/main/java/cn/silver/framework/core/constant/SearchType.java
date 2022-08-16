package cn.silver.framework.core.constant;

public enum SearchType implements BaseContant {

    /**
     * 等于
     */
    EQ("=", "等于", "EQ"),
    /**
     * 不等于
     */
    NE("&lt;>", "不等于", "NE"),
    /**
     * 包含
     */
    LIKE("like", "包含", "LIKE"),
    /**
     * 大于等于
     */
    GE("&gt;=", "大于等于", "GE"),
    /**
     * 大于
     */
    GT("&gt;", "大于", "GT"),
    /**
     * 小于等于
     */
    LE("&lt;=", "小于等于", "LE"),
    /**
     * 小于
     */
    LT("&lt;", "小于", "LT"),
    /**
     * 范围查询
     */
    IN("in", "范围查询", "IN"),
    /**
     * 时间段
     */
    BETWEEN("between", "时间段", "BETWEEN");
    /**
     * 编码
     */
    private final String code;
    /**
     * 名称
     */
    private final String name;

    private final String expression;

    SearchType(String expression, String name, String code) {
        this.code = code;
        this.name = name;
        this.expression = expression;
    }

    public static SearchType getType(String code) {
        SearchType searchType = null;
        for (SearchType type : SearchType.values()) {
            if (type.getCode().equals(code)) {
                searchType = type;
                break;
            }
        }
        return searchType;
    }

    public static void main(String[] args) {
        SearchType searchType = SearchType.getType("=");
        System.out.println(searchType.code);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    public String getExpression() {
        return expression;
    }
}
