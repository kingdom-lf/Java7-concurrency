package com.lifeng.operator.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 逻辑运算符：或
 * <br/>
 * 前一个表达式为真，后面的表达式跳过不执行
 */
public class LogicalOperatorOr {

    private static Logger logger = LoggerFactory.getLogger(LogicalOperatorOr.class);

    public static void main(String[] args) {
        int a = 10, b = 5;

        if (b / a > 1 || isOK()) {
            logger.info("two expression exectue ..");
        }

        if (a / b > 1 || isOK()) {
            logger.info("2nd expression not execute");
        }
    }

    public static boolean isOK() {
        logger.info("isOK exectue ...");
        return true;
    }

}
