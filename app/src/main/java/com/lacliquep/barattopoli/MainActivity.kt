package com.lacliquep.barattopoli

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.lacliquep.barattopoli.ui.theme.BarattopoliTheme

class MainActivity : ComponentActivity() {

    //istanze condivise
    val auth = FirebaseAuth.getInstance()
    //private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    //private val databaseRef: DatabaseReference = database.getReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BarattopoliTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column() {
                        Greeting("Android")
                        RegistrationAndLogin(auth = auth, context = LocalContext.current )
                    }

                }

            }
        }
    }
}


@Composable
fun Greeting(name: String) {
    val context = LocalContext.current
    val navController = rememberNavController()

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





@Composable
fun RegistrationAndLogin(auth: FirebaseAuth, context: Context) {

    //costanti prese da string.xml da fornire come argomenti a Register
    //visto che, non essendo un composable, non può accedervi direttamente
    val failure = stringResource(R.string.failure)
    val success = stringResource(R.string.success)
    val empty_email = stringResource(R.string.empty_email)
    val empty_password = stringResource(R.string.empty_password)
    val password_wrong_length = stringResource(R.string.password_wrong_length)
    val register = stringResource(R.string.register)
    val login = stringResource(R.string.login)

    //salvataggio dello stato iniziale di email, cioè stringa vuota
    var email by remember{ mutableStateOf("")}
    //salvataggio dello stato iniziale di password, cioè stringa vuota
    var password by remember{ mutableStateOf("")}

        //elementi mostrati in ordine verticale
        Column(modifier=Modifier.fillMaxSize()) {
            //chiamata di altri composable per cambiare lo stato di email in risposta ad un evento
            Email(email = email, OnEmailChange = { email = it })
            Password(password = password, OnPasswordChange = { password = it })
            //tasto registrazione (la lambda Registser non può essere un composable)
            Button(onClick = { Register(email, password, auth, context,
                                        failure, success, register, empty_email,
                                        empty_password, password_wrong_length) })
            {
                        Row {
                        Text(stringResource(R.string.register));
                        }

            }
            Button(onClick = { Login(email, password, auth, context,
                failure, success, login, empty_email,
                empty_password) })
            {
                Row {
                    Text(stringResource(R.string.login));
                }

            }

        }
}

@Composable
fun Email(email: String, OnEmailChange: (String) -> Unit) {
    //cornice
    OutlinedTextField(
        value = email,
       //icona della mail
        leadingIcon = {Icon(imageVector = Icons.Default.Email, contentDescription = "EmailIcon")},
       //testo mostrato sulla cornice
       label = { Text(text = stringResource(R.string.email_address)) },
       //testo visualizzato cliccando dentro alla cornice
       placeholder = { Text(text = stringResource(R.string.enter_your_email))},
       //funzione che reagisce all'inserimento di testo nella cornice
       //cambiando lo stato di email nel chiamante
       onValueChange = OnEmailChange,
    )
}
@Composable
fun Password(password: String, OnPasswordChange: (String) -> Unit) {
    //cornice
    OutlinedTextField(
        value = password,
        //testo mostrato sulla cornice
        label = { Text(text = stringResource(R.string.password)) },
        //testo visualizzato cliccando dentro alla cornice
        placeholder = { Text(text = stringResource(R.string.enter_your_password) + stringResource(R.string.app_name))},
        //funzione che reagisce all'inserimento di testo nella cornice
        //cambiando lo stato di password nel chiamante
        onValueChange = OnPasswordChange,
    )
}

/**
 * funzione che ha lo scopo di registrare email e password di un nuovo utente per
 * la successiva autenticazione tramite login. Si richiedono le stringhe utili per la generazione
 * dei feedback all'utente (non essendo un composable non può recuperarle autonomamente da string.xml
 */
fun Register(email: String, password: String, auth: FirebaseAuth, context: Context,
             failure: String, success: String, register: String, empty_email: String, empty_password: String,
             password_wrong_length: String) {
    //alcuni controlli sul testo inserito dall'utente
    if (email.isNotEmpty() && password.isNotEmpty() && password.length >= 6) {
        //salvataggio dell'esito della registrazione con auth in costante task
        val task = auth.createUserWithEmailAndPassword(email, password)
        //stringa da mostrare nel toast come feedback all'azione
        //TODO: togliere task.exception?.message
        var taskResult = failure + "\n exception message: " + task.exception?.message
        //controllo del risultato dell'azione
        if (task.isSuccessful) taskResult = success
        //feedback all'utente
        Toast.makeText(context, register + taskResult, Toast.LENGTH_SHORT).show()
    } else {
        //stringa per il feedback sul tipo di errore/i
        var errorMessage = "";
        if(!(email.isNotEmpty())) errorMessage += empty_email
        if(!(password.isNotEmpty())) errorMessage += empty_password
        if(password.length < 6) errorMessage += password_wrong_length
        //feedback di cosa è andato storto
        Toast.makeText(context, errorMessage , Toast.LENGTH_SHORT).show()
    }

}

/**
 * funzione che ha lo scopo di registrare email e password di un nuovo utente per
 * la successiva autenticazione tramite login. Si richiedono le stringhe utili per la generazione
 * dei feedback all'utente (non essendo un composable non può recuperarle autonomamente da string.xml
 */
fun Login(email: String, password: String, auth: FirebaseAuth, context: Context,
             failure: String, success: String, login: String, empty_email: String, empty_password: String) {
    //alcuni controlli sul testo inserito dall'utente
    if (email.isNotEmpty() && password.isNotEmpty()) {
        //salvataggio dell'esito della registrazione con auth in costante task
        val task = auth.signInWithEmailAndPassword(email, password)
        //stringa da mostrare nel toast come feedback all'azione
        //TODO: togliere task.exception?.message
        var taskResult = failure + "\n exception message: " + task.exception?.message
        //controllo del risultato dell'azione
        if (task.isSuccessful) taskResult = success
        //feedback all'utente
        Toast.makeText(context, login + taskResult, Toast.LENGTH_SHORT).show()
    } else {
        //stringa per il feedback sul tipo di errore/i
        var errorMessage = "";
        if(!(email.isNotEmpty())) errorMessage += empty_email
        if(!(password.isNotEmpty())) errorMessage += empty_password
        //feedback di cosa è andato storto
        Toast.makeText(context, errorMessage , Toast.LENGTH_SHORT).show()
    }
}

