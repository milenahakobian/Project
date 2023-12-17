package com.example.finalproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

@Composable
fun AddItemScreen(
    navController: NavController,
    viewModel: LostAndFoundViewModel,
    context: Context, isLost: Boolean // New parameter indicating whether the item is lost
) {
    var itemName by remember { mutableStateOf("") }
    var itemColor by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var itemFoundDate by remember { mutableStateOf("") }
    var itemFoundLocation by remember { mutableStateOf("") }
    var itemFoundBy by remember { mutableStateOf("") }
    var itemContactEmail by remember { mutableStateOf("") }
    var itemContactPhone by remember { mutableStateOf("") }

    // State to hold the selected image URI
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Image picker launcher
    val getImage: ActivityResultLauncher<Intent> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = itemName,
            onValueChange = { itemName = it },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = itemColor,
            onValueChange = { itemColor = it },
            label = { Text("Item Color") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = itemDescription,
            onValueChange = { itemDescription = it },
            label = { Text("Item Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = itemFoundDate,
            onValueChange = { itemFoundDate = it },
            label = { Text("Lost/Found Date") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = itemFoundLocation,
            onValueChange = { itemFoundLocation = it },
            label = { Text("Lost/Found Location") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = itemFoundBy,
            onValueChange = { itemFoundBy = it },
            label = { Text("Lost/Found By") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = itemContactEmail,
            onValueChange = { itemContactEmail = it },
            label = { Text("Contact Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = itemContactPhone,
            onValueChange = { itemContactPhone = it },
            label = { Text("Contact Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        // Image picker button
        Spacer(modifier = Modifier.height(16.dp))
        // Image picker button
        Button(onClick = {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            getImage.launch(intent)
        }) {
            Text(text = "Pick Image")
        }

        // Display selected image
        selectedImageUri?.let { uri ->
            Image(
                painter = rememberImagePainter(data = uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }


        // Existing code...

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                navController.popBackStack()
            }) {
                Text(text = "Cancel")
            }
            Button(onClick = {
                val newItem = Item(
                    // Generate a unique ID for the item
                    id = System.currentTimeMillis(),
                    item_name = itemName,
                    color = itemColor,
                    description = itemDescription,
                    found_date = itemFoundDate,
                    found_location = itemFoundLocation,
                    found_by = itemFoundBy,
                    contact_email = itemContactEmail,
                    contact_phone = itemContactPhone,
                    imageData = selectedImageUri?.let { uri ->
                        // Convert the image data to Base64
                        val imageByteArray =
                            context.contentResolver.openInputStream(uri)?.readBytes()
                        Base64.encodeToString(imageByteArray, Base64.DEFAULT)
                    }
                )
                // Use the ViewModel to add the item
                viewModel.addItem(newItem, context, isLost)

                // Navigate back
                navController.popBackStack()
            }) {
                Text(text = "Add Item")
            }
        }
    }
}

