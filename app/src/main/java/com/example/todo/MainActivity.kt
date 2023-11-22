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
import androidx.compose.foundation.shape.CornerSize
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
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
fun EmptyMessage () {

}

@Composable
fun ListCard (value: String, onRemove: () -> Unit, onClick: (Boolean) -> Unit) {
    var check = rememberSaveable { mutableStateOf<Boolean>(false) }

    fun onChange () {
        onClick (check.value)
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
                .clickable { onRemove() }
        )
    }
}

enum class Type {
    PRIMARY,
    SECONDARY
}
@Composable
fun Counter (number: Int, text: String, type: Type) {
    Row (horizontalArrangement = Arrangement.Center){
        when (type) {
            Type.PRIMARY -> {
                Text(
                    "$text",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700,
                    color = Color(android.graphics.Color.parseColor("#4EA8DE")))
            }

            Type.SECONDARY -> {
                Text(
                    "$text",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700,
                    color = Color(android.graphics.Color.parseColor("#8284FA")))
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$number",
            fontSize = 14.sp,
            modifier = Modifier
                .background(
                    Color.DarkGray,
                    shape = RoundedCornerShape(80)
                )
                .padding(10.dp, 2.dp),
            color = Color.White)
    }
}

@Composable
fun HeaderList (creates: Int, completeds: Int) {
    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
        Counter(creates, "Criadas", Type.PRIMARY)
        Counter(completeds, "Concluídas", Type.SECONDARY)
    }
}

data class ListValue(val id: Int, val text: String, val checked: Boolean)
@Composable
fun App() {
    var list by rememberSaveable { mutableStateOf(emptyList<ListValue>()) }

    fun onSubmit (text: String) {
        if (text.trim().isNotEmpty()) {
            list += ListValue(id = list.size, text = text.trim(), checked = false)
        }
    }

    fun onCheck (id: Int, check: Boolean) {
        list = list.map { item ->
            if (item.id == id) {
                item.copy(checked = !check)
            } else {
                item
            }
        }
    }

    fun onRemove (id: Int) {
        list = list.filter { it.id != id }
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
                .offset(y = (60).dp)
        )
        InputAndButton ({ onSubmit(it) },
            Modifier
                .fillMaxWidth()
                .padding(20.dp, 0.dp)
                .align(Alignment.TopCenter)
                .offset(y = 130.dp) )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-100).dp)
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp)
                    .height(450.dp)
            ) {
                HeaderList(list.size, list.filter {it.checked}.size )
                Box(modifier = Modifier.height(30.dp))
                if (list.isEmpty()) Box(modifier = Modifier
                    .background(Color.DarkGray)
                    .fillMaxWidth()
                    .height((1.5).dp))
                if (list.isNotEmpty()){

                    LazyColumn() {
                    items(list) { item ->
                        ListCard(
                            item.text,
                            onRemove = { onRemove(item.id) },
                            onClick = { onCheck(item.id, it) })
                        Box(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
        if (list.isEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalAlignment = Alignment
                    .CenterHorizontally) {
                Text(
                    text = "Você ainda não tem tarefas cadastradas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(android.graphics.Color.parseColor("#808080")))
                Text(
                    text = "Crie tarefas e organize seus itens a fazer",
                    color = Color(android.graphics.Color.parseColor("#808080")))
            }
        }
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