package com.example.calculadora_bh

import android.annotation.SuppressLint
import android.icu.math.BigDecimal
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.util.Stack


class MainActivity : AppCompatActivity() {
    private lateinit var inputTextView: TextView
    private lateinit var outputTextView: TextView
    private var currentInput = ""
    private var isBinaryMode = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inputTextView = findViewById(R.id.input)
        outputTextView = findViewById(R.id.output)

        val buttonClickListener = { view: View ->
            val button = view as Button
            when (button.id) {
                R.id.button_0, R.id.button_1 -> appendNumber(button.text.toString())
                R.id.button_2, R.id.button_3 -> appendNumber(button.text.toString())
                R.id.button_4, R.id.button_5 -> appendNumber(button.text.toString())
                R.id.button_6, R.id.button_7 -> appendNumber(button.text.toString())
                R.id.button_8, R.id.button_9 -> appendNumber(button.text.toString())
                R.id.button_A, R.id.button_B -> appendNumber(button.text.toString())
                R.id.button_C, R.id.button_D -> appendNumber(button.text.toString())
                R.id.button_E, R.id.button_F -> appendNumber(button.text.toString())

                R.id.button_clear -> clear()
                R.id.button_equals -> calculate()
                R.id.button_addition -> appendOperation('+')
                R.id.button_subtraction -> appendOperation('-')
                R.id.button_multiply -> appendOperation('×')
                R.id.button_division -> appendOperation('÷')
                R.id.button_dot -> appendDecimal()
                R.id.button_bracket_left -> appendBracket('(')
                R.id.button_bracket_right -> appendBracket(')')
                R.id.button_arrow_left -> shiftLeft()
                R.id.button_arrow_right -> shiftRight()
                R.id.button_mode_toggle -> toggleMode()

            }
        }
        val buttons = arrayOf(
            R.id.button_0, R.id.button_1,
            R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5,
            R.id.button_6, R.id.button_7,
            R.id.button_8, R.id.button_9,
            R.id.button_A, R.id.button_B,
            R.id.button_C, R.id.button_D,
            R.id.button_E, R.id.button_F,
            R.id.button_clear, R.id.button_equals,
            R.id.button_addition, R.id.button_subtraction,
            R.id.button_multiply, R.id.button_division,
            R.id.button_bracket_left, R.id.button_bracket_right,
            R.id.button_arrow_left, R.id.button_arrow_right,
            R.id.button_mode_toggle, R.id.button_dot
        )

        buttons.forEach { id ->
            findViewById<Button>(id).setOnClickListener(buttonClickListener)
        }
    }

//Switch to binary or hexadecimal mode
    private fun toggleMode() {
        isBinaryMode = !isBinaryMode
        if (isBinaryMode) {
            hideHexButtons()
            findViewById<Button>(R.id.button_mode_toggle).text = "Hexadecimal"
        } else {
            showHexButtons()
            findViewById<Button>(R.id.button_mode_toggle).text = "Binario"
        }
        clearAll()
    }
//Clear digits from view when changing modes
    private fun clearAll() {
        currentInput = ""
        inputTextView.text = ""
        outputTextView.text = ""
    }
//Add point in view
    private fun appendDecimal() {
        if (!currentInput.contains(".")) {
            currentInput += "."
            inputTextView.text = currentInput
        }
    }
//Hide buttons hexadecimals
    private fun hideHexButtons() {
        val hexButtons = listOf(
            R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5,
            R.id.button_6, R.id.button_7,
            R.id.button_8, R.id.button_9,
            R.id.button_A, R.id.button_B,
            R.id.button_C, R.id.button_D,
            R.id.button_E, R.id.button_F
        )
        hexButtons.forEach { id ->
            findViewById<Button>(id).visibility = View.GONE
        }
    }
//Show buttons hexadecimals
    private fun showHexButtons() {
        val hexButtons = listOf(
            R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5,
            R.id.button_6, R.id.button_7,
            R.id.button_8, R.id.button_9,
            R.id.button_A, R.id.button_B,
            R.id.button_C, R.id.button_D,
            R.id.button_E, R.id.button_F
        )
        hexButtons.forEach { id ->
            findViewById<Button>(id).visibility = View.VISIBLE
        }
    }
//Add bracket in view
    private fun appendBracket(bracket: Char) {
        currentInput += bracket
        inputTextView.text = currentInput
    }
//Add operations in view
    private fun appendOperation(operation: Char) {
        currentInput += operation
        inputTextView.text = currentInput
    }
//Clear digits from view
    private fun clear() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length - 1)
            inputTextView.text = currentInput
            outputTextView.text = ""
        }
    }
//Validate what mode you are in and decide which method to call
    private fun calculate() {
        if (isBinaryMode) {
            calculateB()
        } else {
            calculateH()
        }
    }
//Add the digits
    private fun appendNumber(number: String) {
        if (!isBinaryMode || number.matches(Regex("[0-9A-Fa-f]")) || currentInput.isEmpty()) {
            currentInput += number
            inputTextView.text = currentInput
        }
    }
//This part does the hexadecimal calculations//
    private fun calculateH() {
        try {
            val input = currentInput.replace("\\s".toRegex(), "")
            if (input.isEmpty()) {
                val resultString = "0"
                outputTextView.textSize = 22f
                outputTextView.text = resultString
                return
            }

            val regex = Regex("([A-F0-9]+)|([-+×÷()])")
            val parts = regex.findAll(input).map { it.value }.toList()

            val fixedExpression = fixImplicitMultiplication(parts)

            val evaluatedParts = mutableListOf<Any>()
            for (part in fixedExpression) {
                if (part.matches("[A-F0-9]+".toRegex())) {
                    evaluatedParts.add(Integer.parseInt(part, 16))
                } else {
                    evaluatedParts.add(part)
                }
            }
            if (evaluatedParts.contains("÷") && evaluatedParts.contains(0)) {
                throw ArithmeticException("Can't divide by 0")
            }

            val result = evaluateExpressionHex(evaluatedParts)
            val decimalResult = result
            val hexadecimalResult = Integer.toHexString(result).toUpperCase()
            val octalResult = Integer.toOctalString(result)

            val resultString = "Hexadecimal: $hexadecimalResult\nOctal: $octalResult\nDecimal: $decimalResult"
            outputTextView.textSize = 22f
            outputTextView.text = resultString
        } catch (e: ArithmeticException) {
            outputTextView.textSize = 30f
            outputTextView.text = "Can't divide by 0"
        } catch (e: Exception) {
            outputTextView.text = "Error"
        }
    }

    private fun fixImplicitMultiplication(parts: List<String>): List<String> {
        val fixedExpression = mutableListOf<String>()
        for (i in parts.indices) {
            val part = parts[i]
            fixedExpression.add(part)
            if (part == "(" && i > 0) {
                val previous = parts[i - 1]
                if (previous.matches("[A-F0-9)]".toRegex())) {
                    fixedExpression.add("×")
                }
            } else if (part.matches("[A-F0-9]".toRegex()) && i < parts.lastIndex) {
                val next = parts[i + 1]
                if (next.matches("[A-F0-9(]".toRegex())) {
                    fixedExpression.add("×")
                }
            } else if (part == ")" && i < parts.lastIndex) {
                val next = parts[i + 1]
                if (next.matches("[A-F0-9(]".toRegex())) {
                    fixedExpression.add("×")
                }
            }
        }
        return fixedExpression
    }

    private fun evaluateExpressionHex(parts: List<Any>): Int {
        val operators = listOf("×", "÷", "+", "-")
        val operatorStack = Stack<String>()
        val valueStack = Stack<Int>()

        for (part in parts) {
            when {
                part is Int -> valueStack.push(part)
                part is String && operators.contains(part) -> {
                    while (!operatorStack.isEmpty() && hasPrecedence(part, operatorStack.peek())) {
                        val result = applyOperator(operatorStack.pop(), valueStack.pop(), valueStack.pop())
                        valueStack.push(result)
                    }
                    operatorStack.push(part)
                }
                part == "(" -> operatorStack.push(part.toString())
                part == ")" -> {
                    while (operatorStack.peek() != "(") {
                        val result = applyOperator(operatorStack.pop(), valueStack.pop(), valueStack.pop())
                        valueStack.push(result)
                    }
                    operatorStack.pop() // Pop the "("
                }
                else -> throw IllegalArgumentException("Parte de la expresión no válida: $part")
            }
        }

        while (!operatorStack.isEmpty()) {
            val result = applyOperator(operatorStack.pop(), valueStack.pop(), valueStack.pop())
            valueStack.push(result)
        }

        return valueStack.pop()
    }

    private fun hasPrecedence(op1: String, op2: String): Boolean {
        return (op2 == "×" || op2 == "÷") && (op1 == "+" || op1 == "-")
    }

    private fun applyOperator(operator: String, b: Int, a: Int): Int {
        return when (operator) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            "÷" -> a / b
            "()" -> a / b
            else -> throw IllegalArgumentException("Operador no válido: $operator")
        }
    }
//This part does the binary calculations//
    private fun calculateB() {
        try {
            val input = currentInput.replace("\\s".toRegex(), "")
            if (input.isEmpty()) {
                val resultString = "0"
                outputTextView.textSize = 22f
                outputTextView.text = resultString
                return
            }

            val regex = Regex("([01]+)|([-+×÷()])")
            val parts = regex.findAll(input).map { it.value }.toList()

            val fixedExpression = fixImplicitMultiplication(parts)

            val evaluatedParts = mutableListOf<Any>()
            for (part in fixedExpression) {
                if (part.matches("[01]+".toRegex())) {
                    evaluatedParts.add(Integer.parseInt(part, 2))
                } else {
                    evaluatedParts.add(part)
                }
            }
            if (evaluatedParts.contains("÷") && evaluatedParts.contains(0)) {
                throw ArithmeticException("Can't divide by 0")
            }

            val result = evaluateExpressionBinary(evaluatedParts)
            val decimalResult = result
            val binaryResult = Integer.toBinaryString(result)
            val octalResult = Integer.toOctalString(result)

            val resultString = "Binary: $binaryResult\nOctal: $octalResult\nDecimal: $decimalResult"
            outputTextView.textSize = 22f
            outputTextView.text = resultString
        } catch (e: ArithmeticException) {
            outputTextView.textSize = 30f
            outputTextView.text = "Can't divide by 0"
        } catch (e: Exception) {
            outputTextView.text = "Error"
        }
    }
    private fun evaluateExpressionBinary(parts: List<Any>): Int {
        val operators = listOf("×", "÷", "+", "-")
        val operatorStack = Stack<String>()
        val valueStack = Stack<Int>()

        for (part in parts) {
            when {
                part is Int -> valueStack.push(part)
                part is String && operators.contains(part) -> {
                    while (!operatorStack.isEmpty() && hasPrecedence(part, operatorStack.peek())) {
                        val result = applyOperator(operatorStack.pop(), valueStack.pop(), valueStack.pop())
                        valueStack.push(result)
                    }
                    operatorStack.push(part)
                }
                part == "(" -> operatorStack.push(part.toString())
                part == ")" -> {
                    while (operatorStack.peek() != "(") {
                        val result = applyOperator(operatorStack.pop(), valueStack.pop(), valueStack.pop())
                        valueStack.push(result)
                    }
                    operatorStack.pop()
                }
                else -> throw IllegalArgumentException("Invalid expression part: $part")
            }
        }

        while (!operatorStack.isEmpty()) {
            val result = applyOperator(operatorStack.pop(), valueStack.pop(), valueStack.pop())
            valueStack.push(result)
        }

        return valueStack.pop()
    }
//This part does the road//
    private fun shiftLeft() {
        try {
            val cursorPosition = inputTextView.selectionStart
            val params = currentInput.trim().split("[^0-1.]+".toRegex())
            var currentParamIndex = 0
            var currentParamStart = 0
            for (param in params) {
                val paramLength = param.length
                if (cursorPosition <= currentParamStart + paramLength) {
                    break
                }
                currentParamStart += paramLength
                currentParamIndex++
            }
            if (currentParamIndex > 0) {
                val previousParamStart = currentParamStart - params[currentParamIndex - 1].length
                val newPosition = previousParamStart + params[currentParamIndex - 1].length
                val newText = StringBuilder(currentInput)
                newText.insert(newPosition, '|')
                inputTextView.setText(newText.toString())
                inputTextView.setSelection(newPosition)
            }
        } catch (e: Exception) {
            outputTextView.text = "Error"
        }
    }

    private fun shiftRight() {
        try {
            val cursorPosition = inputTextView.selectionStart
            val params = currentInput.trim().split("[^0-1.]+".toRegex())
            var currentParamIndex = 0
            var currentParamStart = 0
            for (param in params) {
                val paramLength = param.length
                if (cursorPosition <= currentParamStart + paramLength) {
                    break
                }
                currentParamStart += paramLength
                currentParamIndex++
            }
            if (currentParamIndex < params.size - 1) {
                val nextParamStart = currentParamStart + params[currentParamIndex].length
                val newPosition = nextParamStart + 1
                val newText = StringBuilder(currentInput)
                newText.insert(newPosition, '|')
                inputTextView.setText(newText.toString())
                inputTextView.setSelection(newPosition)
            }
        } catch (e: Exception) {
            outputTextView.text = "Error"
        }
    }
    fun TextView.setSelection(index: Int) {
        val adjustedIndex = index.coerceIn(0, text.length)
        setSelection(adjustedIndex)
    }
}
