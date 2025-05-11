package com.example.todo.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val PrimaryColor = Color(0xFF1A73E8)
val PrimaryVariantLight = Color(0xFF5EA1EF)
val PrimaryVariantDark = Color(0xFF0D47A1)

val SecondaryColor = Color(0xFF4CAF50)
val SecondaryVariantLight = Color(0xFF80E27E)

val NewTaskColor = Color(0xFF1A3764)
val InProgressTaskColor = Color(0xFF1A3E1E)
val CompletedTaskColor = Color(0xFF383838)
val DeletedTaskColor = Color(0xFF5A2326)


val DarkColorPalette = darkColors(
    primary = PrimaryVariantLight,
    primaryVariant = PrimaryColor,
    secondary = SecondaryVariantLight,
    secondaryVariant = SecondaryColor,
    background = Color(0xFF202124),
    surface = Color(0xFF303134),
    error = Color(0xFFEF9A9A),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

val TodoTypography = Typography(
    h5 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        letterSpacing = 0.1.sp
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.4.sp
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    button = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 1.sp
    ),
    caption = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    )
)

@Composable
fun TodoAppTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = DarkColorPalette

    MaterialTheme(
        colors = colors,
        typography = TodoTypography,
        content = content
    )
} 