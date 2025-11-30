package saikrishnas3495275.pollingapp.madproject.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import saikrishnas3495275.pollingapp.madproject.ui.theme.RoyalBlue
import kotlin.random.Random

data class PollOption(
    val option: String,
    val votes: Int
)

data class Poll(
    val id: Int,
    val question: String,
    val category: String,
    val postedDate: String,
    val endDate: String,
    val options: List<PollOption>
) {
    val totalVotes: Int
        get() = options.sumOf { it.votes }
}


fun generateDummyPolls(): List<Poll> {
    return (1..10).map { id ->
        Poll(
            id = id,
            question = "What is your opinion on topic $id?",
            category = listOf("General", "Study", "Sports", "Technology").random(),
            postedDate = "2025-11-30",
            endDate = "2025-12-${10 + id}",
            options = listOf(
                PollOption("Option A", Random.nextInt(10, 200)),
                PollOption("Option B", Random.nextInt(10, 200)),
                PollOption("Option C", Random.nextInt(10, 200)),
                PollOption("Option D", Random.nextInt(10, 200)),
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ManagePollPreview() {
    ManagePoll(navController = NavHostController(LocalContext.current))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePoll(navController: NavController) {

    val pollList = remember { generateDummyPolls() }

    Column(
        modifier = Modifier
            .fillMaxSize()
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RoyalBlue)
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        )
        {
            Text(
                text = "Manage Poll",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .padding(top = 12.dp)
        ) {
            items(pollList.size) { index ->
                PollCard(
                    poll = pollList[index],
                    onViewDetails = {
                        navController.navigate("poll_details/${pollList[index].id}")
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


@Composable
fun PollCard(poll: Poll, onViewDetails: () -> Unit) {

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F6FA)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                poll.question,
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Category: ${poll.category}", fontSize = 14.sp, color = Color.Gray)
            Text("Posted on: ${poll.postedDate}", fontSize = 14.sp, color = Color.Gray)
            Text("End Date: ${poll.endDate}", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Total Votes: ${poll.totalVotes}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF1A237E)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onViewDetails,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("View Details")
            }
        }
    }
}
