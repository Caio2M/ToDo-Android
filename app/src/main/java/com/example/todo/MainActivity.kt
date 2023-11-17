package com.example.todo

import android.icu.text.AlphabeticIndex.Record
import android.os.Bundle
import android.widget.ScrollView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.todo.ui.theme.ToDoTheme
import java.util.Objects
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoTheme {
                App()
            }
        }
    }
}

@Composable
fun MyButton (onClick: () -> Unit) {
    Box(modifier = Modifier
        .background(
            Color(android.graphics.Color.parseColor("#1E6F9F")),
            shape = RoundedCornerShape(12)
        )
        .size(60.dp)
        .clickable { onClick() }
    ){
        Icon(
            Icons.Default.Add,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .border(
                    (1.5).dp,
                    Color.White,
                    shape = RoundedCornerShape(100)
                )
                .size(20.dp)
                .padding(2.dp)
                .align(Alignment.Center)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MyInput (value: String, set: (String) -> Unit, placeHolder: String) {
    var focused by rememberSaveable { mutableStateOf<Boolean>(false) }

    var customTextField = TextFieldDefaults.textFieldColors(
        backgroundColor = Color(android.graphics.Color.parseColor("#333333")),
        cursorColor = Color.White,
        textColor = Color.White,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )

    TextField(
        value,
        { set(it) },
        modifier = Modifier
            .border(
                (1.5).dp,
                if (focused)
                    Color(android.graphics.Color.parseColor("#0D0D0D"))
                else
                    Color(android.graphics.Color.parseColor("#5E60CE")),
                shape = RoundedCornerShape(6)
            )
            .fillMaxWidth(0.80f)
            .onFocusChanged { focused = !focused }
            .clip(RoundedCornerShape(6))
            .height(60.dp)
        ,
        colors = customTextField,
        placeholder = {
            Text(
                text = placeHolder,
                color = Color(android.graphics.Color.parseColor("#808080")),
                modifier = Modifier.padding(3.dp)
            )
        }
    )
}

@Composable
fun InputAndButton (onSubmit: (String) -> Unit, modifier: Modifier) {
    var inputValue by rememberSaveable { mutableStateOf<String>("") }

    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier) {
        MyInput(
            value = inputValue,
            set = { inputValue = it },
            placeHolder = "Adicione uma nova tarefa"
        )
        MyButton { onSubmit(inputValue); inputValue = "" }
    }
}

@Composable
fun CircularCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .background(
                color = if (checked) Color(android.graphics.Color.parseColor("#5E60CE")) else Color.Transparent,
                shape = CircleShape
            )
            .border(
                (1.5).dp,
                if (checked) Color(android.graphics.Color.parseColor("#5E60CE")) else Color(
                    android.graphics.Color.parseColor("#4EA8DE")
                ),
                shape = CircleShape
            )
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
@Composable
fun ListCard (value: String) {
    var check = remember { mutableStateOf<Boolean>(false) }

    fun onChange () {
        check.value = !check.value
    }

    Box(modifier = Modifier
        .clickable { onChange() }
        .fillMaxWidth()
        .border(
            (1.5).dp,
            Color(android.graphics.Color.parseColor("#333333")),
            shape = RoundedCornerShape(10)
        )
        .background(
            Color(android.graphics.Color.parseColor("#262626")),
            shape = RoundedCornerShape(10)
        )
        .height(80.dp)
        .padding(10.dp, 0.dp)
    ){
        CircularCheckbox(
            checked = check.value,
            onCheckedChange = { onChange() } ,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 10.dp)
        )
        Text(
            text = value.trim(),
            color = if (check.value) Color(android.graphics.Color.parseColor("#808080")) else Color.White,
            textDecoration = if (check.value) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 50.dp)
                .fillMaxWidth(0.7f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = Color(android.graphics.Color.parseColor("#808080")),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-10).dp)
                .size(26.dp)
        )
    }
}

@Composable
fun App() {
    val listValue = remember { mutableStateListOf<String>() }

    fun onSubmit (text: String) {
        if (text.trim().isNotEmpty()) {
            listValue.add(text.trim())
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(android.graphics.Color.parseColor("#1A1A1A")))
    ) {
        Text(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            text = "To Do",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (100).dp)
        )
        InputAndButton ({ onSubmit(it) },
            Modifier
                .fillMaxWidth()
                .padding(20.dp, 0.dp)
                .align(Alignment.TopCenter)
                .offset(y = 200.dp) )
            LazyColumn(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-50).dp)
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp)
                    .height(450.dp)
            ) {
                items (listValue) { message ->
                    ListCard(message)
                    Box(modifier = Modifier.height(10.dp))
                }}
        }
    }

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ToDoTheme {
        Box(){
            App()
        }
    }
}