package com.nstokes.aci

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nstokes.aci.ui.theme.MyApplicationTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ACIChatScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ACIChatScreen(modifier: Modifier = Modifier) {
    val maxMessages = 100 // Message number to hit before ACI re-evaluates itself and the conversation.
    var message by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }
    var showDebug by remember { mutableStateOf(false) }
    Column(modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true // Most recent at the bottom
        ) {
            if (messages.isEmpty()) {
                item {
                    ChatBubble(
                        text = "Welcome to the ACI Chat Screen! There is currently a maximum Chat View Size of 100 messages and the ACI's have not been fully implemented so this is an Echo Note's Taking App Currently! Thank you for reviewing my code!",
                        isUser = false,
                        modifier = Modifier.padding(8.dp)
                    ) // Welcome Message
                }
            }
            items(messages.reversed()) { msg ->
                ChatBubble(
                    text = msg.text,
                    isUser = msg.isUser,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message to ACI...") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (message.isNotBlank()) {
                    if (messages.size >= maxMessages) {
                        // Additional self-reference backend logic for ACI will go here
                            // Possibly Remove a classified chunk of the messages as "already processed", instead of one message at a time [i.e. Python.compressMessages(messages)]
                        messages.removeAt(0)
                    }
                    messages.add(Message(message, true))
                    message = ""
                    // Python API call
                }
            }) {
                Text("Send")
            }
            IconButton(onClick = { showDebug = !showDebug }) {
                Text(if (showDebug) "Hide" else "Bug")
            }
        }
        if (showDebug) {
            DebugPanel(messages = messages)
        }
    }
}
@Composable
fun DebugPanel(messages: SnapshotStateList<Message>) {
    var userCount = 0
    var aciCount = 0
    for (msg in messages) {
        if(msg.isUser) userCount++
        else aciCount++
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Debug Panel", style = MaterialTheme.typography.titleMedium)
            Text("Total Messages Currently: ${messages.size}", style = MaterialTheme.typography.bodySmall)
            Text("User: $userCount, ACI: $aciCount", style = MaterialTheme.typography.bodySmall)
        }
    }
}

data class Message(val text: String, val isUser: Boolean, val timestamp: Long = System.currentTimeMillis())

@Composable
fun ChatBubble(text: String, isUser: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 300.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ACIChatPreview() {
    MyApplicationTheme {
        ACIChatScreen()
    }
}