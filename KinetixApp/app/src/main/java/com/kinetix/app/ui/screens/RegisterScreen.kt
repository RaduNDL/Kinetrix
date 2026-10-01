package com.kinetix.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kinetix.app.data.models.RegisterRequest
import com.kinetix.app.ui.components.AuthBackground
import com.kinetix.app.ui.components.AuthField
import com.kinetix.app.ui.components.AuthMessage
import com.kinetix.app.ui.components.AuthPrimaryButton
import com.kinetix.app.ui.components.AuthTitle
import com.kinetix.app.ui.components.FieldGap
import com.kinetix.app.ui.components.KinetixBrand
import com.kinetix.app.ui.theme.KinetixMuted
import com.kinetix.app.ui.theme.KinetixSurface
import com.kinetix.app.ui.viewmodels.AuthViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Prefer not to say") }

    var genderExpanded by remember { mutableStateOf(false) }
    var datePickerVisible by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    val clearMessages: () -> Unit = {
        formError = null
        authViewModel.resetState()
    }

    val fieldsComplete =
        firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                dateOfBirth.isNotBlank() &&
                height.isNotBlank()

    LaunchedEffect(authViewModel.isRegistrationCompleted) {
        if (authViewModel.isRegistrationCompleted) {
            authViewModel.resetState()

            navController.navigate("login") {
                popUpTo("register") {
                    inclusive = true
                }

                launchSingleTop = true
            }
        }
    }

    AuthBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 20.dp,
                    vertical = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KinetixBrand()

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Text(
                text = "CREATE YOUR ACCOUNT",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "Create your profile",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "Complete your details so we can personalize your training experience.",
                color = KinetixMuted,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = KinetixSurface.copy(alpha = 0.97f)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 12.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 22.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    AuthTitle(
                        "Let's get started",
                        "Your information is used only to create and personalize your account."
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AuthField(
                            value = firstName,
                            onValueChange = {
                                firstName = it
                                clearMessages()
                            },
                            label = "First name",
                            leadingIcon = Icons.Filled.Person,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )

                        AuthField(
                            value = lastName,
                            onValueChange = {
                                lastName = it
                                clearMessages()
                            },
                            label = "Last name",
                            leadingIcon = Icons.Filled.Person,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )
                    }

                    FieldGap()

                    AuthField(
                        value = email,
                        onValueChange = {
                            email = it
                            clearMessages()
                        },
                        label = "Email address",
                        leadingIcon = Icons.Filled.Email,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    Text(
                        text = "Use an email address that you can access. We will send a welcome email.",
                        color = KinetixMuted,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(
                            start = 14.dp,
                            top = 3.dp,
                            end = 8.dp
                        )
                    )

                    FieldGap()

                    AuthField(
                        value = password,
                        onValueChange = {
                            password = it
                            clearMessages()
                        },
                        label = "Password",
                        leadingIcon = Icons.Filled.Lock,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                }
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Filled.VisibilityOff
                                    } else {
                                        Icons.Filled.Visibility
                                    },
                                    contentDescription = if (passwordVisible) {
                                        "Hide password"
                                    } else {
                                        "Show password"
                                    },
                                    tint = KinetixMuted
                                )
                            }
                        }
                    )

                    Text(
                        text = "Minimum 10 characters.",
                        color = KinetixMuted,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(
                            start = 14.dp,
                            top = 3.dp
                        )
                    )

                    FieldGap()

                    AuthField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            clearMessages()
                        },
                        label = "Confirm password",
                        leadingIcon = Icons.Filled.Lock,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        visualTransformation = if (confirmPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    confirmPasswordVisible =
                                        !confirmPasswordVisible
                                }
                            ) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) {
                                        Icons.Filled.VisibilityOff
                                    } else {
                                        Icons.Filled.Visibility
                                    },
                                    contentDescription = if (confirmPasswordVisible) {
                                        "Hide confirmation password"
                                    } else {
                                        "Show confirmation password"
                                    },
                                    tint = KinetixMuted
                                )
                            }
                        }
                    )

                    FieldGap()

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AuthField(
                            value = dateOfBirth,
                            onValueChange = {},
                            label = "Date of birth",
                            leadingIcon = Icons.Filled.CalendarToday,
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    datePickerVisible = true
                                },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        datePickerVisible = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CalendarToday,
                                        contentDescription = "Choose date of birth",
                                        tint = KinetixMuted
                                    )
                                }
                            }
                        )
                    }

                    Text(
                        text = if (dateOfBirth.isBlank()) {
                            "Tap the calendar to select your date of birth."
                        } else {
                            "Selected date: $dateOfBirth"
                        },
                        color = KinetixMuted,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(
                            start = 14.dp,
                            top = 3.dp
                        )
                    )

                    FieldGap()

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AuthField(
                            value = gender,
                            onValueChange = {},
                            label = "Gender",
                            leadingIcon = Icons.Filled.Wc,
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    genderExpanded = true
                                },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        genderExpanded = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowDown,
                                        contentDescription = "Choose gender",
                                        tint = KinetixMuted
                                    )
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = {
                                genderExpanded = false
                            }
                        ) {
                            listOf(
                                "Female",
                                "Male",
                                "Non-binary",
                                "Prefer not to say"
                            ).forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(text = option)
                                    },
                                    onClick = {
                                        gender = option
                                        genderExpanded = false
                                        clearMessages()
                                    }
                                )
                            }
                        }
                    }

                    FieldGap()

                    AuthField(
                        value = height,
                        onValueChange = { input ->
                            height = input.filter { character ->
                                character.isDigit() ||
                                        character == '.' ||
                                        character == ','
                            }

                            clearMessages()
                        },
                        label = "Height in centimetres",
                        leadingIcon = Icons.Filled.Height,
                        placeholder = "Example: 175",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        )
                    )

                    Text(
                        text = "Enter a value between 80 and 250 cm.",
                        color = KinetixMuted,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(
                            start = 14.dp,
                            top = 3.dp
                        )
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    formError?.let { message ->
                        AuthMessage(
                            text = message,
                            isError = true
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                    }

                    authViewModel.errorMessage?.let { message ->
                        AuthMessage(
                            text = message,
                            isError = true
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                    }

                    authViewModel.infoMessage?.let { message ->
                        AuthMessage(
                            text = message,
                            isError = false
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    AuthPrimaryButton(
                        text = "Create account",
                        loading = authViewModel.isLoading,
                        enabled = fieldsComplete
                    ) {
                        val validationError = validateForm(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword,
                            dateOfBirth = dateOfBirth,
                            height = height
                        )

                        formError = validationError

                        if (validationError == null) {
                            val parsedHeight = height
                                .replace(',', '.')
                                .toDouble()

                            authViewModel.register(
                                RegisterRequest(
                                    email = email
                                        .trim()
                                        .lowercase(),
                                    password = password,
                                    firstName = firstName.trim(),
                                    lastName = lastName.trim(),
                                    dateOfBirth = dateOfBirth.trim(),
                                    gender = gender,
                                    heightCm = parsedHeight
                                )
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an account? ",
                            color = KinetixMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Sign in",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }

    if (datePickerVisible) {
        DateOfBirthPickerDialog(
            currentValue = dateOfBirth,
            onDateSelected = { selectedDate ->
                dateOfBirth = selectedDate
                datePickerVisible = false
                clearMessages()
            },
            onDismiss = {
                datePickerVisible = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateOfBirthPickerDialog(
    currentValue: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val today = remember {
        LocalDate.now()
    }

    val dateFormatter = remember {
        DateTimeFormatter.ISO_LOCAL_DATE
    }

    val initialDate = remember(currentValue) {
        runCatching {
            LocalDate.parse(
                currentValue,
                dateFormatter
            )
        }.getOrNull()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate?.toUtcMillis(),
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(
                utcTimeMillis: Long
            ): Boolean {
                val selectedDate = utcTimeMillis.toLocalDateUtc()

                return !selectedDate.isAfter(today)
            }

            override fun isSelectableYear(
                year: Int
            ): Boolean {
                return year <= today.year
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedDate = datePickerState
                        .selectedDateMillis
                        ?.toLocalDateUtc()

                    if (
                        selectedDate != null &&
                        !selectedDate.isAfter(today)
                    ) {
                        onDateSelected(
                            selectedDate.format(dateFormatter)
                        )
                    }
                },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text(text = "Select")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    text = "Choose your date of birth",
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 16.dp
                    )
                )
            },
            headline = {
                Text(
                    text = "This helps us personalize your experience.",
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = KinetixMuted
                )
            }
        )
    }
}

private fun validateForm(
    firstName: String,
    lastName: String,
    email: String,
    password: String,
    confirmPassword: String,
    dateOfBirth: String,
    height: String
): String? {
    if (firstName.isBlank()) {
        return "Enter your first name."
    }

    if (lastName.isBlank()) {
        return "Enter your last name."
    }

    val normalizedEmail = email
        .trim()
        .lowercase()

    if (
        !android.util.Patterns.EMAIL_ADDRESS
            .matcher(normalizedEmail)
            .matches()
    ) {
        return "Enter a valid email address."
    }

    if (password.length < 10) {
        return "Your password must contain at least 10 characters."
    }

    if (password != confirmPassword) {
        return "The passwords do not match."
    }

    val birthDate = try {
        LocalDate.parse(dateOfBirth.trim())
    } catch (_: DateTimeParseException) {
        return "Choose your date of birth from the calendar."
    }

    if (birthDate.isAfter(LocalDate.now())) {
        return "Date of birth cannot be in the future."
    }

    val parsedHeight = height
        .replace(',', '.')
        .toDoubleOrNull()
        ?: return "Enter your height in centimetres."

    if (parsedHeight !in 80.0..250.0) {
        return "Height must be between 80 and 250 cm."
    }

    return null
}

private fun LocalDate.toUtcMillis(): Long {
    return atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()
}

private fun Long.toLocalDateUtc(): LocalDate {
    return Instant
        .ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
}