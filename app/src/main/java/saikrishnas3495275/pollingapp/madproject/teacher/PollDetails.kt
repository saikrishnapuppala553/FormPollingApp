package saikrishnas3495275.pollingapp.madproject.teacher

import android.content.Context
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import saikrishnas3495275.pollingapp.madproject.UserPrefs
import saikrishnas3495275.pollingapp.madproject.ui.theme.RoyalBlue
import saikrishnas3495275.pollingapp.madproject.ui.theme.Yellow
import java.text.DecimalFormat

@Composable
fun PollDetailsScreen(pollId: String, navController: NavController) {
    var poll by remember { mutableStateOf<Poll?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showEndDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userEmail = UserPrefs.getEmail(context)
    val database = Firebase.database.reference

    LaunchedEffect(pollId) {
        if (userEmail.isBlank()) {
            errorMessage = "Authentication error."
            isLoading = false
            return@LaunchedEffect
        }
        val safeEmail = userEmail.replace(".", ",")
        val pollRef = database.child("polls").child(safeEmail).child(pollId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                poll = snapshot.getValue(Poll::class.java)
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                errorMessage = "Failed to load poll details: ${error.message}"
                isLoading = false
            }
        }
        pollRef.addValueEventListener(listener)
    }



    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RoyalBlue)
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {


            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Poll Details",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        when {
            isLoading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            errorMessage != null -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text(errorMessage!!, color = Color.Red) }

            poll != null -> {
                poll?.let {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            it.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Category: ${it.category}", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Status: ${it.status.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}",
                            color = if (it.status == "active") Color(0xFF4CAF50) else Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Options & Votes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val totalVotes = it.votes.values.sum()
                        it.options.forEach { option ->
                            val votesForOption = it.votes[option] ?: 0
                            val percentage =
                                if (totalVotes > 0) (votesForOption.toDouble() / totalVotes) * 100 else 0.0
                            VoteProgress(option, votesForOption, percentage)
                            Spacer(Modifier.height(8.dp))
                        }

                        Spacer(Modifier.weight(1f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            if (it.status == "active") {
                                Button(
                                    onClick = { showEndDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Yellow)
                                ) {
                                    Text("End Poll", color = Color.Black)
                                }
                            }
                            Button(
                                onClick = { showDeleteDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Delete Poll")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEndDialog) {
        ConfirmDialog(
            title = "End Poll",
            text = "Are you sure you want to end this poll? This cannot be undone.",
            onConfirm = {
                endPoll(pollId, navController, context)
                showEndDialog = false
            },
            onDismiss = { showEndDialog = false }
        )
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            title = "Delete Poll",
            text = "Are you sure you want to delete this poll? This action is permanent.",
            onConfirm = {
                deletePoll(pollId, navController, context)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
fun VoteProgress(option: String, votes: Int, percentage: Double) {
    val decimalFormat = DecimalFormat("0.0")
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(option, fontWeight = FontWeight.SemiBold)
            Text("$votes votes (${decimalFormat.format(percentage)}%)")
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.LightGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (percentage / 100).toFloat())
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(RoyalBlue)
            )
        }
    }
}

@Composable
fun ConfirmDialog(title: String, text: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Cancel")
            }
        }
    )
}

fun endPoll(pollId: String, navController: NavController, context: Context) {
    val safeEmail = UserPrefs.getEmail(context).replace(".", ",")
    if (safeEmail.isBlank()) return
    Firebase.database.reference.child("polls").child(safeEmail).child(pollId).child("status")
        .setValue("ended")
        .addOnSuccessListener { navController.popBackStack() }
}

fun deletePoll(pollId: String, navController: NavController, context: Context) {
    val safeEmail = UserPrefs.getEmail(context).replace(".", ",")
    if (safeEmail.isBlank()) return
    Firebase.database.reference.child("polls").child(safeEmail).child(pollId).removeValue()
        .addOnSuccessListener { navController.popBackStack() }
}

//@Preview(showBackground = true)
//@Composable
//fun PollDetailsScreenPreview() {
//    MaterialTheme {
//        PollDetailsScreen(pollId = "preview_poll_id", navController = rememberNavController())
//    }
//}