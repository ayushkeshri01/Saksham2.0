package com.example.partlog.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import com.example.partlog.R
import com.example.partlog.db.JobEntry
import com.example.partlog.ui.AppLanguage
import com.example.partlog.ui.JobViewModel
import com.example.partlog.ui.Loc
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: JobViewModel,
    onLogJobClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language
    val entries by viewModel.entries.collectAsState()
    val totalPoints by viewModel.points.collectAsState()
    val redeemedPoints = viewModel.redeemedPoints.value
    val availablePoints = totalPoints - redeemedPoints
    val context = LocalContext.current

    var activeTab by remember { mutableStateOf("dashboard") }
    var showRedeemDialog by remember { mutableStateOf(false) }
    var showKycDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CustomTopBar(
                lang = lang,
                userName = viewModel.mechanicName.value.ifBlank { "Saksham User" },
                onLogoutClick = onLogoutClick
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
            ) {
                HorizontalDivider(color = BrandColors.DividerGrey)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem(
                        selected = activeTab == "dashboard",
                        onClick = { activeTab = "dashboard" },
                        icon = Icons.Default.Home,
                        label = Loc.get("Dashboard", lang)
                    )
                    BottomNavItem(
                        selected = activeTab == "submissions",
                        onClick = { activeTab = "submissions" },
                        icon = Icons.Default.List,
                        label = Loc.get("My Submissions", lang)
                    )
                    BottomNavItem(
                        selected = activeTab == "reports",
                        onClick = { activeTab = "reports" },
                        icon = Icons.Default.Info,
                        label = Loc.get("Reports", lang)
                    )
                    BottomNavItem(
                        selected = activeTab == "profile",
                        onClick = { activeTab = "profile" },
                        icon = Icons.Default.Person,
                        label = Loc.get("Profile", lang)
                    )
                }

                // Floating Action Button docked in the center
                FloatingActionButton(
                    onClick = {
                        viewModel.resetDraft()
                        viewModel.componentType.value = "condenser"
                        onLogJobClick()
                    },
                    shape = CircleShape,
                    containerColor = BrandColors.RoyalBlue,
                    contentColor = Color.White,
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-24).dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New Entry", modifier = Modifier.size(26.dp))
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF5F9FE))
        ) {
            when (activeTab) {
                "dashboard" -> DashboardView(
                    viewModel = viewModel,
                    entriesCount = entries.size,
                    availablePoints = availablePoints,
                    onLogCondenser = {
                        viewModel.resetDraft()
                        viewModel.componentType.value = "condenser"
                        onLogJobClick()
                    },
                    onLogCompressor = {
                        viewModel.resetDraft()
                        viewModel.componentType.value = "compressor"
                        onLogJobClick()
                    }
                )
                "submissions" -> SubmissionsView(entries = entries, lang = lang)
                "reports" -> ReportsView(entries = entries, points = availablePoints, lang = lang)
                "profile" -> ProfileView(
                    viewModel = viewModel,
                    totalPoints = totalPoints,
                    redeemedPoints = redeemedPoints,
                    availablePoints = availablePoints,
                    onRedeemClick = { showRedeemDialog = true },
                    onVerifyKycClick = { showKycDialog = true },
                    onLogout = onLogoutClick
                )
            }

            if (showRedeemDialog) {
                RedeemPointsDialog(
                    availablePoints = availablePoints,
                    lang = lang,
                    onDismiss = { showRedeemDialog = false },
                    onRedeem = { ptsToRedeem ->
                        viewModel.redeemPoints(ptsToRedeem)
                        showRedeemDialog = false
                        Toast.makeText(context, Loc.get("Redemption request of ₹$ptsToRedeem submitted successfully!", lang), Toast.LENGTH_LONG).show()
                    }
                )
            }

            if (showKycDialog) {
                KycVerificationDialog(
                    viewModel = viewModel,
                    lang = lang,
                    onDismiss = { showKycDialog = false },
                    onSuccess = { panName ->
                        showKycDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun CustomTopBar(
    lang: AppLanguage,
    userName: String,
    onLogoutClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val initials = userName.trim().split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")
        .ifBlank { "U" }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(60.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left side: Vikas Logo
        Image(
            painter = painterResource(id = R.drawable.logo_vikas),
            contentDescription = "Vikas Logo",
            modifier = Modifier.height(30.dp),
            contentScale = ContentScale.Fit
        )

        // Center: Saksham Logo inside centered flexible Box
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Saksham Logo",
                modifier = Modifier.height(30.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Right side: Profile initials menu
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.clickable { showMenu = true }
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(BrandColors.RoyalBlue, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Profile Options",
                tint = BrandColors.RoyalBlue,
                modifier = Modifier.size(18.dp)
            )

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(Color.White)
            ) {
                DropdownMenuItem(
                    text = { Text(userName, fontWeight = FontWeight.SemiBold, color = BrandColors.TextPrimary) },
                    onClick = { showMenu = false },
                    enabled = false
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text(Loc.get("Logout Profile", lang), fontWeight = FontWeight.SemiBold, color = Color(0xFFF43F5E)) },
                    onClick = {
                        showMenu = false
                        onLogoutClick()
                    }
                )
            }
        }
    }
}

@Composable
fun RowScope.BottomNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) BrandColors.RoyalBlue else Color(0xFF94A3B8),
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                maxLines = 1,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) BrandColors.RoyalBlue else Color(0xFF94A3B8)
            )
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(36.dp)
                    .height(3.dp)
                    .background(BrandColors.RoyalBlue, shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
            )
        }
    }
}

@Composable
fun DashboardView(
    viewModel: JobViewModel,
    entriesCount: Int,
    availablePoints: Int,
    onLogCondenser: () -> Unit,
    onLogCompressor: () -> Unit
) {
    val name = viewModel.mechanicName.value.ifBlank { "Abhishek" }
    val lang = viewModel.language.value
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = Loc.get("Hello, $name! 👋", lang),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = BrandColors.RoyalBlue
            )
            Text(
                text = Loc.get("Let's collect accurate data and drive quality together.", lang),
                fontSize = 12.sp,
                color = BrandColors.TextSecondary,
                lineHeight = 16.sp
            )
        }

        // Stats cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                icon = Icons.Default.TrendingUp,
                label = Loc.get("Total Submissions", lang),
                value = "$entriesCount",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.Star,
                label = Loc.get("Available Points", lang),
                value = "$availablePoints",
                modifier = Modifier.weight(1f)
            )
        }

        // Center Divider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFDDE1E7)))
            Column(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = Loc.get("What would you like to log today?", lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = Loc.get("Choose the component you want to submit data for", lang),
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
            Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFDDE1E7)))
        }

        // Side-by-side component cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CONDENSER card (Blue theme)
            ComponentCard(
                title = Loc.get("CONDENSER", lang),
                description = Loc.get("Log condenser details, issues and related information.", lang),
                buttonText = Loc.get("LOG CONDENSER", lang),
                onClick = onLogCondenser,
                imageRes = R.drawable.hero_illustration,
                isCompressor = false,
                gradientColors = listOf(Color(0xFF023F97), Color(0xFF001F4D)),
                buttonTextColor = Color(0xFF023F97),
                modifier = Modifier.weight(1f)
            )

            // COMPRESSOR card (Teal theme)
            ComponentCard(
                title = Loc.get("COMPRESSOR", lang),
                description = Loc.get("Log compressor details, issues and related information.", lang),
                buttonText = Loc.get("LOG COMPRESSOR", lang),
                onClick = onLogCompressor,
                imageRes = R.drawable.hero_illustration,
                isCompressor = true,
                gradientColors = listOf(Color(0xFF0D4B46), Color(0xFF032220)),
                buttonTextColor = Color(0xFF084D47),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Promotion banner card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F9FE)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFBDD4F1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Part
                Row(
                    modifier = Modifier.weight(1.2f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(BrandColors.RoyalBlue, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_feature_2),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = Loc.get("Use Pranav Condensers", lang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = BrandColors.RoyalBlue
                        )
                        Text(
                            text = Loc.get("Earn extra incentives on every submission.", lang),
                            fontSize = 11.sp,
                            color = BrandColors.TextSecondary,
                            lineHeight = 14.sp
                        )
                    }
                }
                
                // Vertical separator
                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(1.dp)
                        .height(36.dp)
                        .background(Color(0xFFBDD4F1))
                  )
                
                // Right Part
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = Loc.get("Extra Incentive", lang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = BrandColors.RoyalBlue
                        )
                        Text(
                            text = Loc.get("for Pranav Condensers", lang),
                            fontSize = 10.sp,
                            color = BrandColors.TextSecondary
                        )
                    }
                    
                    Box(
                        modifier = Modifier.size(34.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_footer_2),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                        Text(
                            text = "₹",
                            color = BrandColors.RoyalBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.offset(y = (-2).dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.height(72.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFEAF0F8), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrandColors.RoyalBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandColors.TextSecondary,
                    maxLines = 1
                )
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = BrandColors.RoyalBlue
                )
            }
        }
    }
}

@Composable
fun ComponentCard(
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit,
    imageRes: Int,
    isCompressor: Boolean,
    gradientColors: List<Color>,
    buttonTextColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .height(320.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(2.5.dp)
                        .background(
                            if (isCompressor) Color(0xFF00FFCC) else Color(0xFF3399FF),
                            shape = RoundedCornerShape(1.dp)
                        )
                )
            }
            
            // pedestal glow
            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    (if (isCompressor) Color(0xFF00FFCC) else Color(0xFF3399FF)).copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    alignment = if (isCompressor) Alignment.CenterEnd else Alignment.TopCenter,
                    modifier = Modifier.size(105.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = buttonText,
                            fontWeight = FontWeight.Bold,
                            color = buttonTextColor,
                            fontSize = 11.sp
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = buttonTextColor,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileView(
    viewModel: JobViewModel,
    totalPoints: Int,
    redeemedPoints: Int,
    availablePoints: Int,
    onRedeemClick: () -> Unit,
    onVerifyKycClick: () -> Unit,
    onLogout: () -> Unit
) {
    val lang = viewModel.language.value
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }
    var editWorkshop by remember { mutableStateOf(viewModel.mechanicWorkshop.value) }
    var editCity by remember { mutableStateOf(viewModel.mechanicCity.value) }
    var updating by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFFEAF0F8), shape = RoundedCornerShape(40.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = "User avatar", modifier = Modifier.size(40.dp), tint = Color(0xFF023F97))
        }

        Text(
            text = viewModel.mechanicName.value.ifBlank { "Abhishek Kumar" },
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF1A1A1A)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFDDE1E7), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileRow(Loc.get("Mobile Number", lang), viewModel.mechanicId.value)
                HorizontalDivider()
                
                if (viewModel.mechanicDob.value.isNotBlank()) {
                    ProfileRow(Loc.get("Date of Birth", lang), viewModel.mechanicDob.value)
                    HorizontalDivider()
                }

                if (isEditing) {
                    // Edit Workshop
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(Loc.get("Workshop Name", lang), color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = editWorkshop,
                            onValueChange = { editWorkshop = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF023F97),
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            )
                        )
                    }
                    HorizontalDivider()

                    // Edit City
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(Loc.get("City", lang), color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = editCity,
                            onValueChange = { editCity = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF023F97),
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            )
                        )
                    }
                } else {
                    ProfileRow(Loc.get("Workshop", lang), viewModel.mechanicWorkshop.value)
                    HorizontalDivider()
                    ProfileRow(Loc.get("City", lang), viewModel.mechanicCity.value.ifBlank { "Faridabad" })
                }
                
                HorizontalDivider()
                ProfileRow(Loc.get("Total Earned", lang), "$totalPoints pts")
                HorizontalDivider()
                ProfileRow(Loc.get("Total Redeemed", lang), "$redeemedPoints pts")
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = Loc.get("Available Balance", lang), color = Color.Gray, fontSize = 14.sp)
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFF9C4), shape = RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFFFBC02D), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = "$availablePoints pts (₹$availablePoints)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFD84315))
                    }
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = Loc.get("App Language", lang), color = Color.Gray, fontSize = 14.sp)
                    LanguageToggle(
                        currentLanguage = viewModel.language.value,
                        onLanguageChange = { viewModel.language.value = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // KYC Status Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFDDE1E7), RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Loc.get("kyc_title", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF023F97)
                    )
                    
                    val panStatus = viewModel.mechanicPanStatus.value
                    val badgeColor = when (panStatus) {
                        "VERIFIED" -> Color(0xFFE8F5E9)
                        "PENDING" -> Color(0xFFFFF3E0)
                        else -> Color(0xFFFFEBEE)
                    }
                    val textColor = when (panStatus) {
                        "VERIFIED" -> Color(0xFF2E7D32)
                        "PENDING" -> Color(0xFFEF6C00)
                        else -> Color(0xFFC62828)
                    }
                    val statusText = when (panStatus) {
                        "VERIFIED" -> Loc.get("kyc_verified", lang)
                        "PENDING" -> Loc.get("kyc_pending", lang)
                        else -> Loc.get("kyc_not_submitted", lang)
                    }
                    
                    Box(
                        modifier = Modifier
                            .background(badgeColor, shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusText,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
                
                HorizontalDivider()
                
                val panStatus = viewModel.mechanicPanStatus.value
                if (panStatus == "VERIFIED") {
                    val rawPan = viewModel.mechanicPanNumber.value
                    val maskedPan = if (rawPan.length >= 10) rawPan.substring(0, 2) + "XXXXXX" + rawPan.substring(8) else rawPan
                    ProfileRow(Loc.get("kyc_pan_number", lang), maskedPan)
                    HorizontalDivider()
                    ProfileRow(Loc.get("kyc_name", lang), viewModel.mechanicPanName.value)
                } else {
                    Text(
                        text = Loc.get("kyc_required_msg", lang),
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 15.sp
                    )
                    
                    Button(
                        onClick = { onVerifyKycClick() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF023F97)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().height(38.dp)
                    ) {
                        Text(Loc.get("kyc_verify_btn", lang), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Edit profile / Save & Cancel button row
        if (isEditing) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        isEditing = false
                        editWorkshop = viewModel.mechanicWorkshop.value
                        editCity = viewModel.mechanicCity.value
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(Loc.get("Cancel", lang), fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (editWorkshop.isBlank() || editCity.isBlank()) {
                            Toast.makeText(context, "Fields cannot be blank", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        updating = true
                        viewModel.updateProfile(
                            workshop = editWorkshop,
                            city = editCity,
                            onSuccess = {
                                updating = false
                                isEditing = false
                                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { error ->
                                updating = false
                                Toast.makeText(context, "Failed: $error", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    enabled = !updating,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF023F97)),
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (updating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(Loc.get("Save", lang), fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        } else {
            Button(
                onClick = {
                    editWorkshop = viewModel.mechanicWorkshop.value
                    editCity = viewModel.mechanicCity.value
                    isEditing = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF023F97)),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Text(Loc.get("Edit Details", lang), fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val isKycVerified = viewModel.mechanicPanStatus.value == "VERIFIED"
        Button(
            onClick = {
                if (isKycVerified) {
                    onRedeemClick()
                } else {
                    Toast.makeText(context, Loc.get("kyc_required_msg", lang), Toast.LENGTH_LONG).show()
                    onVerifyKycClick()
                }
            },
            enabled = availablePoints >= 100 && !isEditing,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🪙")
                Text(Loc.get("REDEEM POINTS NOW", lang), fontWeight = FontWeight.Black)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onLogout,
            enabled = !isEditing,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)),
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(Loc.get("Logout Profile", lang), fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun RedeemPointsDialog(
    availablePoints: Int,
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onRedeem: (Int) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var ptsInput by remember { mutableStateOf("") }
    
    var upiId by remember { mutableStateOf("") }

    var holderName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var confirmAccountNumber by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var ifscCode by remember { mutableStateOf("") }

    val ptsToRedeem = ptsInput.toIntOrNull() ?: 0
    val isValidPts = ptsToRedeem in 100..availablePoints
    
    val canRedeem = if (selectedTab == 0) {
        isValidPts && upiId.isNotBlank() && upiId.contains("@")
    } else {
        isValidPts && holderName.isNotBlank() && accountNumber.isNotBlank() && 
        accountNumber == confirmAccountNumber && bankName.isNotBlank() && ifscCode.isNotBlank()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = Loc.get("Redeem Incentives", lang),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color(0xFF023F97)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F9FE), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(Loc.get("Available Balance:", lang), fontSize = 13.sp, color = Color.Gray)
                        Text("$availablePoints pts (₹$availablePoints)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF2E7D32))
                    }
                }

                OutlinedTextField(
                    value = ptsInput,
                    onValueChange = { ptsInput = it },
                    label = { Text(Loc.get("Points to Redeem (Min 100) *", lang)) },
                    placeholder = { Text("e.g. 500") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (ptsInput.isNotBlank()) {
                            if (ptsToRedeem < 100) {
                                Text(Loc.get("Minimum redemption is 100 points.", lang), color = Color.Red)
                            } else if (ptsToRedeem > availablePoints) {
                                Text(Loc.get("Cannot exceed available points balance.", lang), color = Color.Red)
                            } else {
                                Text(Loc.get("You will receive ₹$ptsToRedeem", lang), color = Color(0xFF2E7D32))
                            }
                        }
                    }
                )

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFFF5F9FE),
                    contentColor = Color(0xFF023F97)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("UPI ID", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(Loc.get("Bank Account", lang), fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                }

                if (selectedTab == 0) {
                    OutlinedTextField(
                        value = upiId,
                        onValueChange = { upiId = it },
                        label = { Text(Loc.get("UPI ID *", lang)) },
                        placeholder = { Text("e.g. name@upi") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    OutlinedTextField(
                        value = holderName,
                        onValueChange = { holderName = it },
                        label = { Text(Loc.get("Account Holder Name *", lang)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = { accountNumber = it },
                        label = { Text(Loc.get("Bank Account Number *", lang)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmAccountNumber,
                        onValueChange = { confirmAccountNumber = it },
                        label = { Text(Loc.get("Confirm Account Number *", lang)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            if (accountNumber.isNotBlank() && confirmAccountNumber.isNotBlank() && accountNumber != confirmAccountNumber) {
                                Text(Loc.get("Account numbers do not match.", lang), color = Color.Red)
                            }
                        }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = bankName,
                            onValueChange = { bankName = it },
                            label = { Text(Loc.get("Bank Name *", lang)) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = ifscCode,
                            onValueChange = { ifscCode = it.uppercase() },
                            label = { Text(Loc.get("IFSC Code *", lang)) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(Loc.get("Cancel", lang))
                    }
                    Button(
                        onClick = { onRedeem(ptsToRedeem) },
                        enabled = canRedeem,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF023F97)),
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Text(Loc.get("Redeem Now", lang), color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun KycVerificationDialog(
    viewModel: JobViewModel,
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit
) {
    var panInput by remember { mutableStateOf("") }
    var verifying by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val panRegex = Regex("[A-Z]{5}[0-9]{4}[A-Z]{1}")
    val cleanPan = panInput.trim().uppercase()
    val isValidFormat = panRegex.matches(cleanPan)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = Loc.get("kyc_title", lang),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color(0xFF023F97),
                    modifier = Modifier.align(Alignment.Start)
                )

                Text(
                    text = Loc.get("kyc_dialog_desc", lang),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                OutlinedTextField(
                    value = panInput,
                    onValueChange = { input -> 
                        if (input.length <= 10) {
                            panInput = input.uppercase()
                            errorMessage = null
                        }
                    },
                    label = { Text(Loc.get("kyc_pan_number", lang)) },
                    placeholder = { Text("e.g. ABCDE1234F") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF023F97),
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    supportingText = {
                        if (panInput.isNotBlank() && !isValidFormat) {
                            Text("Format: 5 Letters, 4 Digits, 1 Letter (e.g. ABCDE1234F)", color = Color.Red, fontSize = 10.sp)
                        }
                    }
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !verifying
                    ) {
                        Text(Loc.get("cancel", lang))
                    }
                    
                    Button(
                        onClick = {
                            verifying = true
                            errorMessage = null
                            viewModel.verifyKycPan(
                                panNumber = cleanPan,
                                onSuccess = { name ->
                                    verifying = false
                                    Toast.makeText(context, Loc.get("kyc_success", lang), Toast.LENGTH_LONG).show()
                                    onSuccess(name)
                                },
                                onFailure = { error ->
                                    verifying = false
                                    errorMessage = error
                                }
                            )
                        },
                        enabled = isValidFormat && !verifying,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF023F97)),
                        modifier = Modifier.weight(1.5f)
                    ) {
                        if (verifying) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text(Loc.get("kyc_verify_submit", lang), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubmissionsView(entries: List<JobEntry>, lang: AppLanguage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = Loc.get("recent_entries", lang),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF023F97),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, Color(0xFFDDE1E7), RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = Loc.get("No jobs logged yet", lang),
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(entries) { entry ->
                    EntryItem(entry = entry, lang = lang)
                }
            }
        }
    }
}

@Composable
fun EntryItem(entry: JobEntry, lang: AppLanguage) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(entry.timestamp))
    val isCompressor = entry.componentType == "compressor"
    val themeColor = if (isCompressor) Color(0xFF009FEE) else Color(0xFF023F97)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFDDE1E7), RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${entry.make} ${entry.model}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1A1A1A)
                )
                
                Box(
                    modifier = Modifier
                        .background(themeColor.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = entry.componentType.uppercase(),
                        color = themeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${Loc.get("failure_cause_title", lang)}: ${entry.failureCause} (${entry.severity})",
                fontSize = 13.sp,
                color = Color(0xFF6B6B6B)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                
                val isSynced = entry.syncStatus == "SYNCED"
                Box(
                    modifier = Modifier
                        .background(
                            if (isSynced) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isSynced) Loc.get("synced", lang) else Loc.get("queued", lang),
                        color = if (isSynced) Color(0xFF2E7D32) else Color(0xFFEF6C00),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ReportsView(entries: List<JobEntry>, points: Int, lang: AppLanguage) {
    val condenserCount = entries.count { it.componentType == "condenser" }
    val compressorCount = entries.count { it.componentType == "compressor" }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = Loc.get("Activity Report", lang),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF023F97)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFDDE1E7), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(Loc.get("Logged Component Statistics", lang), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(Loc.get("Condensers Logged", lang), color = Color.Gray)
                    Text("$condenserCount", fontWeight = FontWeight.Bold)
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(Loc.get("Compressors Logged", lang), color = Color.Gray)
                    Text("$compressorCount", fontWeight = FontWeight.Bold)
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val totalEarned = entries.sumOf { entry ->
                        if (entry.componentType == "condenser") {
                            if (entry.brandInstalled?.equals("pranav", ignoreCase = true) == true) 20 else 10
                        } else {
                            if (entry.brandInstalled?.equals("sanden", ignoreCase = true) == true) 20 else 10
                        }
                    }
                    Text(Loc.get("Total Incentives Earned", lang), color = Color.Gray)
                    Text("₹$totalEarned", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
        }
    }
}

@Composable
fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF1A1A1A))
    }
}
