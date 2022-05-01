package com.example.file_manager.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.file_manager.common.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object SearchResultViewModel : ViewModel(){

    private val _resultFiles: MutableLiveData<ArrayList<File>> = MutableLiveData()
    val resultFiles: LiveData<ArrayList<File>> = _resultFiles
    //mở folder
    fun openFolder(path: String){
        getFiles(path)
    }

    //lấy file khi mở folder
    private fun getFiles(path: String) {
        viewModelScope.launch(Dispatchers.IO){
            val listFiles = ArrayList<File>()
            File(path).listFiles()?.let{
                for(file in it){
                    listFiles.add(file)
                }
            }
            _resultFiles.postValue(listFiles)
        }
    }

    //tìm kiếm file và folder khi search
    fun getSearchFile(name: String) {
        viewModelScope.launch(Dispatchers.IO){
            val listFile = ArrayList<File>()
            listFile.add(File(Constant.path))
            val resultFiles = ArrayList<File>()
            var pos = 0
            while (pos < listFile.size){
                val file = listFile[pos]
                if (file.name.lowercase().contains(name)){
                    resultFiles.add(file)
                }
                if (file.isDirectory){
                    file.listFiles()?.let { listFile.addAll(it) }
                }
                pos++
            }
            Timber.e("${resultFiles.size}")
            _resultFiles.postValue(resultFiles)
        }
    }

    //xóa
    fun clear(){
        _resultFiles.value?.clear()
        _resultFiles.postValue(_resultFiles.value)
    }
}