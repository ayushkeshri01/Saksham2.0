package com.example.partlog.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.partlog.ui.AppLanguage
import com.example.partlog.ui.JobViewModel
import com.example.partlog.ui.Loc

@Composable
fun ConfirmationScreen(
    viewModel: JobViewModel,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language
    val isCompressor = viewModel.componentType.value == "compressor"
    val isPranav = viewModel.brandInstalled.value == "Pranav"
    val themeColor = if (isCompressor) Color(0xFF0D4B46) else Color(0xFF023F97)
    val checkColor = if (isCompressor) Color(0xFF0D4B46) else Color(0xFF22C55E)
    val bgCircleColor = if (isCompressor) Color(0xFFE0F2F1) else Color(0xFFDCFCE7)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Checkmark with Confetti Dots
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                // Confetti dots
                Box(modifier = Modifier.offset(x = (-45).dp, y = (-40).dp).size(6.dp).background(checkColor, CircleShape))
                Box(modifier = Modifier.offset(x = 55.dp, y = (-35).dp).size(8.dp).background(Color(0xFFEF4444), CircleShape))
                Box(modifier = Modifier.offset(x = (-55).dp, y = 35.dp).size(8.dp).background(Color(0xFFEAB308), CircleShape))
                Box(modifier = Modifier.offset(x = 50.dp, y = 45.dp).size(6.dp).background(Color(0xFFF97316), CircleShape))
                Box(modifier = Modifier.offset(x = (-10).dp, y = (-65).dp).size(7.dp).background(Color(0xFF3B82F6), CircleShape))
                Box(modifier = Modifier.offset(x = 15.dp, y = 65.dp).size(6.dp).background(Color(0xFFEC4899), CircleShape))

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(bgCircleColor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = checkColor,
                        modifier = Modifier.size(72.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = Loc.get("Success!", lang),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isCompressor) {
                    Loc.get("Compressor report submitted successfully.", lang)
                } else {
                    Loc.get("Condenser report submitted successfully.", lang)
                },
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = themeColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "+10 Points",
                            color = themeColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(Color(0xFFE2E8F0))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = if (isPranav) {
                            if (isCompressor) {
                                Loc.get("Extra incentive applicable for using Pranav Compressor", lang)
                            } else {
                                Loc.get("Extra incentive applicable for using Pranav Condenser", lang)
                            }
                        } else {
                            Loc.get("Standard submission points added.", lang)
                        },
                        color = Color.Gray,
                        fontSize = 10.sp,
                        lineHeight = 13.sp,
                        modifier = Modifier.weight(1.2f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onGoHome,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp)
            ) {
                Text(
                    text = Loc.get("Back to Dashboard", lang),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = Loc.get("View My Submissions", lang),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = themeColor,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable { onGoHome() }
                    .padding(8.dp)
            )
        }
    }
}
