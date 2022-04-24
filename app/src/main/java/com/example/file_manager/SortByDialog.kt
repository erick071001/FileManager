package com.example.file_manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.file_manager.databinding.SortFileBinding
import com.example.file_manager.fragment.listAllFile.FileListViewModel


class SortByDialog: DialogFragment() {
    private lateinit var binding: SortFileBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SortFileBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAtoZ.setOnClickListener {
            FileListViewModel.sortFileAtoZ()
            dismiss()
        }
        binding.btnZtoA.setOnClickListener {
            FileListViewModel.sortFileZtoA()
            dismiss()
        }
        binding.btnLatest.setOnClickListener {
            FileListViewModel.sortFileLatest()
            dismiss()
        }
        binding.btnEarliest.setOnClickListener {
            FileListViewModel.sortFileEarliest()
            dismiss()
        }
        binding.btnCancelSort.setOnClickListener {
            dismiss()
        }
    }
}