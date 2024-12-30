package com.example.firecracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.firecracker.ui.theme.FirecrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirecrackerTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF000080))  // Navy blue background
                ) {
                    CircularFirework(
                        modifier = Modifier
                            .fillMaxSize(fraction = 0.8f)
                            .align(Alignment.Center)
                    )
                    OvalBurstAnimation(
                        modifier = Modifier
                            .fillMaxSize(fraction = 0.5f)
                            .align(Alignment.TopStart)
                    )
                }
            }
        }
    }
}


