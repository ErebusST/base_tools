/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Stack;

/**
 * @author 司徒彬
 * @date 2022/2/25 17:45
 */
@Slf4j
public class CalculatorUtils {
    private final Stack<String> postfixStack = new Stack<>();// 后缀式栈
    private final int[] operationPriority = new int[]{0, 3, 2, 1, -1, 1, 0, 2};// 运用运算符ASCII码-40做索引的运算符优先级

    @Test
    public void test(){
        String string = "1*8.23881471291217e-05";
        double conversion = CalculatorUtils.conversion(string);
        log.info("{}",BigDecimal.valueOf(conversion));
    }
    /**
     * 计算表达式的值
     *
     * @param expression 表达式
     * @return 计算结果
     */
    public static double conversion(String expression) {
        return (new CalculatorUtils()).calculate(expression);
    }

    /**
     * 按照给定的表达式计算
     *
     * @param expression 要计算的表达式例如:5+12*(3+5)/7
     * @return 计算结果
     */
    private double calculate(String expression) {
        prepare(transform(expression));

        Stack<String> resultStack = new Stack<>();
        Collections.reverse(postfixStack);// 将后缀式栈反转
        String firstValue, secondValue, currentOp;// 参与计算的第一个值，第二个值和算术运算符
        while (false == postfixStack.isEmpty()) {
            currentOp = postfixStack.pop();
            if (false == isOperator(currentOp.charAt(0))) {// 如果不是运算符则存入操作数栈中
                currentOp = currentOp.replace("~", "-");
                resultStack.push(currentOp);
            } else {// 如果是运算符则从操作数栈中取两个值和该数值一起参与运算
                secondValue = resultStack.pop();
                firstValue = resultStack.pop();

                // 将负数标记符改为负号
                firstValue = firstValue.replace("~", "-");
                secondValue = secondValue.replace("~", "-");

                BigDecimal tempResult = calculate(firstValue, secondValue, currentOp.charAt(0));
                resultStack.push(tempResult.toString());
            }
        }
        return Double.parseDouble(resultStack.pop());
    }

    /**
     * 数据准备阶段将表达式转换成为后缀式栈
     *
     * @param expression 表达式
     */
    private void prepare(String expression) {
        final Stack<Character> opStack = new Stack<>();
        opStack.push(',');// 运算符放入栈底元素逗号，此符号优先级最低
        char[] arr = expression.toCharArray();
        int currentIndex = 0;// 当前字符的位置
        int count = 0;// 上次算术运算符到本次算术运算符的字符的长度便于或者之间的数值
        char currentOp, peekOp;// 当前操作符和栈顶操作符
        for (int i = 0; i < arr.length; i++) {
            currentOp = arr[i];
            if (isOperator(currentOp)) {// 如果当前字符是运算符
                if (count > 0) {
                    postfixStack.push(new String(arr, currentIndex, count));// 取两个运算符之间的数字
                }
                peekOp = opStack.peek();
                if (currentOp == ')') {// 遇到反括号则将运算符栈中的元素移除到后缀式栈中直到遇到左括号
                    while (opStack.peek() != '(') {
                        postfixStack.push(String.valueOf(opStack.pop()));
                    }
                    opStack.pop();
                } else {
                    while (currentOp != '(' && peekOp != ',' && compare(currentOp, peekOp)) {
                        postfixStack.push(String.valueOf(opStack.pop()));
                        peekOp = opStack.peek();
                    }
                    opStack.push(currentOp);
                }
                count = 0;
                currentIndex = i + 1;
            } else {
                count++;
            }
        }
        if (count > 1 || (count == 1 && !isOperator(arr[currentIndex]))) {// 最后一个字符不是括号或者其他运算符的则加入后缀式栈中
            postfixStack.push(new String(arr, currentIndex, count));
        }

        while (opStack.peek() != ',') {
            postfixStack.push(String.valueOf(opStack.pop()));// 将操作符栈中的剩余的元素添加到后缀式栈中
        }
    }

    /**
     * 判断是否为算术符号
     *
     * @param c 字符
     * @return 是否为算术符号
     */
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')' || c == '%';
    }

    /**
     * 利用ASCII码-40做下标去算术符号优先级
     *
     * @param cur  下标
     * @param peek peek
     * @return 优先级，如果cur高或相等，返回true，否则false
     */
    private boolean compare(char cur, char peek) {// 如果是peek优先级高于cur，返回true，默认都是peek优先级要低
        final int offset = 40;
        if (cur == '%') {
            // %优先级最高
            cur = 47;
        }
        if (peek == '%') {
            // %优先级最高
            peek = 47;
        }

        return operationPriority[peek - offset] >= operationPriority[cur - offset];
    }

    /**
     * 按照给定的算术运算符做计算
     *
     * @param firstValue  第一个值
     * @param secondValue 第二个值
     * @param currentOp   算数符，只支持'+'、'-'、'*'、'/'、'%'
     * @return 结果
     */
    private BigDecimal calculate(String firstValue, String secondValue, char currentOp) {
        BigDecimal result;
        switch (currentOp) {
            case '+':
                result = NumberUtils.add(firstValue, secondValue);
                break;
            case '-':
                result = NumberUtils.subtract(firstValue, secondValue);
                break;
            case '*':
                result = NumberUtils.multiply(firstValue, secondValue);
                break;
            case '/':
                result = NumberUtils.divide(firstValue, secondValue);
                break;
            case '%':
                result = NumberUtils.remainder(firstValue, secondValue);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentOp);
        }
        return result;
    }

    /**
     * 将表达式中负数的符号更改
     *
     * @param expression 例如-2+-1*(-3E-2)-(-1) 被转为 ~2+~1*(~3E~2)-(~1)
     * @return 转换后的字符串
     */
    private static String transform(String expression) {
        expression = StringUtils.cleanBlank(expression);
        expression = StringUtils.removeSuffix(expression, "=");
        final char[] arr = expression.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == '-') {
                if (i == 0) {
                    arr[i] = '~';
                } else {
                    char c = arr[i - 1];
                    if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == 'E' || c == 'e') {
                        arr[i] = '~';
                    }
                }
            }
        }
        if (arr[0] == '~' && (arr.length > 1 && arr[1] == '(')) {
            arr[0] = '-';
            return "0" + new String(arr);
        } else {
            return new String(arr);
        }
    }
}