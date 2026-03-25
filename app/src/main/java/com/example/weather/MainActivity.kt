package com.example.weather

import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weather.ui.theme.WeatherTheme

class MainActivity : ComponentActivity() {

    var city = mutableStateOf("")
    var temperature = mutableStateOf("")
    var weatherDescription = mutableStateOf("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(60.dp)
                    ) {
                        Column(

                        ) {
                            Spacer(modifier = Modifier.height(40.dp))
                            TextField(
                                value = city.value,
                                onValueChange = { newText ->
                                    city.value = newText
                                },
                                label = { Text("Enter city here") } // Optional label/hint
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            ElevatedButton(onClick = {
                                Log.d("Test" , city.value)
                            }, modifier = Modifier.width(320.dp), ) {
                                Text("Search")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.height(60.dp))
                            Text("City name: ${city.value}")
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Temperature: ${temperature.value}")
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Weather Description: ${weatherDescription.value}")
                        }
                    }
                }
            }
        }
    }
}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    WeatherTheme {
//        Greeting("Android")
//    }
//}