package com.example.partlog.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.partlog.R
import com.example.partlog.ui.JobViewModel
import com.example.partlog.ui.Loc

@Composable
fun LandingScreen(
    viewModel: JobViewModel,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar: language toggle aligned right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            LanguageToggle(
                currentLanguage = lang,
                onLanguageChange = { viewModel.language.value = it }
            )
        }

        BrandLogoHeader()

        Spacer(modifier = Modifier.height(12.dp))

        // Hero: slogan on the left, illustration on the right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1.15f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = BrandColors.TextPrimary)) {
                            append(Loc.get("Collect Data.", lang))
                        }
                        append("\n")
                        withStyle(style = SpanStyle(color = BrandColors.RoyalBlue)) {
                            append(Loc.get("Drive Quality.", lang))
                        }
                        append("\n")
                        withStyle(style = SpanStyle(color = BrandColors.RoyalBlue)) {
                            append(Loc.get("Earn More.", lang))
                        }
                    },
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 33.sp
                )

                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(3.dp)
                        .background(BrandColors.RoyalBlue, shape = RoundedCornerShape(2.dp))
                )

                Text(
                    text = Loc.get(
                        "Submit accurate compressor & condenser data and get extra incentives for using Pranav condensers.",
                        lang
                    ),
                    fontSize = 12.sp,
                    color = BrandColors.TextSecondary,
                    lineHeight = 17.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Image(
                painter = painterResource(id = R.drawable.hero_illustration),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .weight(1f)
                    .height(190.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Features card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 18.dp),
                verticalAlignment = Alignment.Top
            ) {
                FeatureItem(
                    iconRes = R.drawable.ic_feature_1,
                    text = Loc.get("Easy & Quick\nData Submission", lang),
                    underlineColor = BrandColors.RoyalBlue,
                    modifier = Modifier.weight(1f)
                )
                FeatureDivider()
                FeatureItem(
                    iconRes = R.drawable.ic_feature_2,
                    text = Loc.get("Extra Incentive\nfor Pranav\nCondensers", lang),
                    underlineColor = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                FeatureDivider()
                FeatureItem(
                    iconRes = R.drawable.ic_feature_3,
                    text = Loc.get("Data Driven\nInsights", lang),
                    underlineColor = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
                FeatureDivider()
                FeatureItem(
                    iconRes = R.drawable.ic_feature_4,
                    text = Loc.get("Accurate Data,\nBetter Quality", lang),
                    underlineColor = Color(0xFF9C27B0),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Action buttons
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandColors.RoyalBlue)
        ) {
            Text(
                text = Loc.get("LOGIN", lang),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            border = BorderStroke(1.5.dp, BrandColors.RoyalBlue),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandColors.RoyalBlue)
        ) {
            Text(
                text = Loc.get("REGISTER", lang),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        AppFooter(lang = lang)
    }
}

@Composable
private fun FeatureItem(
    iconRes: Int,
    text: String,
    underlineColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(34.dp)
        )
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = BrandColors.TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )
        Box(
            modifier = Modifier
                .width(16.dp)
                .height(2.dp)
                .background(underlineColor, shape = RoundedCornerShape(1.dp))
        )
    }
}

@Composable
private fun FeatureDivider() {
    Box(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .width(1.dp)
            .fillMaxHeight()
            .background(BrandColors.DividerGrey)
    )
}
