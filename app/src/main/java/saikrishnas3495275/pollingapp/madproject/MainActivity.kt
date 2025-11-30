package saikrishnas3495275.pollingapp.madproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import saikrishnas3495275.pollingapp.madproject.ui.theme.FormPollingAppTheme
import kotlin.jvm.java

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FormPollingAppTheme {
                LoadingScreenCheck(::isUserLoggedIn)
            }
        }
    }

    private fun isUserLoggedIn(value: Int) {

        when (value) {
            1->{
                gotoHomeActivity(this)
            }
            2 -> {
                gotoSignInActivity(this)
            }

        }
    }
}

@Composable
fun LoadingScreenCheck(isUserLoggedIn: (value: Int) -> Unit) {
    var splashValue by remember { mutableStateOf(true) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(3000)
        splashValue = false
    }

    if (splashValue) {
        OpenPollingStartScreen()
    } else {

        if(UserPrefs.checkLoginStatus(context = context))
        {
            isUserLoggedIn.invoke(1)
        }else{
            isUserLoggedIn.invoke(2)
        }
    }
}



@Composable
fun OpenPollingStartScreen() {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF6200EE))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(bottomEnd = 32.dp, bottomStart = 32.dp))
                    .background(Color(0xFF6200EE)) // Purple color
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.ic_vote), // Replace with your image
                        contentDescription = "Polling App",
                        modifier = Modifier
                            .size(150.dp)
                    )

                    Text(
                        text = "Welcome to Polling App",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )

                }


            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {

                    Spacer(modifier = Modifier.weight(1f))

                    Image(
                        painter = painterResource(id = R.drawable.ic_polling), // Replace with your image
                        contentDescription = "Polling App",
                        modifier = Modifier
                            .size(150.dp)
                    )

                    Text(
                        text = " by Sai Krishna Pannela",
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.weight(1f))

                }


            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    OpenPollingStartScreen()
}

fun gotoSignInActivity(context: Activity) {
    context.startActivity(Intent(context, LoginActivity::class.java))
    context.finish()
}

fun gotoHomeActivity(context: Activity) {
    context.startActivity(Intent(context, HomeActivity::class.java))
    context.finish()
}