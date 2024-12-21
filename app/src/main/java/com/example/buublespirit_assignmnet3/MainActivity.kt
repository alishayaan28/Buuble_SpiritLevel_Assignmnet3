package com.example.buublespirit_assignmnet3

import android.app.Activity
import android.content.pm.ActivityInfo
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
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
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
import java.util.LinkedList
import java.util.Queue
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

                val filter = FloatArray(3){0f}
                var time = 0L
                val storeList = LinkedList<Float>()
                val x = LinkedList<Float>()
                val y = LinkedList<Float>()

                override fun onSensorChanged(event: SensorEvent?) {

                    event?.let {

                        val cTime = System.currentTimeMillis()

                        when (it.sensor.type){
                            Sensor.TYPE_ACCELEROMETER -> {
                                val fil = filterValues(it.values, filter)

                                if(cTime - time > 100){
                                    time = cTime
                                    val circleX = calculateAng(fil[0], fil[2])
                                    val circleY = calculateAng(fil[1], fil[2])
                                    val single = calculateAng(fil[0], fil[1])

                                    // Store 500 value in a link list
                                    when(sensorConf.orientation){
                                        Configuration.ORIENTATION_PORTRAIT ->{
                                            calFiveHV(storeList, "%.1f".format(single).toFloat(), 500)
                                            calFiveHV(x, "%.1f".format(circleX).toFloat(), 500)
                                            calFiveHV(y, "%.1f".format(circleY).toFloat(), 500)
                                        }
                                        Configuration.ORIENTATION_LANDSCAPE->{
                                            calFiveHV(storeList, "%.1f".format(single - 90).toFloat(), 500)
                                            calFiveHV(x, "%.1f".format(circleX).toFloat(), 500)
                                            calFiveHV(y, "%.1f".format(circleY).toFloat(), 500)
                                        }
                                        else->{}
                                    }

                                    // Check the Mode for Landscape and portrait and calculate axis for both
                                    sensorClass = SensorModel(
                                        landscape = landscapeF(
                                            circleX.toFloat(),
                                            circleY.toFloat()
                                        ),
                                        portraitX = when (sensorConf.orientation){
                                            Configuration.ORIENTATION_PORTRAIT -> single.toFloat()
                                            Configuration.ORIENTATION_LANDSCAPE -> (single - 90).toFloat()
                                            else -> sensorClass.portraitX
                                        },
                                        xVal = x.toList(),
                                        yVal = y.toList(),

                                        landscapeX = when (sensorConf.orientation){
                                            Configuration.ORIENTATION_PORTRAIT -> circleX.toFloat()
                                            Configuration.ORIENTATION_LANDSCAPE -> circleY.toFloat()
                                            else -> sensorClass.landscapeX
                                        },

                                        landscapeY = when (sensorConf.orientation){
                                            Configuration.ORIENTATION_PORTRAIT -> circleY.toFloat()
                                            Configuration.ORIENTATION_LANDSCAPE -> circleX.toFloat()
                                            else -> sensorClass.landscapeY
                                        },
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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Spacer(modifier =  Modifier.padding(top = 10.dp))
            // Mode Check
            Text(
                if(sM.landscape) "Flat Mode" else "Uneven Mode",
                style = TextStyle(
                    fontFamily = poppins,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500
                )
            )

            Spacer(modifier =  Modifier.padding(top = 12.dp))

            // Current Angle 1D
            Row{
                Text(
                   "Current Angle 1D : ${"%.1f".format(sM.portraitX)} ",
                    style = TextStyle(
                        fontFamily = poppins,
                        fontSize = 14.sp,
                    )
                )
            }

            Spacer(modifier =  Modifier.padding(top = 12.dp))

            // Max VaL
            Row{
                Spacer(modifier =  Modifier.padding(start = 30.dp))
                Text(
                    "Max value at x = ${sM.xVal.maxOrNull()}",
                    style = TextStyle(
                        fontFamily = poppins,
                        fontSize = 14.sp,
                    )
                )
                Spacer(modifier =  Modifier.weight(1f))
                Text(
                    "Max value at y = ${sM.yVal.maxOrNull()}",
                    style = TextStyle(
                        fontFamily = poppins,
                        fontSize = 14.sp,
                    )
                )
                Spacer(modifier =  Modifier.padding(end = 30.dp))
            }

            Spacer(modifier =  Modifier.padding(top = 12.dp))

            //Min Val
            Row{
                Spacer(modifier =  Modifier.padding(start = 30.dp))
                Text(
                    "Min value at x = ${sM.xVal.minOrNull()}",
                    style = TextStyle(
                        fontFamily = poppins,
                        fontSize = 14.sp,
                    )
                )
                Spacer(modifier =  Modifier.weight(1f))
                Text(
                    "Min value at y = ${sM.yVal.minOrNull()}",
                    style = TextStyle(
                        fontFamily = poppins,
                        fontSize = 14.sp,
                    )
                )
                Spacer(modifier =  Modifier.padding(end = 30.dp))
            }

            Spacer(modifier =  Modifier.padding(top = 12.dp))

            //2D angle
            Row{
                Spacer(modifier =  Modifier.padding(start = 30.dp))
                Text(
                    "2D at x = ${"%.1f".format(sM.landscapeX)}",
                    style = TextStyle(
                        fontFamily = poppins,
                        fontSize = 14.sp,
                    )
                )
                Spacer(modifier =  Modifier.weight(1f))
                Text(
                    "2D at y = ${"%.1f".format(sM.landscapeY)}",
                    style = TextStyle(
                        fontFamily = poppins,
                        fontSize = 14.sp,
                    )
                )
                Spacer(modifier =  Modifier.padding(end = 30.dp))
            }

            Spacer(modifier =  Modifier.weight(1f))

            // Orientation Check for showing Composable Function
            if(sM.landscape){
                LandscapeBubbleView(
                    landscape = sM.landscape,
                    orient = LocalConfiguration.current,
                    landscapeX = sM.landscapeX,
                    landscapeY = sM.landscapeY
                )
            }
            else {
                if(sensorConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
                    PortraitBubbleView(
                        landscape = sM.landscape,
                        orient = LocalConfiguration.current,
                        portraitAngle = sM.portraitX
                    )

                }else{
                    PortraitBubbleView(
                        landscape = sM.landscape,
                        orient = LocalConfiguration.current,
                        portraitAngle = sM.portraitX
                    )
                }
            }
        }
    }

    @Composable
    fun PortraitBubbleView(landscape: Boolean, orient : Configuration, portraitAngle : Float){

        val moving = if(orient.orientation in listOf(Configuration.ORIENTATION_PORTRAIT, Configuration.ORIENTATION_LANDSCAPE)){
            (portraitAngle.coerceIn(-10f, 10f)* 35f)
        } else {
            portraitAngle
        }

        Canvas(modifier = Modifier.fillMaxSize()){

            if(!landscape){
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFB2FF59),
                            Color(0xFF69F0AE)
                        )
                    ),
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
                    style = Stroke(width = 5f),
                    color = Color.Black
                )

                drawCircle(
                    radius = 45f,
                    center = Offset(x= (size.width / 2) + moving, y = size.height * 0.5f),
                    color = Color(0xFFFFEB3B)
                )

            }

        }
    }

    @Composable
    fun LandscapeBubbleView(landscape: Boolean, orient : Configuration, landscapeX : Float, landscapeY: Float, ){

        val context = LocalContext.current
        val activity = context as? Activity

        if (activity != null) {
            if (orient.orientation == Configuration.ORIENTATION_LANDSCAPE && landscape) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else if (orient.orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()){
            val bubble = LandscapeCoordinates(
                mDegree = 10f,
                radius = size.width * 0.4f ,
                bRadius = size.width * 0.03f
            )

            val calculateValue = bubble.calValue(
                xAxis = landscapeX,
                yAxis = landscapeY ,
                portrait = orient.orientation == Configuration.ORIENTATION_PORTRAIT
            )

            if(landscape){
                drawCircle(
                    radius = size.width * 0.4f,
                    center = Offset(size.width / 2, size.height / 2),
                    color = Color(0xff3F51B5),
                    style = Stroke(width = 5f),
                )
                drawCircle(
                    color =Color(0xff76FF03),
                    center = Offset(size.width / 2, size.height / 2),
                    style = Stroke(width = 5f),
                    radius = size.width * 0.4f * 0.2f
                )

                drawCircle(
                    color = Color(0xffFF4081),
                    center = Offset(size.width / 2 + calculateValue.x, size.height / 2 + (calculateValue.y * -1)),
                    radius = size.width * 0.05f
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

    fun calFiveHV(list: Queue<Float>, num: Float, capacity: Int){
        if(list.size >= capacity){
            list.poll()
        }
        list.offer(num)
    }

}



