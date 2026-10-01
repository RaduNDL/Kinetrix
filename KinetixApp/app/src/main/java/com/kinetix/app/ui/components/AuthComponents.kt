package com.kinetix.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetix.app.ui.theme.KinetixBackground
import com.kinetix.app.ui.theme.KinetixLime
import com.kinetix.app.ui.theme.KinetixMuted
import com.kinetix.app.ui.theme.KinetixOutline
import com.kinetix.app.ui.theme.KinetixSurface

@Composable
fun AuthBackground(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF13231B),
                        KinetixBackground,
                        Color(0xFF09120F)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = 72.dp,
                    end = 28.dp
                )
                .size(170.dp)
                .background(
                    color = KinetixLime.copy(alpha = 0.045f),
                    shape = CircleShape
                )
        )

        content()
    }
}

@Composable
fun KinetixBrand() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(16.dp),
            color = KinetixLime
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.FitnessCenter,
                    contentDescription = null,
                    tint = KinetixBackground,
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = "KINETIX",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Text(
                text = "MOVE WITH PURPOSE",
                color = KinetixMuted,
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation =
        VisualTransformation.None,
    trailingIcon: (@Composable (() -> Unit))? = null,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    placeholder: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        placeholder = placeholder?.let { placeholderText ->
            {
                Text(
                    text = placeholderText,
                    color = KinetixMuted
                )
            }
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = KinetixMuted
            )
        },
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        readOnly = readOnly,
        singleLine = singleLine,
        shape = RoundedCornerShape(15.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor =
                MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor =
                MaterialTheme.colorScheme.onSurface,
            focusedContainerColor =
                KinetixSurface.copy(alpha = 0.82f),
            unfocusedContainerColor =
                KinetixSurface.copy(alpha = 0.72f),
            cursorColor = KinetixLime,
            focusedBorderColor = KinetixLime,
            unfocusedBorderColor = KinetixOutline,
            focusedLabelColor = KinetixLime,
            unfocusedLabelColor = KinetixMuted
        )
    )
}

@Composable
fun AuthPrimaryButton(
    text: String,
    loading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = KinetixLime,
            contentColor = KinetixBackground,
            disabledContainerColor =
                KinetixLime.copy(alpha = 0.42f),
            disabledContentColor =
                KinetixBackground.copy(alpha = 0.65f)
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = KinetixBackground,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AuthMessage(
    text: String,
    isError: Boolean
) {
    Surface(
        color = if (isError) {
            Color(0xFF3A1D1C)
        } else {
            Color(0xFF18332B)
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            if (isError) {
                Icon(
                    imageVector = Icons.Filled.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }

            Text(
                text = text,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.secondary
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun AuthTitle(
    title: String,
    subtitle: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = KinetixMuted
        )
    }
}

@Composable
fun FieldGap() {
    Spacer(
        modifier = Modifier.height(13.dp)
    )
}