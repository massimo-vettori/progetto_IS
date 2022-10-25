package com.lacliquep.barattopoli

import android.content.Context
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.lacliquep.barattopoli.ui.theme.BarattopoliTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BarattopoliTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val context = LocalContext.current

    Box {
        Button(onClick = {
             Toast.makeText(context, "Hello $name!", Toast.LENGTH_SHORT).show()
        }, Modifier.background(Color.White)) {
            Row {
               /**/
                Image(painterResource(R.drawable.ic_action_name), "")
                Text(stringResource(R.string.app_name));
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BarattopoliTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Greeting("Android")
        }
    }
}
