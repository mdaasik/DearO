    package com.carworkz.dearo.carpm

    import android.annotation.SuppressLint
    import android.app.Dialog
    import android.content.Context
    import android.os.Bundle
    import android.view.KeyEvent
    import android.view.WindowManager
    import android.widget.ProgressBar
    import android.widget.TextView
    import com.carworkz.dearo.R

    class ProgressDialog (context: Context) : Dialog(context) {

        private lateinit var progressBar: ProgressBar
        private lateinit var progressText: TextView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.dialog_progress)

            progressBar = findViewById(R.id.Prog)
            progressText = findViewById(R.id.txtper)

            setCancelable(false)
            setCanceledOnTouchOutside(false)

            window?.setBackgroundDrawableResource(android.R.color.transparent)

            // Disable touches outside the dialog
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            )
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            )

            // Prevent back button press from dismissing the dialog
            setOnKeyListener { _, keyCode, _ ->
                keyCode == KeyEvent.KEYCODE_BACK
            }

           resetProgress()
        }

        @SuppressLint("SetTextI18n")
        fun updateProgress(progress: Int) {
            val roundedProgress = progress.coerceIn(0, 100)
            progressBar.progress = roundedProgress
            progressText.text = "$roundedProgress%"
        }

        fun resetProgress() {
            progressBar.progress = 0
            progressText.text = "0%"
        }
    }