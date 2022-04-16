package com.example.file_manager.fragment.listAllFile

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.file_manager.R
import com.example.file_manager.databinding.ItemFileBinding
import java.io.File
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.file_manager.BuildConfig
import com.example.file_manager.BuildConfig.APPLICATION_ID
import com.example.file_manager.common.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption


class AllFileAdapter(private val context: Context, private var onItemClick: (String) -> Unit)
    : RecyclerView.Adapter<AllFileAdapter.AllFileViewHolder>() {

   lateinit var path : String

    /**
     class handle MenuMode
     */
    var handleMenuMode: ClassHandleMenuMode? = null
    @JvmName("setHandleMenuMode1")
    fun setHandleMenuMode(handleMenuMode: ClassHandleMenuMode)
    {
        this.handleMenuMode = handleMenuMode
    }

    var listFile = ArrayList<File>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class AllFileViewHolder(private val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(file: File) {
            path = file.path
            with(binding){
                if(FileListViewModel.isGrid.value == false && file.name.length > 5)
                    txtFileName.text = file.name.substring(0, 5)
                else
                    txtFileName.text = file.name
                GlobalScope.launch(Dispatchers.Main) {
                    if (file.isDirectory) {
                        imgIconFile.setImageResource(R.drawable.ic_folder)
                    } else {
                        val url = withContext(Dispatchers.IO){
                            FileProvider.getUriForFile(
                                context,
                                BuildConfig.APPLICATION_ID + ".provider",
                                file
                            )
                        }
                        when {
                            checkIfFileHasExtension(url.toString(), Constant.musicEx) -> {
                                imgIconFile.setImageResource(R.drawable.ic_audio)
                            }
                            checkIfFileHasExtension(url.toString(), Constant.imageEx) -> {
                                imgIconFile.setImageResource(R.drawable.ic_image)
                            }
                            checkIfFileHasExtension(url.toString(), Constant.videoEx) -> {
                                imgIconFile.setImageResource(R.drawable.ic_video)
                            }
                            else -> {
                                imgIconFile.setImageResource(R.drawable.ic_document)
                            }
                        }
                    }
                }



                root.setOnClickListener {
                    if(FileListViewModel.menuMode != MenuMode.SELECT)
                    {
                        if (file.isDirectory) {
                            onItemClick(file.path)
                        } else {
                            openFile(file, binding.root.context)
                        }
                    }
                    else
                    {
                        Toast.makeText(context,"You must select option",Toast.LENGTH_LONG).show()
                    }


                }

                /*
                Handle long click
                long click -> view 3 options: move, share, delete
                In this branch, handle only move
                 */
                root.setOnLongClickListener {
                    if(FileListViewModel.menuMode == MenuMode.SELECT)
                    {
                        FileListViewModel.selectedFile = File("")
                    }
                    else
                    {
                        FileListViewModel.selectedFile = file
                    }
                    notifyDataSetChanged()
                    if(FileListViewModel.menuMode != MenuMode.OPEN)
                    {
                        handleMenuMode?.changeMenuMode(MenuMode.OPEN)
                    }
                    else
                    {
                        handleMenuMode?.changeMenuMode(MenuMode.SELECT)
                    }
                    true
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun Path.exists() : Boolean = Files.exists(this)

        @RequiresApi(Build.VERSION_CODES.O)
        fun Path.isFile() : Boolean = !Files.isDirectory(this)


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AllFileViewHolder, position: Int) {
        holder.bind(listFile[position])
        if(listFile[position].absolutePath != FileListViewModel.selectedFile?.absolutePath)
        {
//            binding.cbSelected.visibility = View.GONE
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }
        else
        {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.action_bar_1))
//            binding.cbSelected.visibility = View.VISIBLE
//            binding.cbSelected.isChecked=true
        }
    }

    override fun getItemCount(): Int {
        return listFile.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllFileViewHolder {
        val binding = ItemFileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AllFileViewHolder(binding)
    }

    private fun openFile(file: File, context: Context) {

        Timber.d("-----"+file.absolutePath+"-----")
        val url = FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID+ ".provider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        GlobalScope.launch(Dispatchers.IO) {
            when {
                checkIfFileHasExtension(url.toString(), Constant.wordEx) -> {
                    Timber.d("Open word document")
                    intent.setDataAndType(url, "application/msword")
                }
                checkIfFileHasExtension(url.toString(), Constant.pdfEx) -> {
                    Timber.d("Open PDF file")
                    intent.setDataAndType(url, "application/pdf")
                }
                checkIfFileHasExtension(url.toString(), Constant.pptEx) -> {
                    Timber.d("Open Powerpoint file")
                    intent.setDataAndType(url, "application/vnd.ms-powerpoint")
                }
                checkIfFileHasExtension(url.toString(), Constant.excelEx) -> {
                    Timber.d("Open Excel file")
                    intent.setDataAndType(url, "application/vnd.ms-excel")
                }
                checkIfFileHasExtension(url.toString(), Constant.compressEx) -> {
                    Timber.d("Open zip file")
                }
                checkIfFileHasExtension(url.toString(), Constant.musicEx) -> {
                    Timber.d("Open audio file")
                    intent.setDataAndType(url, "audio/x-wav")
                }
                checkIfFileHasExtension(url.toString(), Constant.imageEx) -> {
                    Timber.d("Open image file")
                    intent.setDataAndType(url, "image/*")
                }
                checkIfFileHasExtension(url.toString(), Constant.plainTextEx) -> {
                    Timber.d("Open plain text file")
                    intent.setDataAndType(url, "text/plain")
                }
                checkIfFileHasExtension(url.toString(), Constant.gifEx) -> {
                    Timber.d("Open gif file")
                    intent.setDataAndType(url, "image/gif")
                }
                checkIfFileHasExtension(url.toString(), Constant.videoEx) -> {
                    Timber.d("Open video file")
                    intent.setDataAndType(url, "video/*")
                }
                else -> {
                    Timber.d("Open other files")
                    intent.setDataAndType(url, "*/*")
                }
            }
            try {
                if (file.exists()) context.startActivity(
                    Intent.createChooser(intent, "Open with")
                ) else Toast.makeText(context, "File is corrupted", Toast.LENGTH_LONG).show()
            } catch (ex: Exception) {
                Toast.makeText(
                    context,
                    "No Application is found to open this file. The File is saved at",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private suspend fun checkIfFileHasExtension(s: String, extend: MutableList<String>): Boolean {
        return withContext(Dispatchers.IO){
            extend.forEach {
                if (s.endsWith(it))
                    return@withContext true
            }
            false
        }
    }
}

