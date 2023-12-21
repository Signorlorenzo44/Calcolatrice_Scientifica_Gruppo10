package com.example.calculatorapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorAppTest {

    @Test
    void testIsValidComplexNumber_ValidInput() {
        CalculatorApp calculator = new CalculatorApp();

        // Test input with valid complex number format
        assertTrue(calculator.isValidComplexNumber("3+4j"));
        assertTrue(calculator.isValidComplexNumber("0-5.5j"));
        assertTrue(calculator.isValidComplexNumber("-2.3+7.8j"));
        assertTrue(calculator.isValidComplexNumber("-1.1-6.6j"));
        assertTrue(calculator.isValidComplexNumber("10j"));
    }

    @Test
    void testIsValidComplexNumber_InvalidInput() {
        CalculatorApp calculator = new CalculatorApp();

        // Test input with invalid complex number format
        assertFalse(calculator.isValidComplexNumber("3+4")); // Missing imaginary part
        assertFalse(calculator.isValidComplexNumber("3j+4")); // Incorrect order of parts
        assertFalse(calculator.isValidComplexNumber("3.5+j")); // Missing real part
        assertFalse(calculator.isValidComplexNumber("3.5+j5")); // Incorrect format
        assertFalse(calculator.isValidComplexNumber("abc")); // Non-numeric characters
        assertFalse(calculator.isValidComplexNumber("")); // Empty input
    }

    @Test
    public void testParseComplexNumber_ValidInput() {
        CalculatorApp calculator = new CalculatorApp();

        ComplexNumber result = calculator.parseComplexNumber("3+4j");
        assertNotNull(result);
        assertEquals(3.0, result.getReal());
        assertEquals(4.0, result.getImaginary());
    }

    @Test
    public void testParseComplexNumber_InvalidInput() {
        CalculatorApp calculator = new CalculatorApp();

        ComplexNumber result = calculator.parseComplexNumber("invalid");
        assertNull(result);
    }

    private CalculatorApp calculatorApp;

    @BeforeEach
    void setUp() {
        calculatorApp = new CalculatorApp();
    }

    @Test
    void testUpdateStackDisplay() throws NoSuchFieldException, IllegalAccessException {
        Deque<ComplexNumber> stack = new ArrayDeque<>();
        for (int i = 0; i < 12; i++) {
            stack.push(new ComplexNumber(i, i)); // Aggiungi numeri complessi fittizi
        }

        // Utilizza riflessione per accedere al campo privato 'lastTwelveStackElements'
        Field field = CalculatorApp.class.getDeclaredField("lastTwelveStackElements");
        field.setAccessible(true);
        field.set(calculatorApp, stack);

        calculatorApp.updateStackDisplay();

        StringBuilder expectedOutput = new StringBuilder();
        for (ComplexNumber number : stack) {
            expectedOutput.append(number.toString()).append("\n");
        }

        // Utilizza riflessione per ottenere il valore del campo privato 'lastTwelveStackElements'
        Deque<ComplexNumber> actualStack = (Deque<ComplexNumber>) field.get(calculatorApp);

        StringBuilder actualOutput = new StringBuilder();
        for (ComplexNumber number : actualStack) {
            actualOutput.append(number.toString()).append("\n");
        }

        assertEquals(expectedOutput.toString(), actualOutput.toString());
    }


    @Test
    void testHandleUserInputWithValidComplexNumber() {
        String validInput = "3+4j"; // Un numero complesso valido
        calculatorApp.handleUserInput(validInput);

        // Assicura che l'area di visualizzazione mostri uno stack con il numero complesso inserito
        assertEquals("3.0 + 4.0i", calculatorApp.getStackDisplay().getItems().get(0));
    }

    @Test
    void testHandleUserInputWithInvalidInput() {
        String invalidInput = "Invalid"; // Un input non valido
        calculatorApp.handleUserInput(invalidInput);

        // Assicura che l'area di visualizzazione mostri un messaggio di errore
        assertEquals("Invalid input", calculatorApp.getDisplayArea().getText());
    }
    @Test
    public void testPerformOperation_Addition() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.handleUserInput("3+5");
        calculator.performOperation("+");

        // Verifica se l'operazione di addizione è stata eseguita correttamente
        assertEquals("8.0", calculator.getDisplayArea().getText());
    }

    @Test
    public void testPerformOperation_Subtraction() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.handleUserInput("10-5");
        calculator.performOperation("-");

        // Verifica se l'operazione di sottrazione è stata eseguita correttamente
        assertEquals("5.0", calculator.getDisplayArea().getText());
    }
    @Test
    public void testCalculateResult_Addition() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.handleUserInput("3+5");
        calculator.performOperation("+");
        ComplexNumber result = calculator.calculateResult("+");

        // Verifica se l'operazione di addizione restituisce il risultato corretto
        assertEquals(new ComplexNumber(8.0, 0.0), result);
    }

    @Test
    public void testCalculateResult_Subtraction() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.handleUserInput("10-5");
        calculator.performOperation("-");
        ComplexNumber result = calculator.calculateResult("-");

        // Verifica se l'operazione di sottrazione restituisce il risultato corretto
        assertEquals(new ComplexNumber(5.0, 0.0), result);
    }
    @Test
    public void testEvaluatePostfix_Addition() {
        CalculatorApp calculator = new CalculatorApp();
        List<String> postfix = Arrays.asList("3", "5", "+");
        double result = calculator.evaluatePostfix(postfix);

        // Verifica se l'operazione di addizione restituisce il risultato corretto
        assertEquals(8.0, result);
    }

    @Test
    public void testEvaluatePostfix_Multiplication() {
        CalculatorApp calculator = new CalculatorApp();
        List<String> postfix = Arrays.asList("3", "5", "*");
        double result = calculator.evaluatePostfix(postfix);

        // Verifica se l'operazione di moltiplicazione restituisce il risultato corretto
        assertEquals(15.0, result);
    }
    @Test
    public void testConvertToPostfix_AdditionAndMultiplication() {
        CalculatorApp calculator = new CalculatorApp();
        List<String> tokens = Arrays.asList("3", "+", "5", "*", "2");
        List<String> postfix = calculator.convertToPostfix(tokens);

        // Verifica se la conversione a postfix è corretta per l'operazione "3 + 5 * 2"
        List<String> expectedPostfix = Arrays.asList("3", "5", "2", "*", "+");
        assertIterableEquals(expectedPostfix, postfix);
    }

    @Test
    public void testConvertToPostfix_Parentheses() {
        CalculatorApp calculator = new CalculatorApp();
        List<String> tokens = Arrays.asList("(", "3", "+", "5", ")", "*", "2");
        List<String> postfix = calculator.convertToPostfix(tokens);

        // Verifica se la conversione a postfix è corretta per l'operazione "(3 + 5) * 2"
        List<String> expectedPostfix = Arrays.asList("3", "5", "+", "2", "*");
        assertIterableEquals(expectedPostfix, postfix);
    }
    @Test
    public void testGetStackElements_EmptyStack() {
        CalculatorApp calculator = new CalculatorApp();
        List<String> stackElements = calculator.getStackElements();

        // Verifica se la pila è vuota all'inizio
        assertTrue(stackElements.isEmpty());
    }
    @Test
    public void testCompareValues_LessComparison() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.handleUserInput("5");
        calculator.handleUserInput("3");
        calculator.handleUserInput("less");

        assertEquals("true", calculator.getDisplayArea().getText());
    }

    @Test
    public void testCompareValues_GreaterComparison() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.handleUserInput("5");
        calculator.handleUserInput("3");
        calculator.handleUserInput("greater");

        assertEquals("true", calculator.getDisplayArea().getText());
    }
    @Test
    public void testCalculateSquareRoot_PositiveNumber() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.handleUserInput("25");
        calculator.calculateSquareRoot();

        assertEquals("5.0", calculator.getDisplayArea().getText());
    }

    @Test
    public void testCalculateSquareRoot_NegativeNumber() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.handleUserInput("-25");
        calculator.calculateSquareRoot();

        assertEquals("Error", calculator.getDisplayArea().getText());
    }
    @Test
    public void testInvertSign_PositiveNumber() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.pushValue("5"); // Push a positive number onto the stack
        calculator.invertSign(); // Invert the sign of the number

        assertEquals("-5", calculator.getStackElements().get(0)); // Check if the sign is inverted
    }

    @Test
    public void testInvertSign_NegativeNumber() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.pushValue("-8"); // Push a negative number onto the stack
        calculator.invertSign(); // Invert the sign of the number

        assertEquals("8", calculator.getStackElements().get(0)); // Check if the sign is inverted
    }

    @Test
    public void testInvertSign_Zero() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.pushValue("0"); // Push zero onto the stack
        calculator.invertSign(); // Invert the sign of the number

        assertEquals("0", calculator.getStackElements().get(0)); // Zero remains unchanged
    }
    @Test
    public void testSwapOperands_TwoOperands() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.pushValue("5"); // Push operand 1
        calculator.pushValue("10"); // Push operand 2
        calculator.swapOperands(); // Swap the operands

        assertEquals("5", calculator.getStackElements().get(0)); // Check if operand 1 is now the original operand 2
        assertEquals("10", calculator.getStackElements().get(1)); // Check if operand 2 is now the original operand 1
    }

    @Test
    public void testSwapOperands_OneOperand() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.pushValue("5"); // Push operand 1
        calculator.swapOperands(); // Try swapping when only one operand is present

        assertEquals("5", calculator.getStackElements().get(0)); // Check if the single operand remains unchanged
    }

    @Test
    public void testSwapOperands_NoOperands() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.swapOperands(); // Try swapping when no operands are present

        assertEquals(0, calculator.getStackElements().size()); // Check if the stack remains empty
    }
    @Test
    public void testDropOperand_WithOperands() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.pushValue("5");
        calculator.pushValue("10");
        calculator.dropOperand(); // Drop an operand

        assertEquals("5", calculator.getStackElements().get(0)); // Check if the top operand is retained after dropping one
        assertEquals(1, calculator.getStackElements().size()); // Check if only one operand remains
    }

    @Test
    public void testDropOperand_NoOperands() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.dropOperand(); // Try dropping an operand when no operands are present

        assertEquals(0, calculator.getStackElements().size()); // Check if the stack remains empty
    }
    @Test
    public void testDuplicateOperand_WithOperand() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.pushValue("5");
        calculator.duplicateOperand(); // Duplicate an operand

        assertEquals("5", calculator.getStackElements().get(0)); // Check if the original operand is retained
        assertEquals("5", calculator.getStackElements().get(1)); // Check if the duplicated operand is added to the stack
        assertEquals(2, calculator.getStackElements().size()); // Check if the stack size is incremented by one
    }

    @Test
    public void testDuplicateOperand_NoOperands() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.duplicateOperand(); // Try duplicating an operand when no operands are present

        assertEquals(0, calculator.getStackElements().size()); // Check if the stack remains empty
    }
    @Test
    public void testOverOperands_EnoughOperands() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.pushValue("5");
        calculator.pushValue("10");
        calculator.overOperands(); // Perform the over operation

        assertEquals("10", calculator.getStackElements().get(0)); // Check if the top operand is retained
        assertEquals(1, calculator.getStackElements().size()); // Check if only one operand remains after over operation
    }

    @Test
    public void testOverOperands_InsufficientOperands() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.pushValue("5");
        calculator.overOperands(); // Try performing the over operation with insufficient operands

        assertEquals(1, calculator.getStackElements().size()); // Check if the stack size remains unchanged
    }
    @Test
    public void testCalculate_Addition() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.calculate("5");
        calculator.calculate("10");
        calculator.calculate("+"); // Perform addition

        assertEquals("15", calculator.getStackElements().get(0)); // Check if addition is performed correctly
        assertEquals(1, calculator.getStackElements().size()); // Check if only one operand remains
    }

    @Test
    public void testCalculate_Multiplication() {
        CalculatorApp calculator = new CalculatorApp();
        calculator.calculate("5");
        calculator.calculate("10");
        calculator.calculate("*"); // Perform multiplication

        assertEquals("50", calculator.getStackElements().get(0)); // Check if multiplication is performed correctly
        assertEquals(1, calculator.getStackElements().size()); // Check if only one operand remains
    }
}