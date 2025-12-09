package saikrishnas3495275.pollingapp.madproject.student

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
import saikrishnas3495275.pollingapp.madproject.LoginActivity
import saikrishnas3495275.pollingapp.madproject.UserData
import saikrishnas3495275.pollingapp.madproject.UserPrefs
import saikrishnas3495275.pollingapp.madproject.ui.theme.FormPollingAppTheme



class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FormPollingAppTheme() {
                StudentProfileScreen(
                    user = UserData(
                        UserPrefs.getName(this),
                        UserPrefs.getEmail(this),
                        UserPrefs.getRole(this),
                        UserPrefs.getPass(this)
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileScreen(user: UserData) {

    val context = LocalContext.current
    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) } // NOT editable
    var role by remember { mutableStateOf(user.role) }
    var password by remember { mutableStateOf(user.password) }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE)),
                navigationIcon = {
                    IconButton(onClick = {
                        (context as Activity).finish()
                    }) {
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
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // ------------------- PROFILE HEADER -------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(Color(0xFF6200EE), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1).uppercase(),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ------------------- INFO CARD -------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text("Full Name", fontWeight = FontWeight.SemiBold)
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Email Address", fontWeight = FontWeight.SemiBold)
                    TextField(
                        value = email,
                        onValueChange = { },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            disabledContainerColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // --------------- ROLE SELECTION ----------------
                    Text("Role", fontWeight = FontWeight.SemiBold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = role == "Student",
                                onClick = { role = "Student" },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF6200EE)
                                )
                            )
                            Text("Student")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = role == "Teacher",
                                onClick = { role = "Teacher" },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF6200EE)
                                )
                            )
                            Text("Teacher")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ------------------- UPDATE BUTTON -------------------
            Button(
                onClick = {
                    val db = FirebaseDatabase.getInstance()
                    val ref = db.getReference("SignedUpUsers")
                        .child(email.replace(".", ","))

                    val updatedUser = UserData(
                        name = name,
                        email = email,
                        role = role,
                        password = password
                    )

                    ref.setValue(updatedUser)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Update Failed", Toast.LENGTH_SHORT).show()
                        }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Update Profile", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ------------------- LOGOUT BUTTON -------------------
            Text(
                text = "Logout",
                fontSize = 16.sp,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        UserPrefs.markLoginStatus(context,false)
                        val intent = Intent(context, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                    }
            )
        }
    }
}