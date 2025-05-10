package com.kadekjuli.operasibilangan

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kadekjuli.operasibilangan.ui.theme.OperasiBilanganTheme
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OperasiBilanganTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen() {
    var number1 by remember { mutableStateOf(TextFieldValue("")) }
    var number2 by remember { mutableStateOf(TextFieldValue("")) }
    var showResult by remember { mutableStateOf(false) }
    var addition by remember { mutableStateOf("") }
    var subtraction by remember { mutableStateOf("") }
    var multiplication by remember { mutableStateOf("") }
    var division by remember { mutableStateOf("") }

    val context = LocalContext.current

    // DecimalFormat untuk format ribuan dengan titik (Indonesian style)
    val decimalFormat = DecimalFormat("#,###", DecimalFormatSymbols(Locale("id", "ID")).apply {
        groupingSeparator = '.'
    })

    // DecimalFormat untuk pembagian dengan 2 angka di belakang koma
    val decimalFormatDiv = DecimalFormat("#,###.##", DecimalFormatSymbols(Locale("id", "ID")).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    })

    // Fungsi untuk memformat angka ke string dengan ribuan
    fun formatNumber(value: Long): String {
        return decimalFormat.format(value)
    }

    // Fungsi untuk memformat angka pembagian dengan 2 angka di belakang koma
    fun formatDivisionNumber(value: Double): String {
        return decimalFormatDiv.format(value)
    }

    // Fungsi untuk memparsing input string ke integer
    fun parseInput(input: String): Int? {
        return try {
            val cleanInput = input.replace(".", "").trim()
            if (cleanInput.isEmpty() || cleanInput == "-") {
                null
            } else {
                // Coba parse ke Int, jika gagal karena overflow, return null
                try {
                    cleanInput.toInt()
                } catch (e: NumberFormatException) {
                    // Jika nilai terlalu besar, tampilkan pesan
                    Toast.makeText(context, "Nilai terlalu besar, gunakan nilai yang lebih kecil", Toast.LENGTH_SHORT).show()
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    // Fungsi untuk memfilter input agar hanya menerima angka, minus, dan titik ribuan
    fun filterNumericInput(input: String): String {
        val allowedChars = "-0123456789.".toSet()
        return input.filter { it in allowedChars }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Operasi Bilangan",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Masukkan Bilangan",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    OutlinedTextField(
                        value = number1,
                        onValueChange = { newValue ->
                            val filteredText = filterNumericInput(newValue.text)
                            val cleanText = filteredText.replace(".", "")
                            val parsed = parseInput(cleanText)
                            number1 = if (parsed != null) {
                                TextFieldValue(formatNumber(parsed.toLong()), newValue.selection)
                            } else {
                                TextFieldValue(filteredText, newValue.selection)
                            }
                        },
                        label = { Text("Bilangan 1") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = number2,
                        onValueChange = { newValue ->
                            val filteredText = filterNumericInput(newValue.text)
                            val cleanText = filteredText.replace(".", "")
                            val parsed = parseInput(cleanText)
                            number2 = if (parsed != null) {
                                TextFieldValue(formatNumber(parsed.toLong()), newValue.selection)
                            } else {
                                TextFieldValue(filteredText, newValue.selection)
                            }
                        },
                        label = { Text("Bilangan 2") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }

            Button(
                onClick = {
                    val num1 = parseInput(number1.text)
                    val num2 = parseInput(number2.text)

                    if (num1 != null && num2 != null) {
                        try {
                            // Limit untuk nilai maksimum yang bisa diproses
                            val maxLimit = Int.MAX_VALUE.toLong()
                            val minLimit = Int.MIN_VALUE.toLong()

                            // Penjumlahan dengan pengecekan overflow
                            val sum = num1.toLong() + num2.toLong()
                            addition = if (sum in minLimit..maxLimit) {
                                formatNumber(sum)
                            } else {
                                "Melebihi batas"
                            }

                            // Pengurangan
                            val diff = num1.toLong() - num2.toLong()
                            subtraction = if (diff in minLimit..maxLimit) {
                                formatNumber(diff)
                            } else {
                                "Melebihi batas"
                            }

                            // Perkalian dengan pengecekan overflow
                            try {
                                val product = num1.toLong() * num2.toLong()
                                multiplication = if (product in minLimit..maxLimit) {
                                    formatNumber(product)
                                } else {
                                    "Melebihi batas"
                                }
                            } catch (e: ArithmeticException) {
                                multiplication = "Melebihi batas"
                            }

                            // Pembagian dengan angka di belakang koma
                            division = if (num2 != 0) {
                                val divResult = num1.toDouble() / num2.toDouble()
                                formatDivisionNumber(divResult)
                            } else {
                                "Tidak bisa dibagi 0"
                            }

                            showResult = true
                            Toast.makeText(context, "Perhitungan berhasil", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Masukkan bilangan bulat yang valid", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Calculate Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    "Hitung",
                    fontSize = 16.sp
                )
            }

            AnimatedVisibility(
                visible = showResult,
                enter = fadeIn(animationSpec = tween(300)) +
                        expandVertically(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) +
                        shrinkVertically(animationSpec = tween(300))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Hasil Perhitungan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Divider(
                            modifier = Modifier.padding(bottom = 12.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )

                        ResultRow(
                            label = "Penjumlahan",
                            value = addition,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.primary
                        )

                        ResultRow(
                            label = "Pengurangan",
                            value = subtraction,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.primary
                        )

                        ResultRow(
                            label = "Perkalian",
                            value = multiplication,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.primary
                        )

                        ResultRow(
                            label = "Pembagian",
                            value = division,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultRow(
    label: String,
    value: String,
    containerColor: Color,
    labelColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label:",
                fontWeight = FontWeight.Medium,
                color = labelColor
            )

            Text(
                text = value,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}