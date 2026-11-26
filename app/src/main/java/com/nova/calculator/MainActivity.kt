package com.nova.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat

private val Background = Color(0xFF090E1A)
private val Panel = Color(0xFF121B2B)
private val Key = Color(0xFF1B2639)
private val Muted = Color(0xFF8D9BB2)
private val Accent = Color(0xFF8B7CFF)
private val Mint = Color(0xFF69E0C0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CalculatorScreen()
            }
        }
    }
}

@Composable
private fun CalculatorScreen() {
    var expression by remember { mutableStateOf("0") }
    var result by remember { mutableStateOf("0") }
    var justEvaluated by remember { mutableStateOf(false) }
    var history by remember { mutableStateOf(listOf<Pair<String, String>>()) }

    fun calculate() {
        runCatching {
            val value = ExpressionParser(expression.replace("×", "*").replace("÷", "/")).parse()
            if (!value.isFinite()) error("Invalid result")
            val formatted = DecimalFormat("#,##0.##########").format(value)
            result = formatted
            history = (listOf(expression to formatted) + history).take(8)
            expression = formatted.replace(",", "")
            justEvaluated = true
        }.onFailure { result = "Error" }
    }

    fun input(label: String) {
        when (label) {
            "AC" -> { expression = "0"; result = "0"; justEvaluated = false }
            "DEL" -> {
                expression = if (expression.length <= 1) "0" else expression.dropLast(1)
                justEvaluated = false
            }
            "=" -> calculate()
            "%" -> {
                runCatching {
                    val v = ExpressionParser(expression).parse() / 100.0
                    expression = v.toString().removeSuffix(".0")
                    result = expression
                }.onFailure { result = "Error" }
                justEvaluated = false
            }
            "+/-" -> {
                expression = if (expression.startsWith("-")) expression.drop(1) else "-$expression"
                justEvaluated = false
            }
            else -> {
                if (justEvaluated && label.firstOrNull()?.isDigit() == true) expression = "0"
                if (justEvaluated && label in listOf("+", "-", "×", "÷")) justEvaluated = false
                if (expression == "0" && label !in listOf(".", "+", "-", "×", "÷")) expression = label
                else expression += label
                justEvaluated = false
            }
        }
    }

    val rows = listOf(
        listOf("AC", "+/-", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("DEL", "0", ".", "=")
    )

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF111A2B), Background, Color(0xFF080C15)))
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 22.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(Accent), contentAlignment = Alignment.Center) {
                    Text("N", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("NOVA", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                    Text("CALCULATOR", color = Muted, fontSize = 9.sp, letterSpacing = 2.sp)
                }
                Text("BASIC  •  01", color = Muted, fontSize = 10.sp, letterSpacing = 1.sp)
            }

            Surface(
                modifier = Modifier.fillMaxWidth().weight(1f, fill = false).heightIn(min = 190.dp, max = 270.dp),
                shape = RoundedCornerShape(28.dp),
                color = Panel.copy(alpha = 0.88f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(22.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    Text("YOUR CALCULATION", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        expression, color = Color(0xFFB9C5D8), fontSize = 25.sp,
                        maxLines = 2, textAlign = TextAlign.End, overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        result, color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Light,
                        maxLines = 1, textAlign = TextAlign.End, overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(Modifier.width(44.dp).height(3.dp).clip(CircleShape).background(Accent))
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("KEYPAD", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
                Text("⌫  TAP TO EDIT", color = Muted.copy(alpha = 0.7f), fontSize = 9.sp, letterSpacing = 1.sp)
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                rows.forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { label ->
                            val operator = label in listOf("÷", "×", "-", "+")
                            val utility = label in listOf("AC", "+/-", "%", "DEL")
                            val equals = label == "="
                            val bg = when {
                                equals -> Accent
                                operator -> Color(0xFF29334D)
                                utility -> Color(0xFF263247)
                                else -> Key
                            }
                            val fg = when {
                                equals -> Color.White
                                operator -> Mint
                                utility -> Color(0xFFB9C5D8)
                                else -> Color.White
                            }
                            Box(
                                modifier = Modifier.weight(1f).height(66.dp).clip(RoundedCornerShape(22.dp))
                                    .background(if (equals) Brush.linearGradient(listOf(Accent, Color(0xFF6658D9))) else Brush.linearGradient(listOf(bg, bg)))
                                    .clickable { input(label) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, color = fg, fontSize = if (label.length > 2) 16.sp else 23.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            if (history.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("RECENT", color = Muted, fontSize = 9.sp, letterSpacing = 2.sp)
                    history.take(2).forEach { (exp, res) ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(exp, color = Muted, fontSize = 12.sp)
                            Text(res, color = Mint, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            } else {
                Text("A little clarity in every calculation.", color = Muted.copy(alpha = 0.65f), fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

private class ExpressionParser(private val input: String) {
    private var pos = 0

    fun parse(): Double {
        val value = expression()
        skipSpaces()
        if (pos != input.length) error("Unexpected character")
        return value
    }

    private fun expression(): Double {
        var value = term()
        while (true) {
            skipSpaces()
            value = when {
                take('+') -> value + term()
                take('-') -> value - term()
                else -> return value
            }
        }
    }

    private fun term(): Double {
        var value = factor()
        while (true) {
            skipSpaces()
            value = when {
                take('*') -> value * factor()
                take('/') -> value / factor()
                else -> return value
            }
        }
    }

    private fun factor(): Double {
        skipSpaces()
        if (take('+')) return factor()
        if (take('-')) return -factor()
        if (take('(')) {
            val value = expression()
            if (!take(')')) error("Missing )")
            return value
        }
        val start = pos
        while (pos < input.length && (input[pos].isDigit() || input[pos] == '.')) pos++
        if (start == pos) error("Number expected")
        return input.substring(start, pos).toDouble()
    }

    private fun take(c: Char): Boolean {
        if (pos < input.length && input[pos] == c) { pos++; return true }
        return false
    }

    private fun skipSpaces() { while (pos < input.length && input[pos].isWhitespace()) pos++ }
}
