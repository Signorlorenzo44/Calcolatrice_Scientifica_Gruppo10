package com.example.calculatorapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.commons.math3.complex.Complex;

import javax.swing.*;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

//Il codice inizia con le dichiarazioni del package e le importazioni necessarie per librerie e classi esterne
// come JavaFX e Apache Commons Math.
//Questa classe rappresenta l'applicazione di calcolatrice.
//Override del metodo start che inizializza l'interfaccia utente della calcolatrice utilizzando JavaFX.
//Gestione dell'input dell'utente tramite tastiera o pulsanti dell'interfaccia.
public class CalculatorApp extends Application {
    private StringBuilder inputBuffer = new StringBuilder();
    private TextArea displayArea;
    private ListView<String> stackDisplay;
    private Deque<ComplexNumber> stack = new ArrayDeque<>();
    private ListView<String> resultDisplay;
    private Deque<ComplexNumber> lastTwelveStackElements = new LinkedList<>();
    private static final int MAX_STACK_SIZE = 12;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Calculator");

        GridPane gridPane = createGridPane();
        Scene scene = new Scene(gridPane, 400, 600);
        displayArea = createDisplayArea();
        gridPane.add(displayArea, 0, 0, 4, 1);

        stackDisplay = createStackDisplay();
        gridPane.add(stackDisplay, 0, 1, 4, 1);


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    void handleUserInput(String userInput) {
        try {
            if (isValidComplexNumber(userInput)) {
                ComplexNumber parsedNumber = parseComplexNumber(userInput);
                stack.push(parseComplexNumber(userInput));
                lastTwelveStackElements.addFirst(parsedNumber);
                if (lastTwelveStackElements.size() > 12) {
                    lastTwelveStackElements.removeLast();
                }
                updateStackDisplay();
            } else {
                performOperation(userInput);
            }
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid input");
        }
    }

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: pink;");
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(8);
        gridPane.setHgap(8);

        displayArea = new TextArea();
        displayArea.setEditable(false);
        displayArea.setPrefRowCount(2);
        gridPane.add(displayArea, 0, 0, 4, 1);
        ListView<String> stackDisplay = createStackDisplay();
        gridPane.getChildren().add(stackDisplay);
        stackDisplay = createStackDisplay();
        gridPane.add(stackDisplay, 0, 1, 4, 1);


        String[][] buttonLabels = {{"7", "8", "9", "/"},
                {"4", "5", "6", "*"},
                {"1", "2", "3", "-"},
                {"C", "0", ".", "+"},
                {"", "", "=", ""},
                {"<", ">", "√", "±"},
                {"swap", "drop", "dup", "over"},
                {"a", "b", "c", "d"},
                {"e", "f", "g", "h"},
                {"i", "j", "k", "l"},
                {"m", "n", "o", "p"},
                {"q", "r", "s", "t"},
                {"u", "v", "w", "x"},
                {"y", "z", "", ""}
        };

        for (int i = 0; i < buttonLabels.length; i++) {
            for (int j = 0; j < buttonLabels[i].length; j++) {
                Button button = new Button(buttonLabels[i][j]);
                button.setPrefWidth(50);
                button.setOnAction(e -> handleButtonClick(button.getText()));
                gridPane.add(button, j, i + 3);
            }
        }

        return gridPane;
    }

    //Viene creato un layout GridPane per organizzare i componenti dell'interfaccia.
    //Viene utilizzata una TextArea per visualizzare l'input dell'utente e il risultato delle operazioni.
    //Sono presenti pulsanti per inserire numeri, eseguire operazioni matematiche,
    // manipolare la pila degli operandi, e altro ancora.
    private void handleButtonClick(String buttonValue) {
        switch (buttonValue) {
            case "=":
                double result = calculate(inputBuffer.toString());
                displayArea.setText(String.valueOf(result));
                inputBuffer = new StringBuilder();
                break;
            case "C":
                inputBuffer = new StringBuilder();
                displayArea.clear();
                break;
            case "<":
                displayArea.appendText("<");
                break;
            case ">":
                displayArea.appendText(">");
                break;
            case "√":
                calculateSquareRoot();
                break;
            case "±":
                invertSign();
                break;
            case "swap":
                swapOperands();
                break;
            case "drop":
                dropOperand();
                break;
            case "dup":
                duplicateOperand();
                break;
            case "over":
                overOperands();
                break;
            default:
                inputBuffer.append(buttonValue);
                displayArea.appendText(buttonValue);
                break;

        }
    }

    private void handleKeyEvent(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            handleUserInput(displayArea.getText());
            displayArea.clear();
            updateStackDisplay();
        }
    }

    //I metodi handleUserInput e handleButtonClick gestiscono l'input dell'utente proveniente
    // dalla tastiera e dai pulsanti dell'interfaccia.
    //Vengono eseguite operazioni matematiche sui numeri complessi e gestite eventuali
    // eccezioni di input non valido.
    void performOperation(String operation) {
        ComplexNumber result = null;
        switch (operation) {
            case "+":
                performBinaryOperation(Complex::add);
                break;
            case "-":
                performBinaryOperation(Complex::subtract);
                break;
            case "*":
                performBinaryOperation(Complex::multiply);
                break;
            case "/":
                performBinaryOperation(Complex::divide);
                break;
            case "sqrt":
                performUnaryOperation(Complex::sqrt);
                break;
            case "+-":
                invertSign();
                break;

            default:
                displayArea.setText("Unknown operation");
                return;
        }
        //I metodi performOperation e calculateResult eseguono operazioni come
        // addizione, sottrazione, moltiplicazione, divisione e altre operazioni matematiche su numeri complessi.
        //Gestione della pila degli operandi (stack) e aggiornamento della visualizzazione della pila.


        result = calculateResult(operation);
        if (result != null) {
            lastTwelveStackElements.addFirst(result);
            if (lastTwelveStackElements.size() > 12) {
                lastTwelveStackElements.removeLast();
            }
            updateStackDisplay();
            updateResultDisplay();
        }
    }

    ComplexNumber calculateResult(String operation) {
        ComplexNumber result = null;
        if (!stack.isEmpty()) {
            ComplexNumber num2 = stack.pop();
            ComplexNumber num1 = stack.isEmpty() ? (ComplexNumber) ComplexNumber.ZERO : stack.pop();

            switch (operation) {
                case "+":
                    result = num1.add(num2);
                    break;
                case "-":
                    result = num1.subtract(num2);
                    break;
                case "*":
                    result = num1.multiply(num2);
                    break;
                case "/":
                    result = num1.divide(num2);
                    break;
                case "sqrt":
                    result = (ComplexNumber) num2.sqrt();
                    break;
                case "+-":
                    result = (ComplexNumber) num2.multiply(-1);
                    break;
                default:
                    displayArea.setText("Unknown operation");
                    break;
            }

            if (result != null) {
                stack.push(result);
                lastTwelveStackElements.addFirst(result);
                if (lastTwelveStackElements.size() > 12) {
                    lastTwelveStackElements.removeLast();
                }
                updateStackDisplay();
                updateResultDisplay();
            } else {
                displayArea.setText("Unknown operation");
            }
        } else {
            displayArea.setText("Not enough operands");
        }
        return result;
    }

    void updateStackDisplay() {
        if (_stackDisplay != null) {
            _stackDisplay.getItems().clear(); // Pulisci la visualizzazione dello stack
            for (ComplexNumber number : lastTwelveStackElements) {
                _stackDisplay.getItems().add(number.toString()); // Aggiungi i numeri complessi allo stack display
            }
        }
    }


    boolean isValidComplexNumber(String input) {
        // Implementa la logica per verificare se l'input è un numero complesso valido
        // Ritorna true se è un numero complesso valido, altrimenti false
        String complexNumberPattern = "^[-+]?[0-9]*\\.?[0-9]+[-+][0-9]*\\.?[0-9]*[ij]$";
        return input.matches(complexNumberPattern);
    }

    ComplexNumber parseComplexNumber(String input) {
        // Verifica preliminare se l'input è valido come numero complesso
        if (!isValidComplexNumber(input)) {
            // Restituisci un numero complesso nullo o gestisci l'errore a seconda delle necessità
            return null;
        }

        // Rimuovi spazi bianchi e caratteri non numerici dalla stringa
        String sanitizedInput = input.replaceAll("\\s", "").replaceAll("i", "j");

        // Dividi la stringa in parte reale e immaginaria
        String[] parts = sanitizedInput.split("[+|-]");

        // Gestisci il segno del numero complesso
        double realPart;
        double imaginaryPart;

        if (sanitizedInput.startsWith("-")) {
            realPart = Double.parseDouble(parts[1]);
            imaginaryPart = sanitizedInput.contains("-") ? -Double.parseDouble(parts[2].replace("j", "")) : Double.parseDouble(parts[2].replace("j", ""));
        } else {
            realPart = Double.parseDouble(parts[0]);
            imaginaryPart = sanitizedInput.contains("-") ? -Double.parseDouble(parts[1].replace("j", "")) : Double.parseDouble(parts[1].replace("j", ""));
        }

        // Restituisci il numero complesso
        return new ComplexNumber(realPart, imaginaryPart);
    }


    private void updateResultDisplay() {
        resultDisplay.getItems().clear();
        for (ComplexNumber result : lastTwelveStackElements) {
            resultDisplay.getItems().add(result.toString());
        }
    }

    private ListView<String> createResultDisplay() {
        ListView<String> resultDisplay = new ListView<>();
        resultDisplay.setPrefHeight(50);
        GridPane.setColumnSpan(resultDisplay, 4);
        GridPane.setRowSpan(resultDisplay, 1);
        GridPane.setConstraints(resultDisplay, 0, 3);

        return resultDisplay;
    }

    private void performBinaryOperation(BinaryOperator<Complex> operator) {
        if (stack.size() >= 2) {
            ComplexNumber num2 = stack.pop();
            ComplexNumber num1 = stack.pop();
            stack.push((ComplexNumber) operator.apply(num1, num2));
        } else {
            displayArea.setText("Not enough operands");
        }
    }

    private void performUnaryOperation(UnaryOperator<Complex> operator) {
        if (!stack.isEmpty()) {
            ComplexNumber num = stack.pop();
            stack.push((ComplexNumber) operator.apply(num));
        } else {
            displayArea.setText("Not enough operands");
        }
    }


    private Complex _evaluatePostfix_(List<String> postfix) {
        Stack<Complex> stack = new Stack<>();

        for (String token : postfix) {
            if (Character.isDigit(token.charAt(0))) {
                stack.push(parseComplexNumber(token));
            } else if (token.matches("[a-z]")) {
                // Handle variable operations if needed
            } else {
                if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
                    if (stack.size() >= 2) {
                        Complex operand2 = stack.pop();
                        Complex operand1 = stack.pop();

                        switch (token) {
                            case "+":
                                stack.push(operand1.add(operand2));
                                break;
                            case "-":
                                stack.push(operand1.subtract(operand2));
                                break;
                            case "*":
                                stack.push(operand1.multiply(operand2));
                                break;
                            case "/":
                                stack.push(operand1.divide(operand2));
                                break;
                        }
                    } else {
                        // Handle insufficient operands error
                    }
                }
            }
        }

        if (!stack.isEmpty()) {
            return stack.pop();
        } else {
            // Handle empty stack error
            return null;
        }
    }


    // ... altri metodi rimangono invariati


    List<String> getStackElements() {
        List<String> tokens = Arrays.asList(inputBuffer.toString().split("(?<=[-+*/()\\s])|(?=[-+*/()\\s])"));
        List<String> elements = new ArrayList<>();
        for (String token : tokens) {
            if (!token.isEmpty()) {
                elements.add(token);
            }
        }
        return elements;
    }


    private void compareValues(String comparisonType) {
        try {
            List<String> tokens = Arrays.asList(inputBuffer.toString().split("(?<=[-+/()])|(?=[-+/()])"));
            if (tokens.size() == 3 && Character.isDigit(tokens.get(0).charAt(0)) && tokens.get(1).equals(comparisonType) && Character.isDigit(tokens.get(2).charAt(0))) {
                double operand1 = Double.parseDouble(tokens.get(0));
                double operand2 = Double.parseDouble(tokens.get(2));
                boolean result;
                switch (comparisonType) {
                    case "less":
                        result = operand1 < operand2;
                        break;
                    case "greater":
                        result = operand1 > operand2;
                        break;
                    default:
                        result = false;
                }
                displayArea.setText(String.valueOf(result));
                inputBuffer = new StringBuilder(String.valueOf(result));
            } else {
                displayArea.setText("Error");
                inputBuffer = new StringBuilder();
            }
        } catch (NumberFormatException e) {
            displayArea.setText("Error");
            inputBuffer = new StringBuilder();
        }
    }

    void calculateSquareRoot() {
        try {
            double operand = Double.parseDouble(inputBuffer.toString());
            double result = Math.sqrt(operand);
            displayArea.setText(String.valueOf(result));
            inputBuffer = new StringBuilder(String.valueOf(result));
        } catch (NumberFormatException e) {
            displayArea.setText("Error");
            inputBuffer = new StringBuilder();
        }
    }

    void invertSign() {
        try {
            double operand = Double.parseDouble(inputBuffer.toString());
            double result = -operand;
            displayArea.setText(String.valueOf(result));
            inputBuffer = new StringBuilder(String.valueOf(result));

        } catch (NumberFormatException e) {
            displayArea.setText("Error");
            inputBuffer = new StringBuilder();
        }
    }


    void swapOperands() {
        try {
            List<String> tokens = Arrays.asList(inputBuffer.toString().split("(?<=[-+*/()])|(?=[-+*/()])"));
            if (tokens.size() == 3 && Character.isDigit(tokens.get(0).charAt(0)) && Character.isDigit(tokens.get(2).charAt(0))) {
                Collections.swap(tokens, 0, 2);
                displayArea.setText(String.join("", tokens));
                inputBuffer = new StringBuilder(String.join("", tokens));
            } else {
                displayArea.setText("Error");
                inputBuffer = new StringBuilder();
            }
        } catch (NumberFormatException e) {
            displayArea.setText("Error");
            inputBuffer = new StringBuilder();
        }
    }

    void dropOperand() {
        if ((stack.size() >= 1)) {
            stack.pop();
            updateStackDisplay();
        } else {
            displayArea.setText("Error: Not enough operands");
        }
    }

    void duplicateOperand() {
        if ((stack.size() >= 1)) {
            ComplexNumber topElement = stack.peek();
            stack.push(new ComplexNumber(topElement.getReal(), topElement.getImaginary()));
            updateStackDisplay();
        } else {
            displayArea.setText("Error: Not enough operands");
        }
    }

    public void overOperands() {
        if (stack.size() >= 2) {
            Iterator<ComplexNumber> iterator = stack.iterator();
            ComplexNumber secondLast = null;
            while (iterator.hasNext()) {
                secondLast = iterator.next();
            }
            stack.push(new ComplexNumber(secondLast.getReal(), secondLast.getImaginary()));
            updateStackDisplay();
        } else {
            displayArea.setText("Error: Not enough operands");
        }
    }
    //Metodi per il calcolo della radice quadrata, inversione del segno, manipolazione degli operandi nella
    // pila (swap, drop, dup, over), confronto di valori e conversione in notazione postfissa.

    public double calculate(String input) {
        List<String> tokens = Arrays.asList(input.split("(?<=[-+*/()])|(?=[-+*/()])"));
        List<String> postfix = convertToPostfix(tokens);
        return evaluatePostfix(postfix);
    }

    List<String> convertToPostfix(List<String> tokens) {
        List<String> postfix = new ArrayList<>();
        Stack<String> operatorStack = new Stack<>();

        Map<String, Integer> precedence = Map.of(
                "+", 1,
                "-", 1,
                "*", 2,
                "/", 2
        );

        for (String token : tokens) {
            if (Character.isDigit(token.charAt(0)) || token.matches("[a-z]")) {
                postfix.add(token);
            } else if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    postfix.add(operatorStack.pop());
                }
                operatorStack.pop(); // Remove "("
            } else {
                while (!operatorStack.isEmpty() && precedence.getOrDefault(token, 0) <= precedence.getOrDefault(operatorStack.peek(), 0)) {
                    postfix.add(operatorStack.pop());
                }
                operatorStack.push(token);
            }
        }

        while (!operatorStack.isEmpty()) {
            postfix.add(operatorStack.pop());
        }

        return postfix;
    }

    double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (Character.isDigit(token.charAt(0))) {
                stack.push(Double.parseDouble(token));
            } else if (token.matches("[a-z]")) {
            } else {
                double operand2 = stack.pop();
                double operand1 = stack.pop();
                switch (token) {
                    case "+":
                        stack.push(operand1 + operand2);
                        break;
                    case "-":
                        stack.push(operand1 - operand2);
                        break;
                    case "*":
                        stack.push(operand1 * operand2);
                        break;
                    case "/":
                        stack.push(operand1 / operand2);
                        break;
                }
            }
        }

        return stack.pop();
    }

    private ListView<String> createStackDisplay() {
        ListView<String> stackDisplay = new ListView<>();
        stackDisplay.setPrefHeight(600);
        GridPane.setColumnSpan(stackDisplay, 4);
        GridPane.setRowSpan(stackDisplay, 1);
        GridPane.setConstraints(stackDisplay, 0, 1);

        return stackDisplay;
    }

    private void customizeButton(Button button) {
        button.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14;");
    }

    private void customizeDisplayArea(TextArea displayArea) {
        displayArea.setStyle("-fx-background-color: white; -fx-text-fill: black;");
    }

    private Map<Character, Double> variables = new HashMap<>();

    private void handleVariableOperation(String token) {
        char variableName = token.charAt(0);
        switch (token.charAt(1)) {
            case '>':
                // ">x": Take the top element from the stack and save it into the variable "x"
                if (!variables.containsKey(variableName)) {
                    if (!inputBuffer.toString().isEmpty()) {
                        double value = Double.parseDouble(inputBuffer.toString());
                        variables.put(variableName, value);
                        displayArea.setText(variables.toString());
                    } else {
                        displayArea.setText("Error: No value on stack to save into variable " + variableName);
                    }
                } else {
                    displayArea.setText("Error: Variable " + variableName + " already exists");
                }
                inputBuffer = new StringBuilder();
                break;
            case '<':
                // "<x": Push the value of the variable "x" onto the stack
                if (variables.containsKey(variableName)) {
                    double value = variables.get(variableName);
                    inputBuffer.append(value);
                    displayArea.setText(inputBuffer.toString());
                } else {
                    displayArea.setText("Error: Variable " + variableName + " doesn't exist");
                }
                break;
            case '+':
                // "+x": Take the top element from the stack and add it to the value of the variable "x"
                if (variables.containsKey(variableName)) {
                    if (!inputBuffer.toString().isEmpty()) {
                        double value = Double.parseDouble(inputBuffer.toString());
                        double currentValue = variables.get(variableName);
                        variables.put(variableName, currentValue + value);
                        displayArea.setText(variables.toString());
                    } else {
                        displayArea.setText("Error: No value on stack to add to variable " + variableName);
                    }
                } else {
                    displayArea.setText("Error: Variable " + variableName + " doesn't exist");
                }
                inputBuffer = new StringBuilder();
                break;
            case '-':
                // "-x": Take the top element from the stack and subtract it from the value of the variable "x"
                if (variables.containsKey(variableName)) {
                    if (!inputBuffer.toString().isEmpty()) {
                        double value = Double.parseDouble(inputBuffer.toString());
                        double currentValue = variables.get(variableName);
                        variables.put(variableName, currentValue - value);
                        displayArea.setText(variables.toString());
                    } else {
                        displayArea.setText("Error: No value on stack to subtract from variable " + variableName);
                    }
                } else {
                    displayArea.setText("Error: Variable " + variableName + " doesn't exist");
                }
                inputBuffer = new StringBuilder();
                break;
        }
    }

    private void handleStackOperation(String operation) {
        switch (operation) {
            case "clear":
                // Clear the stack
                inputBuffer = new StringBuilder();
                displayArea.clear();
                break;
            case "drop":
                // Remove the last element
                if (inputBuffer.length() > 0) {
                    inputBuffer.deleteCharAt(inputBuffer.length() - 1);
                    displayArea.setText(inputBuffer.toString());
                } else {
                    displayArea.setText("Error: Stack is empty");
                }
                break;
            case "dup":
                // Push a copy of the last element
                if (inputBuffer.length() > 0) {
                    char lastChar = inputBuffer.charAt(inputBuffer.length() - 1);
                    inputBuffer.append(lastChar);
                    displayArea.setText(inputBuffer.toString());
                } else {
                    displayArea.setText("Error: Stack is empty");
                }
                break;
            case "swap":
                // Exchange the last two elements
                if (inputBuffer.length() >= 2) {
                    char lastChar = inputBuffer.charAt(inputBuffer.length() - 1);
                    char secondLastChar = inputBuffer.charAt(inputBuffer.length() - 2);
                    inputBuffer.setCharAt(inputBuffer.length() - 1, secondLastChar);
                    inputBuffer.setCharAt(inputBuffer.length() - 2, lastChar);
                    displayArea.setText(inputBuffer.toString());
                } else {
                    displayArea.setText("Error: Not enough elements in the stack");
                }
                break;
            case "over":
                // Push a copy of the second last element
                if (inputBuffer.length() >= 2) {
                    char secondLastChar = inputBuffer.charAt(inputBuffer.length() - 2);
                    inputBuffer.append(secondLastChar);
                    displayArea.setText(inputBuffer.toString());
                } else {
                    displayArea.setText("Error: Not enough elements in the stack");
                }
                break;
            default:

                break;


        }
    }


    private ListView<String> _stackDisplay;


    private TextArea createDisplayArea() {
        TextArea displayArea = new TextArea();
        displayArea.setEditable(false);
        displayArea.setPrefRowCount(2);
        return displayArea;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public AbstractButton getDisplayArea() {
        return null;
    }

    public void pushValue(String number) {
    }

    public ChoiceBox<Object> getStackDisplay() {
        return null;
    }

}

