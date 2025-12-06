package saikrishnas3495275.pollingapp.madproject.teacher

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import saikrishnas3495275.pollingapp.madproject.UserPrefs
import saikrishnas3495275.pollingapp.madproject.ui.theme.RoyalBlue
import saikrishnas3495275.pollingapp.madproject.ui.theme.Yellow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class Poll(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val options: List<String> = emptyList(),
    val endDate: String = "",
    val createdDate: String = "",
    val createdBy: String = "",
    val status: String = "active",
    val votes: Map<String, Int> = emptyMap()
)

@Composable
fun CreatePollScreen() {
    var pollName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Select Category") }
    val options = remember { mutableStateListOf("", "", "", "") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val context = LocalContext.current

    val clearFields = {
        pollName = ""
        selectedCategory = "Select Category"
        options.clear()
        options.addAll(listOf("", "", "", ""))
        selectedDate = null
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RoyalBlue)
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            Text(
                text = "Create Poll",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onPrimary // Ensure the icon color matches title
                )
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Poll Name",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 12.dp)

        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = pollName,
            onValueChange = { pollName = it },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            )
        )


        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Category",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 12.dp)

        )

        Spacer(modifier = Modifier.height(12.dp))

        Dropdown(selectedCategory) { selectedCategory = it }


        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Options",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 12.dp)

        )

        Spacer(modifier = Modifier.height(8.dp))

        options.forEachIndexed { index, option ->
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                value = option,
                onValueChange = { options[index] = it },
                placeholder = { Text("Option ${index + 1}") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }


        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Poll Duration",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 12.dp)

        )

        Spacer(modifier = Modifier.height(8.dp))

        IconTriggeredDatePicker(selectedDate) { selectedDate = it }


        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    postPoll(
                        pollName,
                        selectedCategory,
                        options.filter { it.isNotBlank() },
                        selectedDate,
                        context,
                        onSuccess = clearFields
                    )
                },
                modifier = Modifier
                    .width(140.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Yellow,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Text(
                    text = "Post",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun postPoll(
    pollName: String,
    category: String,
    options: List<String>,
    endDate: LocalDate?,
    context: Context,
    onSuccess: () -> Unit
) {
    val userEmail = UserPrefs.getEmail(context)
    if (userEmail.isBlank()) {
        Toast.makeText(context, "You must be logged in to create a poll.", Toast.LENGTH_SHORT).show()
        return
    }

    if (pollName.isBlank() || category == "Select Category" || options.size < 2 || endDate == null) {
        Toast.makeText(context, "Please fill all fields and provide at least two options.", Toast.LENGTH_LONG).show()
        return
    }

    if (endDate.isBefore(LocalDate.now())) {
        Toast.makeText(context, "End date must be in the future.", Toast.LENGTH_SHORT).show()
        return
    }

    val database = Firebase.database.reference
    val safeEmail = userEmail.replace(".", ",")
    val pollId = database.child("polls").child(safeEmail).push().key ?: ""

    val poll = Poll(
        id = pollId,
        name = pollName,
        category = category,
        options = options,
        endDate = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
        createdDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
        createdBy = userEmail,
        status = "active",
        votes = options.associateWith { 0 }
    )

    database.child("polls").child(safeEmail).child(pollId).setValue(poll)
        .addOnSuccessListener {
            Toast.makeText(context, "Poll posted successfully!", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to post poll: ${it.message}", Toast.LENGTH_LONG).show()
        }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val options = listOf(
        "Course Feedback", "Module Feedback", "Lecture Understanding", "Assignment Help",
        "Exam Preparation", "Project Topics", "Class Scheduling", "Lab Session Feedback",
        "Workshop Feedback", "Upcoming Tests", "Study Resources", "Career Guidance",
        "Placement Information", "Student Wellbeing", "Group Activities", "Research Interests",
        "Feedback on Teaching Style", "Online/Offline Class Preference", "Event Participation",
        "General Announcements"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconTriggeredDatePicker(selectedDate: LocalDate?, onDateSelected: (LocalDate?) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
            onDateSelected(date)
            showDatePicker = false
        }
    }

    OutlinedTextField(
        value = selectedDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) ?: "",
        onValueChange = {},
        placeholder = { Text("Select Date") },
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Pick Date",
                modifier = Modifier.clickable { showDatePicker = true }
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,

            ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),

        )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {},
            dismissButton = {}
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CreatePollScreenPreview() {
    MaterialTheme {
        CreatePollScreen()
    }
}