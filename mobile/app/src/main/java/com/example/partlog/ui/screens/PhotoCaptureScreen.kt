package com.example.partlog.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.partlog.ui.AppLanguage
import com.example.partlog.ui.JobViewModel
import com.example.partlog.ui.Loc
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoCaptureScreen(
    viewModel: JobViewModel,
    onNext: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language
    val context = LocalContext.current
    val isCompressor = viewModel.componentType.value == "compressor"
    val themeColor = if (isCompressor) Color(0xFF0D4B46) else Color(0xFF023F97)

    val photo1 by viewModel.photoPath1
    val photo2 by viewModel.photoPath2
    val photo3 by viewModel.photoPath3
    val photo4 by viewModel.photoPath4
    val photo5 by viewModel.photoPath5
    val photo6 by viewModel.photoPath6

    var tempPath1 by remember { mutableStateOf<String?>(null) }
    var tempPath2 by remember { mutableStateOf<String?>(null) }
    var tempPath3 by remember { mutableStateOf<String?>(null) }
    var tempPath4 by remember { mutableStateOf<String?>(null) }
    var tempPath5 by remember { mutableStateOf<String?>(null) }
    var tempPath6 by remember { mutableStateOf<String?>(null) }

    val cameraLauncher1 = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPath1 != null) {
            viewModel.photoPath1.value = tempPath1
            viewModel.captureGpsAndTimestamp()
        }
    }
    val cameraLauncher2 = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPath2 != null) {
            viewModel.photoPath2.value = tempPath2
            viewModel.captureGpsAndTimestamp()
        }
    }
    val cameraLauncher3 = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPath3 != null) {
            viewModel.photoPath3.value = tempPath3
            viewModel.captureGpsAndTimestamp()
        }
    }
    val cameraLauncher4 = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPath4 != null) {
            viewModel.photoPath4.value = tempPath4
            viewModel.captureGpsAndTimestamp()
        }
    }
    val cameraLauncher5 = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPath5 != null) {
            viewModel.photoPath5.value = tempPath5
            viewModel.captureGpsAndTimestamp()
        }
    }
    val cameraLauncher6 = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPath6 != null) {
            viewModel.photoPath6.value = tempPath6
            viewModel.captureGpsAndTimestamp()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (!cameraGranted || !locationGranted) {
            Toast.makeText(context, "Camera and Location permissions are required.", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        val hasCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val hasLoc = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasCamera || !hasLoc) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    fun launchCamera(slotIndex: Int) {
        val hasCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (!hasCamera) {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
            return
        }

        val imagesDir = File(context.getExternalFilesDir(null), "Pictures")
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }
        val file = File(imagesDir, "partlog_img_${slotIndex}_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        when (slotIndex) {
            1 -> {
                tempPath1 = file.absolutePath
                cameraLauncher1.launch(uri)
            }
            2 -> {
                tempPath2 = file.absolutePath
                cameraLauncher2.launch(uri)
            }
            3 -> {
                tempPath3 = file.absolutePath
                cameraLauncher3.launch(uri)
            }
            4 -> {
                tempPath4 = file.absolutePath
                cameraLauncher4.launch(uri)
            }
            5 -> {
                tempPath5 = file.absolutePath
                cameraLauncher5.launch(uri)
            }
            6 -> {
                tempPath6 = file.absolutePath
                cameraLauncher6.launch(uri)
            }
        }
    }

    val labels = if (isCompressor) {
        listOf(
            "Compressor",
            "Compressor Label",
            "Pulley",
            "Oil Leakage",
            "Installation",
            "Engine Bay"
        )
    } else {
        listOf(
            "Front View",
            "Back View",
            "Damaged Area",
            "Vehicle VIN",
            "Old Part Label",
            "Installed New Port"
        )
    }

    val photoPaths = listOf(photo1, photo2, photo3, photo4, photo5, photo6)

    val hasAtLeastOnePhoto = photo1 != null || photo2 != null || photo3 != null ||
                             photo4 != null || photo5 != null || photo6 != null

    Scaffold(
        topBar = {
            LoggingFlowHeader(
                currentStep = if (isCompressor) 2 else 3,
                totalSteps = if (isCompressor) 3 else 5,
                title = if (isCompressor) Loc.get("Log Compressor", lang) else Loc.get("Log Condenser", lang),
                themeColor = themeColor,
                onBack = onCancel
            )
        },
        bottomBar = {
            FlowNextButton(
                text = Loc.get("next", lang),
                enabled = hasAtLeastOnePhoto,
                themeColor = themeColor,
                onClick = onNext
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = Loc.get("Upload Images", lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = if (lang == AppLanguage.EN) {
                        "Capture clear images for accurate verification."
                    } else {
                        "सटीक सत्यापन के लिए स्पष्ट चित्र लें।"
                    },
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Grid of 6 image slots
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (i in 0..2) {
                        Box(modifier = Modifier.weight(1f)) {
                            GridPhotoItem(
                                label = labels[i],
                                filePath = photoPaths[i],
                                themeColor = themeColor,
                                onClick = { launchCamera(i + 1) }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (i in 3..5) {
                        Box(modifier = Modifier.weight(1f)) {
                            GridPhotoItem(
                                label = labels[i],
                                filePath = photoPaths[i],
                                themeColor = themeColor,
                                onClick = { launchCamera(i + 1) }
                            )
                        }
                    }
                }
            }

            // Verification banner card
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFF22C55E), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = Loc.get("All images will be verified", lang),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            fontSize = 13.sp
                        )
                        Text(
                            text = Loc.get("Make sure images are clear and well lit.", lang),
                            color = Color(0xFF166534),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GridPhotoItem(
    label: String,
    filePath: String?,
    themeColor: Color,
    onClick: () -> Unit
) {
    val bitmap = rememberImageBitmap(filePath)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .border(1.dp, if (filePath != null) Color(0xFF22C55E) else Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
            .background(if (filePath != null) Color(0xFFF0FDF4) else Color.White, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (bitmap != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
            ) {
                Image(
                    bitmap = bitmap,
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(0xFF22C55E), CircleShape)
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Uploaded",
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = themeColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (filePath != null) Color(0xFF166534) else Color(0xFF475569),
            textAlign = TextAlign.Center,
            lineHeight = 13.sp,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun rememberImageBitmap(filePath: String?): ImageBitmap? {
    if (filePath == null) return null
    return remember(filePath) {
        try {
            val file = File(filePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                val scaled = android.media.ThumbnailUtils.extractThumbnail(bitmap, 200, 200)
                scaled?.asImageBitmap()
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
