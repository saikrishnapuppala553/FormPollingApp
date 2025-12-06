package saikrishnas3495275.pollingapp.madproject.teacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlin.random.Random

// This preview uses the Poll data class defined in CreatePoll.kt
@Preview(showBackground = true)
@Composable
fun PollDetailsScreenPreview() {

    val pollData = Poll(
        id = "1",
        name = "What is your opinion on topic 1?",
        category = listOf("General", "Study", "Sports", "Technology").random(),
        createdDate = "2025-11-30",
        endDate = "2025-12-10",
        options = listOf("Option A", "Option B", "Option C", "Option D"),
        votes = mapOf(
            "Option A" to Random.nextInt(10, 200),
            "Option B" to Random.nextInt(10, 200),
            "Option C" to Random.nextInt(10, 200),
            "Option D" to Random.nextInt(10, 200)
        )
    )

    ViewPollDetailsScreen(pollData, navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPollDetailsScreen(poll: Poll, navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Poll Details1") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF303F9F)),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                poll.name,
                style = typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("Category: ${poll.category}", fontSize = 15.sp, color = Color.Gray)
            Text("Posted: ${poll.createdDate}", fontSize = 15.sp, color = Color.Gray)
            Text("Ends: ${poll.endDate}", fontSize = 15.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Options & Vote Percentage",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            val totalVotes = poll.votes.values.sum()

            poll.options.forEach { option ->
                val votesForOption = poll.votes[option] ?: 0
                val percentage =
                    if (totalVotes == 0) 0f else votesForOption.toFloat() / totalVotes

                OptionResultItem(
                    optionText = option,
                    votes = votesForOption,
                    percentage = percentage
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun OptionResultItem(optionText: String, votes: Int, percentage: Float) {

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(optionText, fontWeight = FontWeight.SemiBold)
            Text("${(percentage * 100).toInt()}%", fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50)),
            color = Color(0xFF3F51B5)
        )

        Text("$votes votes", color = Color.Gray, fontSize = 13.sp)
    }
}
