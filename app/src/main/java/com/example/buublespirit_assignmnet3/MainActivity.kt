package com.example.buublespirit_assignmnet3

import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buublespirit_assignmnet3.ui.theme.BuubleSpiritAssignmnet3Theme
import com.example.buublespirit_assignmnet3.ui.theme.poppins
import kotlin.math.atan2

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
        val sensorConf = LocalConfiguration.current
        val filter = FloatArray(3){0f}
        var time = 0L

        var sensorClass by remember { mutableStateOf(SensorModel())}
        val sensorM = remember {
            context.getSystemService(SensorManager::class.java)
        }
        val sensorDefault = remember {
            sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
        val sensorDefault1 = remember {
            sensorM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        }

        val sensorLst = remember {
            object : SensorEventListener{
                override fun onSensorChanged(event: SensorEvent?) {

                    event?.let {

                        val cTime = System.currentTimeMillis()

                        when (it.sensor.type){
                            Sensor.TYPE_ACCELEROMETER -> {
                                val fil = filterValues(it.values, filter)

                                if(cTime - time > 100){
                                    time = cTime
                                    val circleX = calculateAng(fil[0], fil[1])
                                    val circleY = calculateAng(fil[0], fil[1])



                                    sensorClass = SensorModel(
                                        landscape = landscapeF(
                                            circleX.toFloat(),
                                            circleY.toFloat()
                                        )
                                    )


                                }



                            }
                            Sensor.TYPE_MAGNETIC_FIELD -> {
                            }
                        }

                    }


                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

            }
        }

        LaunchedEffect(Unit) {
            sensorM.registerListener(
                sensorLst,
                sensorDefault,
                SensorManager.SENSOR_DELAY_UI
            )
            sensorM.registerListener(
                sensorLst,
                sensorDefault1,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        DisposableEffect(Unit) {
            onDispose {
                sensorM.unregisterListener(sensorLst)
            }
        }
        Layout(
            sM = sensorClass,
            sensorConfig = sensorConf

        )
    }

    @Composable
    fun Layout(
        sM: SensorModel,
        sensorConfig: Configuration
    ){
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                if(sM.landscape) "LandScape Mode" else "Portrait Mode"
            )
            Spacer(modifier =  Modifier.weight(1f))
            if(sM.landscape){
                LandscapeBubbleView(
                    landscape = sM.landscape,
                    orient = LocalConfiguration.current
                )
            } else {
                if(sensorConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
                    PortraitBubbleView(
                        landscape = sM.landscape,
                        orient = LocalConfiguration.current
                    )

                }else{
                    PortraitBubbleView(
                        landscape = sM.landscape,
                        orient = LocalConfiguration.current
                    )
                }
            }

        }
    }


    @Composable
    fun PortraitBubbleView(
        landscape: Boolean,
        orient : Configuration
    ){

        val bubbleMove = if(orient.orientation in listOf(Configuration.ORIENTATION_PORTRAIT, Configuration.ORIENTATION_LANDSCAPE)){

        } else {

        }

        Canvas(modifier = Modifier.fillMaxSize()){

            if(!landscape){
                drawRoundRect(
                    color = Color(0xFFBBDEFB),
                    topLeft = Offset(
                        (size.width - 800f) / 2,
                        (size.height - 100f) / 2
                    ),
                    size = Size(width = 800f, height = 100f),
                    cornerRadius = CornerRadius(50f, 50f)
                )

                drawCircle(
                    radius = 45f,
                    center = Offset(size.width * 0.5f, size.height * 0.5f),
                    style = Stroke(width = 4f),
                    color = Color(0xFFFF5722)
                )

                drawCircle(
                    radius = 45f,
                    // center = Offset(size.width * 0.5f, size.height * 0.5f),
                    color = Color(0xFF81C784)
                )

            }

        }
    }

    @Composable
    fun LandscapeBubbleView(
        landscape: Boolean,
        orient : Configuration
    ){
        Canvas(modifier = Modifier.fillMaxSize()){
            val circle : Float
            val iCircle : Float
            val bCircle : Float
            if(orient.orientation == Configuration.ORIENTATION_PORTRAIT){
                bCircle = size.width * 0.03f
                iCircle = size.width * 0.4f
                circle = iCircle
            }else{
                Configuration.ORIENTATION_PORTRAIT
                bCircle = size.width * 0.3f
                iCircle = size.width * 0.05f
                circle = iCircle
            }

            if(landscape){

                drawCircle(
                    radius = circle,
                    center = Offset(size.width / 2, size.height / 2),
                    color = Color.Green
                )

                drawCircle(
                    color = Color.Black,
                    center = Offset(size.width / 2, size.height / 2),
                    style = Stroke(width = 3f),
                    radius = circle * 0.2f
                )

            }

        }
    }

    fun filterValues(inp : FloatArray, out: FloatArray): FloatArray{
        val alpha = 0.9f
        for (m in inp.indices){
            out[m]= alpha * out[m] + ( 1 - alpha) * inp[m]
        }
        return out
    }

    fun calculateAng(val1: Float, val2: Float):Double{
        return atan2(val1.toDouble(), val2.toDouble()) / (Math.PI/180)
    }

    fun landscapeF(x: Float, y: Float): Boolean{
        return x in -10f..10f && y in -10f..10f
    }

}



