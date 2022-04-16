package com.example.file_manager.activity


import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import com.example.file_manager.common.Constant
import com.example.file_manager.databinding.ActivityHomeBinding
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber


class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.d("Check permission")
        if (checkPermission()) {
            Constant.path = Environment.getExternalStorageDirectory().path
            Constant.pathDownload =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
            Timber.e(Constant.path)
        } else {
            requestPermission()
        }

        with(binding) {

            Timber.d("Calculate storage")
            val internalStatFs = StatFs(Environment.getRootDirectory().absolutePath)
            val externalStatFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
            val KILOBYTE = 1024L

            val internalTotal: Long =
                (internalStatFs.blockCountLong * internalStatFs.blockSizeLong) / (KILOBYTE * KILOBYTE * KILOBYTE)
            val internalFree: Long =
                (internalStatFs.availableBlocksLong * internalStatFs.blockSizeLong) / (KILOBYTE * KILOBYTE * KILOBYTE)

            val externalTotal: Long =
                (externalStatFs.blockCountLong * externalStatFs.blockSizeLong) / (KILOBYTE * KILOBYTE * KILOBYTE)
            val externalFree: Long =
                (externalStatFs.availableBlocksLong * externalStatFs.blockSizeLong) / (KILOBYTE * KILOBYTE * KILOBYTE)

            val total: Long = internalTotal + externalTotal
            val free: Long = internalFree + externalFree
            val used: Long = total - free

            percentageOfUsed.max = 100
            percentageOfUsed.progress = (100f * (used * 1f / total)).toInt()

            detailOfUsed.text = "${(total - free).toInt()}GB of ${total.toInt()}GB used"


            val intent = Intent(this@HomeActivity, FolderDetailActivity::class.java)
            btnOtherFolder.setOnClickListener {
                intent.putExtra("typefolder", "other")
                startActivity(intent)
            }
            btnImageFolder.setOnClickListener {
                intent.putExtra("typefolder", "image")
                startActivity(intent)
            }
            btnAudioFolder.setOnClickListener {
                intent.putExtra("typefolder", "audio")
                startActivity(intent)
            }
            btnVideoFolder.setOnClickListener {
                intent.putExtra("typefolder", "video")
                startActivity(intent)
            }
            btnDocumentFolder.setOnClickListener {
                intent.putExtra("typefolder", "document")
                startActivity(intent)
            }
            btnDownloadFolder.setOnClickListener {
                intent.putExtra("typefolder", "download")
                startActivity(intent)
            }

        }
    }


    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            (EasyPermissions.hasPermissions(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ))
        } else {
            (EasyPermissions.hasPermissions(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ))
        }

    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your storage",
                111,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your storage",
                111,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }
}
