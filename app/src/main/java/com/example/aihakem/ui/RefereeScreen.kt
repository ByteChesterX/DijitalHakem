package com.example.aihakem.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aihakem.R
import com.example.aihakem.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

// Ubuntu Font Tanımı (resId açıkça belirtildi)
val UbuntuFontFamily = FontFamily(
    Font(resId = R.font.ubuntu_regular, weight = FontWeight.Normal),
    Font(resId = R.font.ubuntu_bold, weight = FontWeight.Bold)
)

@Composable
fun RefereeScreen(viewModel: RefereeViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    var isListening by remember { mutableStateOf(false) }
    var activeSpeaker by remember { mutableStateOf(1) } // 1: Kişi 1, 2: Kişi 2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GruvboxBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Üst Başlık
        Text(
            text = "DijitalHakem",
            color = GruvboxYellow,
            fontSize = 28.sp,
            fontFamily = UbuntuFontFamily,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 32.dp)
        )

        // Orta Kısım: Buton ve Ses Çubukları
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(280.dp)
        ) {
            if (isListening) {
                WaveformBars(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            // Ortadaki Büyük Minimal Buton
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(if (isListening) GruvboxOrange else GruvboxGreen)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isListening = !isListening
                        if (!isListening) {
                            activeSpeaker = if (activeSpeaker == 1) 2 else 1
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(GruvboxBg)
                )
            }
        }

        // Alt Kısım: Konuşmacı Bilgisi Kartı
        SpeakerStatusCard(activeSpeaker = activeSpeaker, isListening = isListening)
    }
}

@Composable
fun WaveformBars(modifier: Modifier = Modifier) {
    val barHeights = remember { mutableStateListOf(0.3f, 0.6f, 0.9f, 0.5f, 0.8f, 0.4f, 0.7f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(120)
            for (i in barHeights.indices) {
                barHeights[i] = Random.nextDouble(0.2, 1.0).toFloat()
            }
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        barHeights.forEach { heightFactor ->
            val animatedHeight by animateFloatAsState(
                targetValue = heightFactor,
                animationSpec = tween(durationMillis = 100),
                label = "bar_height"
            )

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight(animatedHeight)
                    .clip(RoundedCornerShape(3.dp))
                    .background(GruvboxGreen)
            )
        }
    }
}

@Composable
fun SpeakerStatusCard(activeSpeaker: Int, isListening: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GruvboxBgSoft)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpeakerItem(
            name = "Kişi 1",
            isActive = activeSpeaker == 1,
            isListening = isListening && activeSpeaker == 1
        )
        SpeakerItem(
            name = "Kişi 2",
            isActive = activeSpeaker == 2,
            isListening = isListening && activeSpeaker == 2
        )
    }
}

@Composable
fun SpeakerItem(name: String, isActive: Boolean, isListening: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = name,
            color = if (isActive) GruvboxYellow else GruvboxGray,
            fontSize = 18.sp,
            fontFamily = UbuntuFontFamily,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (isListening) "Konuşuyor..." else if (isActive) "Sıra Sende" else "Bekliyor",
            color = if (isListening) GruvboxGreen else GruvboxFg,
            fontSize = 13.sp,
            fontFamily = UbuntuFontFamily
        )
    }
}
