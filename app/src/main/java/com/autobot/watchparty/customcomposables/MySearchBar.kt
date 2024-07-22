//package com.launcher.arclauncher.compose.customcomposables
//
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.material.icons.filled.MoreVert
//import com.launcher.arclauncher.database.datatypes.DrawerApp
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import coil.compose.rememberAsyncImagePainter
//import coil.request.ImageRequest
//import com.launcher.arclauncher.database.viewmodels.AppViewModel
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MySearchBar(appViewModel: AppViewModel, onAppClick: (DrawerApp) -> Unit) {
//    val drawerApps by appViewModel.installedApps.observeAsState()
//    var text by remember { mutableStateOf("") }
//    var active by remember { mutableStateOf(false) }
//
//    SearchBar(
//        modifier = Modifier.padding(24.dp).fillMaxWidth(),
//        query = text,
//        onQueryChange = { text = it },
//        onSearch = { active = false },
//        active = active,
//        onActiveChange = { active = it },
//        placeholder = { Text("Search apps") },
//        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
//        trailingIcon = { Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu") }
//    ) {
//        LazyColumn {
//            val filteredApps = drawerApps?.filter { it.label.contains(text, ignoreCase = true) }?.sortedBy { it.label } ?: emptyList()
//            items(filteredApps) { app ->
//                Box(
//                    modifier = Modifier
//                        .clickable { onAppClick(app) }
//                        .padding(16.dp)
//                ) {
//                    Image(
//                        painter = rememberAsyncImagePainter(
//                            model = ImageRequest.Builder(LocalContext.current)
//                                .data(app.icon)
//                                .build()
//                        ),
//                        contentDescription = null,
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(24.dp))
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(text = app.label, style = TextStyle(fontSize = 18.sp))
//                }
//            }
//        }
//    }
//}
//@Composable
//@Preview(showBackground = true)
//fun CustomSearchBarPreview() {
//
//}
//
