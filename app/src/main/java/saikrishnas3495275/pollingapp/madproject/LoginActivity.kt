package saikrishnas3495275.pollingapp.madproject


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import kotlin.jvm.java


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoInScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoInScreenPreview() {
    GoInScreen()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoInScreen() {
    var errorMessage by remember { mutableStateOf("") }
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
                    .background(Color(0xFF6200EE))
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.ic_polling), 
                        contentDescription = "Polling Image",
                        modifier = Modifier
                            .size(150.dp)
                    )

                    Text(
                        text = "User Login",
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
                    // Email Basic TextField
                    var email by remember { mutableStateOf("") }
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 4.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Basic TextField
                    var password by remember { mutableStateOf("") }
                    var passwordVisible by remember { mutableStateOf(false) }
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 4.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_password_24),
                                contentDescription = "Toggle Password Visibility",
                                modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )


                    Spacer(modifier = Modifier.height(8.dp))

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login Button
                    Button(
                        onClick = {

                            val database = FirebaseDatabase.getInstance()
                            val databaseReference = database.reference

                            val sanitizedEmail = email.replace(".", ",")

                            databaseReference.child("SignedUpUsers").child(sanitizedEmail).get()
                                .addOnSuccessListener { snapshot ->
                                    if (snapshot.exists()) {
                                        val chefData = snapshot.getValue(UserData::class.java)
                                        chefData?.let {

                                            if (password == it.password) {

                                                Toast.makeText(context, "Login Successfull", Toast.LENGTH_SHORT).show()

                                                context.startActivity(Intent(context, HomeActivity::class.java))
                                                (context as Activity).finish()
                                            }
                                            else{
                                                Toast.makeText(context,"Incorrect Credentials",Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context,"No User Found",Toast.LENGTH_SHORT).show()
                                    }
                                }.addOnFailureListener { exception ->
                                    println("Error retrieving data: ${exception.message}")
                                }



                        },


                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBC02D)) // Yellow button
                    ) {
                        Text(text = "Log In", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Or Divider
                    Text("Or", color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Social Login Row
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceEvenly
//                    ) {
//                        SocialLoginIcon(painterResource(id = R.drawable.baseline_visibility_off_24)) // Replace with Google icon
//                        SocialLoginIcon(painterResource(id = R.drawable.baseline_lock_24))  // Replace with Apple icon
//                        SocialLoginIcon(painterResource(id = R.drawable.baseline_check_circle_24)) // Replace with Facebook icon
//                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Footer Text
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Don't have an account? ", color = Color.Black)
                        Text(
                            text = "Sign Up",
                            color = Color(0xFF6200EE), // Purple color for link
                            modifier = Modifier.clickable {
                                context.startActivity(Intent(context, RegisterActivity::class.java))
                                (context as Activity).finish()

                            }
                        )
                    }
                }
            }
        }
    }
}


data class UserData
    (
    var name: String = "",
    var role: String ="",
    var email: String ="",
    var password: String ="",
)