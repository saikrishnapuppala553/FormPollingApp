package saikrishnas3495275.pollingapp.madproject.student

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import saikrishnas3495275.pollingapp.madproject.ui.theme.FormPollingAppTheme

class StudentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FormPollingAppTheme() {
                StudentHomeScreen()
            }
        }
    }
}