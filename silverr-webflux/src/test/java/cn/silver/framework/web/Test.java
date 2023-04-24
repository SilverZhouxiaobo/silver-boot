package cn.silver.framework.web;

import cn.silver.framework.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test {
    public static void main(String[] args) {
        log.info(SecurityUtils.encryptPassword("123456"));
        log.info(SecurityUtils.encryptPassword("123456"));
        log.info(SecurityUtils.encryptPassword("123456"));
        log.info(SecurityUtils.encryptPassword("123456"));
    }
}
