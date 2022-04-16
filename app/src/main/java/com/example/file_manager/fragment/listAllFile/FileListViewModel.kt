package com.example.file_manager.fragment.listAllFile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.file_manager.BuildConfig
import com.example.file_manager.common.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object FileListViewModel : ViewModel() {
    var typeOfFolder = "other"
    val stackPath = Stack<String>()
    val stackLastItemDisplay = Stack<Int>()
    private val _files: MutableLiveData< ArrayList<File> > = MutableLiveData()
    val files: LiveData<ArrayList<File>> = _files

    private val _isGrid: MutableLiveData<Boolean> = MutableLiveData()
    val isGrid: LiveData<Boolean> = _isGrid

    private val _stateLoading: MutableLiveData<Boolean> = MutableLiveData()
    val stateLoading: LiveData<Boolean> = _stateLoading


    /**
     Variable copy - paste file
     **/
    var selectedFile: File? = null
    var currentDictionary: File = File(Constant.path)
    var menuMode: MenuMode = MenuMode.OPEN
    var handleMode: HandleMode = HandleMode.NONE

    fun isRoot(): Boolean{
        return stackPath.size == 1
    }

    fun openFolder(path: String){
        stackPath.push(path)
        currentDictionary = File(stackPath.peek())
        getFiles(path)
    }

    fun onBackPressed(){
        stackPath.pop()
        currentDictionary= File(stackPath.peek())
        getFiles(stackPath.peek())
    }

    fun changeLayout(){
        _isGrid.postValue(!isGrid.value!!)
    }

    private fun getFiles(path: String) {
        viewModelScope.launch(Dispatchers.IO){
            val listFiles = ArrayList<File>()
            File(path).listFiles()?.let{
                for(file in it){
                    listFiles.add(file)
                }
            }
            _files.postValue(listFiles)
        }
    }

    private fun getSpecificFiles() {
        viewModelScope.launch(Dispatchers.IO){
            val listFiles = ArrayList<File>()
            val specificFiles = ArrayList<File>()
            File(Constant.path).listFiles()?.let{ listFiles.addAll(it) }
            var pos = 0
            while (pos < listFiles.size){
                val file = listFiles[pos]
                if (file.isDirectory){
                    file.listFiles()?.let{ listFiles.addAll(it) }
                }else{
                    if (checkIfFileHasExtension(file.name, ext(typeOfFolder))){
                        specificFiles.add(file)
                    }
                }
                pos++
            }

            _files.postValue(specificFiles)
        }
    }

    private fun ext(typeOfFolder: String): MutableList<String> {
        return when(typeOfFolder){
            "image" ->{
                Constant.imageEx
            }
            "audio" ->{
                Constant.musicEx
            }
            "video" ->{
                Constant.videoEx
            }
            else ->{
                Constant.docEx
            }
        }
    }
    private fun checkIfFileHasExtension(s: String, extend: MutableList<String>): Boolean {
        extend.forEach {
            if (s.endsWith(it))
                return true
            }
        return false
    }



    fun cut()
    {
        handleMode = HandleMode.CUT
    }

    fun copy()
    {
        handleMode = HandleMode.COPY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun paste()
    {
        if(handleMode == HandleMode.COPY)
        {
            val targetPath = currentDictionary?.toString() + "/" +selectedFile?.nameWithoutExtension +"."+ selectedFile?.extension
            val destPath = selectedFile?.absolutePath
            val targetFile = File(targetPath)
            val destFile = File(destPath)

            if(!targetFile.exists())
            {
                if(destFile.isDirectory)
                {
                    destFile.copyRecursively(targetFile,false)
                }
                else
                {
                    destFile.copyTo(targetFile,false)
                }

                _files.value?.add(targetFile)
                _files.postValue(_files.value)
            }

        }
        else if(handleMode == HandleMode.CUT)
        {
            val targetPath = currentDictionary?.toString() + "/" +selectedFile?.nameWithoutExtension +"."+ selectedFile?.extension
            val destPath = selectedFile?.absolutePath
            val targetFile = File(targetPath)
            val destFile = File(destPath)

            if(!targetFile.exists()){
                _files.value?.add(targetFile)
                _files.postValue(_files.value)
                destFile.renameTo(targetFile)
            }


        }
        handleMode = HandleMode.NONE
    }


    fun updateTypeOfFolder(type: String){
        stackPath.clear()
        typeOfFolder = type
        if (typeOfFolder == "download"){
            stackPath.push(Constant.pathDownload)
            getFiles(Constant.pathDownload)
            return
        }
        if (typeOfFolder == "other"){
            stackPath.push(Constant.path)
            getFiles(Constant.path)
            return
        }
        stackPath.push(Constant.path)
        getSpecificFiles()
    }

    fun changeStateLoading(){
        if (_stateLoading.value == true)
            _stateLoading.postValue(false)
//        else _stateLoading.postValue(true)
    }

    fun clear(){
        typeOfFolder = "other"
        stackPath.clear()
        _files.value?.clear()
        _files.postValue(_files.value)
        _isGrid.postValue(true)
        _stateLoading.postValue(true)
    }
    init {
        _stateLoading.postValue(true)
        _isGrid.postValue(true)
    }

}

