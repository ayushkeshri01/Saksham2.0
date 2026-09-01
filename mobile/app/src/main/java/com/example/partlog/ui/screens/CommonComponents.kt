package com.example.partlog.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.partlog.R
import com.example.partlog.ui.AppLanguage
import com.example.partlog.ui.Loc

object BrandColors {
    val RoyalBlue = Color(0xFF023F97)
    val CyanAccent = Color(0xFF009FEE)
    val TextPrimary = Color(0xFF0F172A)
    val TextSecondary = Color(0xFF64748B)
    val DividerGrey = Color(0xFFE2E8F0)
}

@Composable
fun BrandLogoHeader(
    modifier: Modifier = Modifier,
    sakshamLogoHeight: Int = 40
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_vikas),
            contentDescription = "Vikas Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(width = 56.dp, height = 38.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Saksham Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.height(sakshamLogoHeight.dp)
        )
    }
}

@Composable
fun AppFooter(
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BrandColors.RoyalBlue, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterBadge(
                iconRes = R.drawable.ic_footer_1,
                line1 = Loc.get("Trusted", lang),
                line2 = Loc.get("Platform", lang),
                modifier = Modifier.weight(1f)
            )
            FooterDivider()
            FooterBadge(
                iconRes = R.drawable.ic_footer_2,
                line1 = Loc.get("Quality", lang),
                line2 = Loc.get("Assured", lang),
                modifier = Modifier.weight(1f)
            )
            FooterDivider()
            FooterBadge(
                iconRes = R.drawable.ic_footer_3,
                line1 = Loc.get("Data Drives", lang),
                line2 = Loc.get("Excellence", lang),
                modifier = Modifier.weight(1f)
            )
            FooterDivider()
            FooterBadge(
                iconRes = R.drawable.ic_footer_4,
                line1 = Loc.get("More Data,", lang),
                line2 = Loc.get("More Rewards", lang),
                modifier = Modifier.weight(1f)
            )
        }
        Box(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun FooterBadge(
    iconRes: Int,
    line1: String,
    line2: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = line1,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )
        Text(
            text = line2,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )
    }
}

@Composable
private fun FooterDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(36.dp)
            .background(Color.White.copy(alpha = 0.25f))
    )
}

@Composable
fun FlowNextButton(
    text: String,
    enabled: Boolean,
    themeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(color = Color(0xFFF8FAFC)) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Button(
                onClick = onClick,
                enabled = enabled,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColor,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
