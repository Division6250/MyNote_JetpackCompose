package com.example.mynote

import android.R.attr.shape
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mynote.model.Note
import com.example.mynote.ui.theme.MyNoteJetpackComposeTheme
import com.example.mynote.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyNoteJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val noteViewModel = viewModel<NoteViewModel>()
    val title = remember {
        mutableStateOf("")
    }
    val description = remember {
        mutableStateOf("")
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        HeaderView()
        EnterNote(
            noteViewModel = noteViewModel,
            value1 = title.value,
            value2 = description.value,
        ) {
            result ->
            title.value = result[0]
            description.value = result[1]
        }
        ListNote(noteViewModel.noteList) {
            deleteNote ->
            noteViewModel.deleteNote(deleteNote)
            // Just notify UI change
            val temp = title.value
            title.value = "1"
            title.value = temp
        }
    }
}

@Composable
fun HeaderView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(5.dp)
            .background(Color.LightGray)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(13.dp),
            text = "My Note",
            style = MaterialTheme.typography.h6
        )
        IconButton(
            onClick = { },
            modifier = Modifier
                .align(Alignment.CenterEnd),
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Bell"
            )
        }
    }
}

@Composable
fun EnterNote(noteViewModel: NoteViewModel, value1: String, value2: String, enterNote: (result: List<String>) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(
            label = { Text("Title") },
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = {
                enterNote(listOf(it, value2))
            },
            value = value1,
        )
        OutlinedTextField(
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = {
                enterNote(listOf(value1, it))
            },
            value = value2,
        )
        Button(
            onClick = {
                noteViewModel.addNote(
                    Note(
                        title = value1,
                        description = value2,
                        entryDate = System.currentTimeMillis()
                    )
                )
                // Just notify UI change
                enterNote(listOf("1", ""))
                enterNote(listOf("", ""))
            },
            modifier = Modifier
                .padding(7.dp),
        ) {
            Text(text = "Add note")
        }
    }
}

@Composable
fun ListNote(noteList: StateFlow<List<Note>>, deleteNote: (result: Note) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        items(noteList.value) {
            item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, bottom = 5.dp)
                    // https://www.android--code.com/2021/09/jetpack-compose-box-rounded-corners_25.html
                    .clip(RoundedCornerShape(topEnd = 25.dp, bottomStart = 25.dp))
                    .background(Color(0xffdce5ea)),
            ) {
                Row(
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 5.dp, bottom = 5.dp, start = 20.dp, end = 20.dp)
                            .fillParentMaxWidth(),
                    ) {
                        Text(
                            item.title,
                            style = MaterialTheme.typography.h6,
                        )
                        Text(
                            item.description,
                            style = MaterialTheme.typography.subtitle1,
                        )
                        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)
                        Text(
                            formatter.format(Date(item.entryDate)),
                            style = MaterialTheme.typography.subtitle1,
                        )
                    }
                }
                IconButton(
                    onClick = {
                        deleteNote(item)
                    },
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .align(Alignment.CenterEnd),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete note"
                    )
                }
            }
        }
    }
}

