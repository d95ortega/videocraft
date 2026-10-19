package com.example.ui.credits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CreditsAndPlansScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(14.dp)
            .testTag("credits_and_plans_screen")
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Créditos & Planes de Producción",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "Transparencia de consumo por modelo, duración y resolución",
            color = TextSecondaryDark,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, BrightAmber.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("BALANCE DISPONIBLE", color = TextSecondaryDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("240", color = BrightAmber, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Créditos de Demostración", color = Color.White, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Plan Actual: Creador Pro • Renovación mensual incluida para desarrollo",
                    color = TextSecondaryDark,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Coste Estimado por Tipo de Generación:",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        val rates = listOf(
            Triple("Guion Completo con Gemini IA", "1 Crédito", "Planificación, logline, sinopsis y 5 escenas"),
            Triple("Plano de Video Corto (4s)", "4 Créditos", "Renderizado con estilo y movimiento de cámara"),
            Triple("Tráiler Completo (40s)", "18 Créditos", "Secuencia de 4 planos con dirección cinematográfica"),
            Triple("Película Completa (1.5 min)", "35 Créditos", "6 actos, diálogos, atmósfera y banda sonora")
        )

        rates.forEach { (feature, cost, detail) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, StudioCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(feature, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(detail, color = TextSecondaryDark, fontSize = 10.sp)
                    }
                    Surface(color = CoralPink.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                        Text(cost, color = CoralPink, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }
}
