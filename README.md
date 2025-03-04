![FoodieRee](https://github.com/user-attachments/assets/063c00cd-c29f-412f-9fa6-be547bcaa8ff)


https://github.com/user-attachments/assets/c781c5ad-22cd-40c4-b6a9-b69ad5e251dc

Set up guide -

1. Add all dependancies and permission -
    //ui control
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.26.4-beta")

    //navigation
    val nav_version = "2.8.5"
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    //icons
    implementation("androidx.compose.material:material-icons-extended-android:1.7.6")

    //video player
    implementation ("androidx.media3:media3-exoplayer:1.5.1")
    implementation ("androidx.media3:media3-ui:1.5.1")
    implementation ("androidx.media3:media3-common:1.5.1")

    // for short video pager
    implementation("com.google.accompanist:accompanist-pager:0.36.0")

    //room library for database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    //location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    //map
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation ("com.google.android.gms:play-services-maps:19.1.0")

    // for di
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")



    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

2. add api key for google map
  <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="replace_with_your_api_key" />

4. Here I have used DI (dagger hilt), MVVM, JetPack Compose, Media3 Exoplayer, Google Map

