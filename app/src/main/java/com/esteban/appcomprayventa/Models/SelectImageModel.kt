package com.esteban.appcomprayventa.Models

import android.net.Uri

class SelectImageModel {

    var id = ""
    var imageUri : Uri?=null
    var imageUrl : String?=null
    var onInternet = false

    constructor()
    constructor(id: String, imageUri: Uri?, imageUrl: String?, onInternet: Boolean) {
        this.id = id
        this.imageUri = imageUri
        this.imageUrl = imageUrl
        this.onInternet = onInternet
    }


}