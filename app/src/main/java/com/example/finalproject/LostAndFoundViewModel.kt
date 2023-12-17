package com.example.finalproject


import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.Item
import com.example.finalproject.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.JsonAdapter
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class LostAndFoundViewModel : ViewModel() {
    private val itemsFileNameFound = "items.json"
    private val itemsFileNameLost = "items2.json"

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val itemsFlow = _items.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQueryFlow: StateFlow<String> = _searchQuery

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }


    fun addItem(item: Item, context: Context, isLost: Boolean) {
        val currentItems = _items.value.toMutableList()
        currentItems.add(item)

        _items.value = currentItems
        saveItems(context, isLost)
    }

    fun getItems() = itemsFlow



    fun loadItemsFromStorage(context: Context, isLost: Boolean) {
        val itemsFileName = if (isLost) itemsFileNameLost else itemsFileNameFound

        if (!fileExists(context, itemsFileName)) {
            val mockDataFileName = if (isLost) "mock_data2.json" else "mock_data.json"
            copyAssetFileToInternalStorage(context, mockDataFileName, itemsFileName)
        }

        try {
            context.openFileInput(itemsFileName).use { fileInput ->
                val jsonString = fileInput.bufferedReader().use { it.readText() }
                val itemsList: List<Item> = parseJson(jsonString)
                _items.value = itemsList
                Log.d("ReadItems", jsonString)
            }
        } catch (e: IOException) {
            Log.e("ReadItems", "Error reading items from JSON: ${e.message}")
        }
    }



    private fun fileExists(context: Context, fileName: String): Boolean {
        val file = context.getFileStreamPath(fileName)
        return file.exists()
    }

    private fun copyAssetFileToInternalStorage(context: Context, assetFileName: String, internalFileName: String) {
        try {
            context.assets.open(assetFileName).use { inputStream ->
                context.openFileOutput(internalFileName, Context.MODE_PRIVATE).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Log.d("FileCopy", "Copied $assetFileName to $internalFileName")
        } catch (e: IOException) {
            Log.e("FileCopy", "Error copying file: ${e.message}")
        }
    }

    private fun saveItemsToJson(items: List<Item>, fileName: String, context: Context) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(items)
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
                outputStream.write(jsonString.toByteArray())
            }
        } catch (e: IOException) {
            Log.e("FileOperation", "Error saving items to JSON: ${e.message}")
        }
    }

    private fun saveItems(context: Context, isLost: Boolean) {
        viewModelScope.launch {
            try {
                val fileName = if (isLost) "items2.json" else "items.json"
                saveItemsToJson(_items.value, fileName, context)

                // Log successful save
                Log.d("FileSave", "Items saved successfully.")

                // Read and log items from storage (optional)
                loadItemsFromStorage(context, isLost)
            } catch (e: Exception) {
                Log.e("FileOperation", "Error saving items: ${e.message}")
            }
        }
    }


    private fun parseJson(jsonString: String): List<Item> {
        return try {
            val typeToken = object : TypeToken<List<Item>>() {}.type
            Gson().fromJson(jsonString, typeToken)
        } catch (e: Exception) {
            Log.e("ParseJson", "Error parsing JSON: ${e.message}")
            emptyList()
        }
    }

}