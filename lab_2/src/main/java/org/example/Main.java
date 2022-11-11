package org.example;

// Разбор выражения и вычисление его значения.
// Выражение может содержать числа, знаки операций, скобки.
// В случае, если выражение записано корректно, вычислить значение, в противном случае — вывести сообщение об ошибке.
// Дополнительно приветствуется поддержка имен переменных и различных функций.
// В случае, если есть переменные, их значения нужно запросить у пользователя (для каждой из них — по одному разу).

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        System.out.println("Введите выражение: ");
        String expression = console.nextLine();
        List<JavaCalculate.Element> elements = JavaCalculate.expressionAnalyze(expression);
        JavaCalculate.ElementBuffer elementBuffer = new JavaCalculate.ElementBuffer(elements);
        System.out.println("Результат выражения: ");
        System.out.println(JavaCalculate.calculate(elementBuffer));
    }
}