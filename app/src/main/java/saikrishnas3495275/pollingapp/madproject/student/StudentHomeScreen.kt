package saikrishnas3495275.pollingapp.madproject.student

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import saikrishnas3495275.pollingapp.madproject.LoginActivity
import saikrishnas3495275.pollingapp.madproject.UserPrefs
import saikrishnas3495275.pollingapp.madproject.teacher.Poll
import saikrishnas3495275.pollingapp.madproject.ui.theme.RoyalBlue
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen() {
    var polls by remember { mutableStateOf<List<Poll>>(emptyList()) }
    var votedPolls by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectedCategory by remember { mutableStateOf("All") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentView by remember { mutableStateOf("active") } // "active" or "voted"

    val context = LocalContext.current
    val userEmail = UserPrefs.getEmail(context)
    val database = Firebase.database.reference

    LaunchedEffect(userEmail) {
        if (userEmail.isBlank()) {
            errorMessage = "Please log in to view polls."
            isLoading = false
            return@LaunchedEffect
        }

        val pollsRef = database.child("polls")
        val sanitizedEmail = userEmail.replace(".", ",")
        val userVotesRef = database.child("user_votes").child(sanitizedEmail)

        val pollsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pollList = mutableListOf<Poll>()
                snapshot.children.forEach { teacherSnapshot ->
                    teacherSnapshot.children.forEach { pollSnapshot ->
                        pollSnapshot.getValue(Poll::class.java)?.let { poll ->
                            if (poll.status == "active") {
                                pollList.add(poll)
                            }
                        }
                    }
                }
                polls = pollList.sortedByDescending { it.createdDate }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                errorMessage = "Failed to load polls: ${error.message}"
                isLoading = false
            }
        }

        val votesListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pollIds = snapshot.children.mapNotNull { it.key }.toSet()
                votedPolls = pollIds
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }

        pollsRef.addValueEventListener(pollsListener)
        userVotesRef.addValueEventListener(votesListener)
    }

    val categories = listOf("All") + polls.map { it.category }.distinct()
    val filteredPolls = when (currentView) {
        "active" -> polls.filter { !votedPolls.contains(it.id) && (selectedCategory == "All" || it.category == selectedCategory) }
        "voted" -> polls.filter { votedPolls.contains(it.id) }
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Student Home",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RoyalBlue,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = { // Add actions block for trailing icons
                    IconButton(onClick = {
                        val intent = Intent(context, ProfileActivity::class.java)
                        context.startActivity(intent)

                    }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onPrimary // Ensure the icon color matches title
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
//            Header()
            TabRow(selectedTabIndex = if (currentView == "active") 0 else 1) {
                Tab(
                    selected = currentView == "active",
                    onClick = { currentView = "active" },
                    text = { Text("Active Polls") })
                Tab(
                    selected = currentView == "voted",
                    onClick = { currentView = "voted" },
                    text = { Text("Voted Polls") })
            }

            if (currentView == "active") {
                CategoryFilter(categories, selectedCategory) { selectedCategory = it }
            }

            when {
                isLoading -> CenteredCircularProgress()
                errorMessage != null -> CenteredText(errorMessage!!, color = Color.Red)
                filteredPolls.isEmpty() -> CenteredText(if (currentView == "active") "No active polls available." else "You haven't voted in any polls yet.")
                else -> PollList(polls = filteredPolls, votedPolls = votedPolls)
            }
        }
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RoyalBlue)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Student Dashboard",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilter(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Filter by Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun PollList(polls: List<Poll>, votedPolls: Set<String>) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(polls) { poll ->
            PollItem(poll = poll, hasVoted = votedPolls.contains(poll.id))
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PollItem(poll: Poll, hasVoted: Boolean) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                poll.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Category: ${poll.category}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (hasVoted) {
                PollResults(poll)
            } else {
                poll.options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption = option }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = { selectedOption = option }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        selectedOption?.let {
                            submitVote(poll, it, context)
                        } ?: Toast.makeText(context, "Please select an option.", Toast.LENGTH_SHORT)
                            .show()
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = selectedOption != null
                ) {
                    Text("Submit Vote")
                }
            }
        }
    }
}

@Composable
fun PollResults(poll: Poll) {
    val totalVotes = poll.votes.values.sum()
    val decimalFormat = DecimalFormat("0.0")

    Column {
        Text("Results:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        poll.options.forEach { option ->
            val votes = poll.votes[option] ?: 0
            val percentage = if (totalVotes > 0) (votes.toDouble() / totalVotes) * 100 else 0.0
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(option)
                Text("${decimalFormat.format(percentage)}% ($votes votes)")
            }
            LinearProgressIndicator(
                progress = { (percentage / 100).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(vertical = 2.dp)
            )
        }
    }
}

fun submitVote(poll: Poll, option: String, context: Context) {
    val userEmail = UserPrefs.getEmail(context)
    if (userEmail.isBlank()) {
        Toast.makeText(context, "You must be logged in to vote.", Toast.LENGTH_SHORT).show()
        return
    }

    val database = Firebase.database.reference
    val pollCreatorSafeEmail = poll.createdBy.replace(".", ",")
    val pollRef = database.child("polls").child(pollCreatorSafeEmail).child(poll.id)

    pollRef.runTransaction(object : Transaction.Handler {
        override fun doTransaction(currentData: MutableData): Transaction.Result {
            val p =
                currentData.getValue(Poll::class.java) ?: return Transaction.success(currentData)
            val updatedVotes = p.votes.toMutableMap()
            updatedVotes[option] = (updatedVotes[option] ?: 0) + 1
            p.copy(votes = updatedVotes).also {
                currentData.value = it
            }
            return Transaction.success(currentData)
        }

        override fun onComplete(
            error: DatabaseError?,
            committed: Boolean,
            currentData: DataSnapshot?
        ) {
            if (committed) {
                val sanitizedEmail = userEmail.replace(".", ",")
                database.child("user_votes").child(sanitizedEmail).child(poll.id).setValue(true)
                Toast.makeText(context, "Vote submitted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to submit vote.", Toast.LENGTH_SHORT).show()
            }
        }
    })
}


@Composable
fun CenteredCircularProgress() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun CenteredText(text: String, color: Color = Color.Black) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, color = color, textAlign = TextAlign.Center, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun StudentHomeScreenPreview() {
    MaterialTheme {
        StudentHomeScreen()
    }
}