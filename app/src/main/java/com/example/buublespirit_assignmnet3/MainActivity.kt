package com.example.buublespirit_assignmnet3

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buublespirit_assignmnet3.ui.theme.BuubleSpiritAssignmnet3Theme
import com.example.buublespirit_assignmnet3.ui.theme.SensorModel
import com.example.buublespirit_assignmnet3.ui.theme.poppins

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuubleSpiritAssignmnet3Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White,
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = "Bubble/Spirit Level",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.W500,
                                        fontFamily = poppins
                                    )
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.White,
                                titleContentColor = Color.Black
                            ),
                            modifier = Modifier.shadow(4.dp)
                        )
                    },
                ) { innerPadding ->
                    Column (
                        modifier = Modifier.padding(innerPadding)
                    ){
                        SensorHelper()
                    }
                }
            }
        }
    }

    @Composable
    fun SensorHelper(){

        val context = LocalContext.current

        var sensorClass by remember { mutableStateOf(SensorModel())}
        val sensorM = remember {
            context.getSystemService(SensorManager::class.java)
        }
        var sensorDefault = remember {
            sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
        var sensorDefault1 = remember {
            sensorM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        }

        val sensorLst = remember {
            object : SensorEventListener{
                override fun onSensorChanged(event: SensorEvent?) {
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

            }
        }

        LaunchedEffect(Unit) {
            listOf(sensorDefault, sensorDefault1).forEach{
                sensors ->
                sensorM.registerListener(
                    sensorLst,
                    sensors,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                sensorM.unregisterListener(sensorLst)
            }
        }
    }
}
