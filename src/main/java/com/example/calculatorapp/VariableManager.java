package com.example.calculatorapp;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;



public class VariableManager {
    private Map<Character, String> variables = new HashMap<>();

    public void saveVariable(String variable, String value) {
        variables.put(variable.charAt(0), value);
    }

    public void loadVariable(String variable, Stack<String> stack) {
        if (!variables.containsKey(variable.charAt(0))) {
            throw new IllegalArgumentException("La variabile " + variable + " non è stata definita.");
        }
        stack.push(variables.get(variable.charAt(0)));
    }

    public String getVariableValue(String variable) {
        return variable;
    }
}
