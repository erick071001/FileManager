package com.example.file_manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.file_manager.databinding.NewDirectoryBinding
import com.example.file_manager.fragment.listAllFile.FileListViewModel

class CreateFolderDialog : DialogFragment(){
    private lateinit var binding: NewDirectoryBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NewDirectoryBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            if (!binding.edtDirName.text.isNullOrBlank()){
                FileListViewModel.createFolder(binding.edtDirName.text.toString())
                dismiss()
            }
        }
    }
}