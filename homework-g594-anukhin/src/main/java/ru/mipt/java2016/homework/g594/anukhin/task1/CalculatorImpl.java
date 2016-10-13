package ru.mipt.java2016.homework.g594.anukhin.task1;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

import java.util.Arrays;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;


/**
 * Created by clumpytuna on 13.10.16.
 */

public class CalculatorImpl implements Calculator {

  @Override
  public double calculate(String expression) throws ParsingException {
    if (expression == null) {
      throw new ParsingException("Null expression");
    }
    System.out.println(expression);
    System.out.println(makePosfixNotation(expression));
    System.out.println();
    return makeOperations(makePosfixNotation(expression));
  }

  private static final Set<Character> SYMBOLS =
          new TreeSet<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'));

  private static final Set<Character> OPERATORS =
          new TreeSet<>(Arrays.asList('+', '-', '*', '/'));

  private int priority(char a) // функция для определения приоритетов операций
    {
      switch(a)
        {
          case '&':
            return 4;
          case '*':
          case '/':
            return 3;

          case '-':
          case '+':
            return 2;

          case '(':
            return 1;
        }
      return 0;
    }

  private Double doOperation(Character operation, Double operand_left, Double operand_right)
          throws ParsingException {
    switch (operation) {
      case '+':
        return operand_left + operand_right;
      case '-':
        return operand_right - operand_left;
      case '*':
        return operand_left * operand_right;
      case '/':
        return operand_right / operand_left;
      default:
        throw new ParsingException("Invalid expression");
    }
  }

  private  Double doUnaryOperation(Character operation, Double operand) throws ParsingException{
    switch (operation){
      case '-':
        return -operand;
      default:
        throw new ParsingException("Invalid expression");
    }
  }



  private String makePosfixNotation(String expression) throws  ParsingException {
    StringBuilder postfixString = new StringBuilder();
    Stack<Character> stack = new Stack<Character>();
    char previos = 'e'; //0 - число // 1 - оператор // 2 - открывающаяся скобка
    char symbol;
    if (expression.length() == 0){
      throw new ParsingException("Expression is empty");
    }
    for (int i = 0; i < expression.length(); ++i) {
      symbol = expression.charAt(i);
      if (symbol == '\t' | symbol == '\n' | symbol == ' '){
        continue;
      }

      if (SYMBOLS.contains(symbol)){
        postfixString.append(symbol);
        previos = 0;
        continue;
      }
      else if (symbol == '(') {
        stack.push(symbol);
        postfixString.append(' ');
        previos = 2;
        continue;
      }
      else if (symbol == ')') {
        boolean isOpens = false;
        while (!stack.empty() && !(stack.lastElement().equals('('))) {
          postfixString.append(' ');
          postfixString.append(stack.pop()).append(' ');
        }
        if (!stack.empty() && stack.lastElement().equals('(')) {
          isOpens = true;
        }
        if(!isOpens && stack.empty()){
          throw new ParsingException("Invalid expression");
        }
        stack.pop();
        previos = 'e';
        continue;
      }
      else if (OPERATORS.contains(symbol)){//встретили оператор

        if (previos == 1 && symbol == '-') {
          postfixString.append(' ').append(' ');
          stack.push('&');
          continue;
        }

        while (!stack.empty() && ( priority(stack.lastElement()) >= priority(symbol))){
          postfixString.append(' ').append(stack.pop()).append(' ');
        }
        if (previos == 2) {
          postfixString.append('0').append(' ');
          if (symbol != '-') {
            throw new ParsingException("Invalid expression");
          }
        }


        postfixString.append(' ');
        stack.push(symbol);
        previos = 1;
      }
      else {
        throw new ParsingException("Invalid symbol");
      }
    }
    while (!stack.empty()) {
      postfixString.append(' ');
      postfixString.append(stack.lastElement()).append(' ');
      stack.pop();
    }
    if (postfixString.toString().contains(")") || postfixString.toString().contains("(") || postfixString.length() == 0){
      System.out.println(postfixString.toString());
      throw new ParsingException("Invalid expression");
    }
    return postfixString.toString();
  }
  private double makeOperations(String expression) throws ParsingException {
    Stack<Double> stack = new Stack<Double>();
    StringBuilder Number = new StringBuilder();
    for (int i = 0; i < expression.length(); ++i) {
      Character symbol = expression.charAt(i);
      if (SYMBOLS.contains(symbol)){
        Number.append(symbol);
        continue;
      } else {
       if (i > 0 && symbol.equals(' ') && SYMBOLS.contains(expression.charAt(i - 1))){
         try {
           stack.push(Double.parseDouble(Number.toString()));
         } catch (NumberFormatException e) {
           throw new ParsingException("Invalid number");
         }
         Number.delete(0, Number.length());
       } else if (OPERATORS.contains(symbol)) {
           if (stack.size() == 1){
             double result = doUnaryOperation(symbol, stack.pop());
             stack.push(result);
           } else {
           if (stack.size() < 2){
             throw new ParsingException("Something going wrong");
           }
           Double left = stack.pop();
           Double right = stack.pop();
           stack.push(doOperation(symbol, left, right));
         }
        } else if(symbol == '&') {
         stack.push(-stack.pop());
       }
      }
    }
    if (stack.size() == 1){
      return stack.lastElement();
    } else {
      throw new ParsingException("Something going wrong");
    }
  }
}