package saikrishnas3495275.pollingapp.madproject.teacher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePollScreen(
    onSubmitPoll: (question: String, options: List<String>, duration: String) -> Unit = { _, _, _ -> }
) {
    var pollQuestion by remember { mutableStateOf("") }

    val options = remember { mutableStateListOf("Option 1", "Option 2") }

    var selectedDuration by remember { mutableStateOf("24 Hours") }
    var isDurationDropdownExpanded by remember { mutableStateOf(false) }

    val durationOptions = listOf("1 Hour", "3 Hours", "12 Hours", "24 Hours", "7 Days")

    val isSubmitEnabled = pollQuestion.isNotBlank() && options.all { it.isNotBlank() } && options.size >= 2

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create New Poll", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Poll Question Input
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "The Question",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = pollQuestion,
                        onValueChange = { pollQuestion = it },
                        label = { Text("Enter your poll question") },
                        placeholder = { Text("e.g., Which topic do you find most confusing?") },
                        leadingIcon = { Icon(Icons.Default.Menu, contentDescription = "Question") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 2. Answer Options Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Answer Options (${options.size} minimum 2)",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Dynamically generated option fields
                    options.forEachIndexed { index, optionText ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = optionText,
                                onValueChange = { options[index] = it },
                                label = { Text("Option ${index + 1}") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            // Remove button (shown only if more than 2 options exist)
                            AnimatedVisibility(
                                visible = options.size > 2,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                IconButton(
                                    onClick = { options.removeAt(index) },
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove option", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }

                    // Button to add a new option
                    ElevatedButton(
                        onClick = { options.add("") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = options.size < 8 // Limit options to 8 for reasonable design
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add option")
                        Spacer(Modifier.width(8.dp))
                        Text("Add Option")
                    }
                }
            }

            // 3. Poll Duration Dropdown
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Poll Duration",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    // ExposedDropdownMenuBox is the M3 standard for dropdowns
                    ExposedDropdownMenuBox(
                        expanded = isDurationDropdownExpanded,
                        onExpandedChange = { isDurationDropdownExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            value = selectedDuration,
                            onValueChange = { },
                            label = { Text("Voting ends after...") },
                            leadingIcon = { Icon(Icons.Default.MoreVert, contentDescription = "Duration") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDurationDropdownExpanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = isDurationDropdownExpanded,
                            onDismissRequest = { isDurationDropdownExpanded = false }
                        ) {
                            durationOptions.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        selectedDuration = selectionOption
                                        isDurationDropdownExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Submission Button
            Button(
                onClick = { onSubmitPoll(pollQuestion, options.toList(), selectedDuration) },
                enabled = isSubmitEnabled,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(56.dp)
            ) {
                Text(
                    text = "Post Poll",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun CreatePollScreenPreview() {
    MaterialTheme {
        CreatePollScreen()
    }
}