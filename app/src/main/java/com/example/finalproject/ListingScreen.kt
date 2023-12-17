package com.example.finalproject

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun ListingScreen(navController: NavController, viewModel: LostAndFoundViewModel, context: Context, isLost: Boolean) {
    var selectedItem by remember { mutableStateOf<Item?>(null) }

    // Observe the items using StateFlow
    val items by viewModel.getItems().collectAsState()

    // Observe search query
    val searchQuery by viewModel.searchQueryFlow.collectAsState()

    // Load items only if the list is empty or searchQuery changes
    LaunchedEffect(searchQuery, isLost) {
        if (items.isEmpty() || searchQuery.isNotEmpty()) {
            viewModel.loadItemsFromStorage(context, isLost)
        }
    }

    // Clear the search query when navigating back to the Welcome screen
    DisposableEffect(navController) {
        onDispose {
            viewModel.setSearchQuery("")
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        SearchBar(onSearch = { query -> viewModel.setSearchQuery(query) })
        Spacer(modifier = Modifier.height(16.dp))

        if (selectedItem == null) {
            LostAndFoundList(
                items.filter { it.item_name.contains(searchQuery, ignoreCase = true) },
                onItemClick = { selected -> selectedItem = selected }
            )
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            LostAndFoundDetails(selectedItem!!) {
                selectedItem = null
            }
        }
    }

    // Floating action button outside of the Column
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = {
                navController.navigate("addItemScreen")
            },
            content = {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        )
    }
}


@Composable
fun SearchBar(onSearch: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchQuery,
        onValueChange = {
            searchQuery = it
            onSearch(it)
        },
        label = { Text("Search") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth()
    )
}




@Composable
fun LostAndFoundList(items: List<Item>, onItemClick: (Item) -> Unit) {
    LazyColumn {
        items(items) { item ->
            LostAndFoundItem(item) { onItemClick(item) }
            Divider()
        }
    }
}


@Composable
fun LostAndFoundItem(item: Item, onItemClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(16.dp)
    ) {
        // Display the image if imageData is not null
        if (!item.imageData.isNullOrBlank()) {
            // Decode Base64 string to ByteArray
            val imageByteArray = Base64.decode(item.imageData, Base64.DEFAULT)

            // Create a Bitmap from the ByteArray
            val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)

            // Display the Bitmap using Image composable
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        // Display item details
        Column {
            Text(text = "${item.found_date}", fontSize = 12.sp)
            Text(text = "${item.item_name}", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${item.description}")
        }
    }
}


@Composable
fun LostAndFoundDetails(item: Item, onGoBack: () -> Unit) {
    val context = LocalContext.current

    Column(modifier = Modifier .fillMaxWidth() .padding(16.dp) .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = item.item_name,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextWithIcon(icon = Icons.Default.DateRange, text = "${item.found_date}")
        TextWithIcon(icon = Icons.Default.LocationOn, text = "${item.found_location}")

        // Decode Base64 string to ByteArray
        if (!item.imageData.isNullOrBlank()) {
            val imageByteArray = Base64.decode(item.imageData, Base64.DEFAULT)

            // Create a Bitmap from the ByteArray
            val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)

            // Display the Bitmap using Image composable
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(text = "Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = item.description ?: "", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Contacts", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = "${item.found_by}", fontSize = 16.sp)
        // Make phone number clickable
        ClickableText(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("Phone number: ${item.contact_phone}")
                }
            },
            onClick = { offset ->
                val phoneNumber = item.contact_phone
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                context.startActivity(intent)
            },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Make email clickable
        ClickableText(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("Email: ${item.contact_email}")
                }
            },
            onClick = { offset ->
                val email = item.contact_email
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$email")
                }
                context.startActivity(intent)
            },
            modifier = Modifier.padding(bottom = 16.dp)
        )


        // Go Back button
        Button(onClick = onGoBack) { Text(text = "Go Back") }
        BackHandler {
            // Handle the back button press
            onGoBack()
        }
    }
}



@Composable
fun TextWithIcon(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 16.sp)
    }
}
