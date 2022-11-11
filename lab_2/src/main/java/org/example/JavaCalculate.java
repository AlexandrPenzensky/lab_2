package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Александр Пензенский
 */
public class JavaCalculate {

    /**
     * Используемые математические операции:
     * 1) op_mult – операция умножения '*',
     *    op_div – операция делеания '/';
     * 2) plus – операция сложения '+',
     *    minus – операция вычитания '-';
     * 3) l_bracket – левая скобка '(',
     *    r_bracket – правая скобка ')';
     * 4) number – число;
     * 5) END – конец выражения.
     */
    public enum CharType {
        op_mult, op_div,
        plus, minus,
        l_bracket, r_bracket,
        number,
        END
    }

    /**
     * Класс, описывающий предсталение отдельного элемента
     */
    public static class Element {
        CharType type;    // Представление элемента в CharType
        String value;     // Представление элемента в самом выражении

        public Element(CharType type, String value) {
            this.type = type;
            this.value = value;
        }

        public Element(CharType type, Character value) {
            this.type = type;
            this.value = value.toString();
        }
    }

    /**
     * В классе описаны методы для работы с полученным на вход выражением:
     * 1) next() – переход к следующему элементу;
     * 2) back() – возвращение к предыдущему элементу;
     * 3) getPosition() – получение позиции элемента.
     */
    public static class ElementBuffer {
        private int position;

        public List<Element> elementsExp;

        public ElementBuffer(List<Element> elementsExp) {
            this.elementsExp = elementsExp;
        }

        public Element next() {
            return elementsExp.get(position++);
        }

        public void back() {
            position--;
        }

        public int getPosition() {
            return position;
        }
    }

    /**
     * Метод описывает анализ полученого на вход выражения (разбиение его на отдельные значащие элементы).
     * @param expression – в данный параметр передается выражение, которое мы разбиваем на подвыражения.
     * @return на выход получаем массив отдельных значащих элементов.
     */
    public static List<Element> expressionAnalyze(String expression) {
        ArrayList<Element> elementsExp = new ArrayList<>();
        int indexElement = 0;
        // Проходим по всему выражению с самого начала
        while (indexElement < expression.length()) {
            char symbol = expression.charAt(indexElement);
            switch (symbol) {
                // Проверка на оператор
                case '(':
                    elementsExp.add(new Element(CharType.l_bracket, symbol));
                    indexElement++;
                    continue;
                case ')':
                    elementsExp.add(new Element(CharType.r_bracket, symbol));
                    indexElement++;
                    continue;
                case '*':
                    elementsExp.add(new Element(CharType.op_mult, symbol));
                    indexElement++;
                    continue;
                case '/':
                    elementsExp.add(new Element(CharType.op_div, symbol));
                    indexElement++;
                    continue;
                case '+':
                    elementsExp.add(new Element(CharType.plus, symbol));
                    indexElement++;
                    continue;
                case '-':
                    elementsExp.add(new Element(CharType.minus, symbol));
                    indexElement++;
                    continue;
                default:
                    // Проверка на цифру
                    if (symbol >= '0' && symbol <= '9') {
                        // Создается для проверки многозначных чисел
                        StringBuilder builder = new StringBuilder();
                        do {
                            builder.append(symbol);
                            indexElement++;
                            if (indexElement >= expression.length()) {
                                break;
                            }
                            symbol = expression.charAt(indexElement);
                        } while (symbol >= '0' &&  symbol <= '9');
                        // Добавление считанного из выражения числа в массив элементов
                        elementsExp.add(new Element(CharType.number, builder.toString()));
                    }
                    else {
                        // Проверка на пробел
                        if (symbol != ' ') {
                            throw new RuntimeException("Неизвестный символ: <<" + symbol + ">>");
                        }
                        indexElement++;
                    }
            }
        }
        // После анализа всего выражения добавляем элемент конца строки
        elementsExp.add(new Element(CharType.END, ""));
        return elementsExp;
    }


    /**
     * Логику вычисления выражения можно описать по форме Бэкуса-Наура:
     * (1) plusmines ::= (multidiv + plusminus) | (multidiv - plusminus) | multidiv
     * – состоит из сложения, вычитания или (2)
     * (2) multidiv ::= (F * multidiv) | (F / multidiv) | F
     * – состоит из умножения, деления или (3)
     * (3) F ::= number | plusminus
     * – состоит из числа или (1)
     */
    public static int calculate(ElementBuffer elementsExp) {
        Element element = elementsExp.next();
        if (element.type == CharType.END) {
            return 0;
        } else {
            elementsExp.back();
            return plusminus(elementsExp);
        }
    }

    /**
     * @param elementsExp выражение разбитое на отдельные элементы.
     * @return – вычисленное выражение
     */
    public static int plusminus(ElementBuffer elementsExp) {
        int value = multdiv(elementsExp);
        while (true) {
            Element element = elementsExp.next();
            switch (element.type) {
                case plus:
                    value += multdiv(elementsExp);
                    break;
                case minus:
                    value -= multdiv(elementsExp);
                    break;
                case r_bracket:
                case END:
                    elementsExp.back();
                    return value;
                default:
                    throw new RuntimeException("Неизвестный знак: " + element.value + " на позиции: " + elementsExp.getPosition());
            }
        }
    }

    /**
     * @param elementsExp выражение разбитое на отдельные элементы.
     * @return – вычисленное выражение
     */
    public static int multdiv(ElementBuffer elementsExp) {
        // Расчет выражения в скобках
        int value = func(elementsExp);
        while (true) {
            Element element = elementsExp.next();
            switch (element.type) {
                case op_mult:
                    value *= func(elementsExp);
                    break;
                case op_div:
                    value /= func(elementsExp);
                    break;
                case r_bracket:
                case plus:
                case minus:
                case END:
                    elementsExp.back();
                    return value;
                default:
                    throw new RuntimeException("Неизвестный знак: " + element.value + " на позиции: " + elementsExp.getPosition());
            }
        }
    }

    /**
     * @param elementsExp выражение разбитое на отдельные элементы.
     * @return – вычисленное выражение
     */
    public static int func(ElementBuffer elementsExp) {
        Element element = elementsExp.next();
        switch (element.type) {
            case number:
                return Integer.parseInt(element.value);
            case l_bracket:
                // Расчет выражений в скобках
                int value = calculate(elementsExp);
                element = elementsExp.next();
                // Проверка на правую скобку
                if (element.type != CharType.r_bracket) {
                    throw new RuntimeException("Неизвестный знак: << " + element.value + " >> на позиции: " + elementsExp.getPosition());
                }
                return value;
            default:
                throw new RuntimeException("Неизвестный знак: << " + element.value + " >> на позиции: " + elementsExp.getPosition());
        }
    }
}

