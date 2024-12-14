package com.qader.videoserviceapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.media3.common.util.UnstableApi

class VideoFragment1 : Fragment() {

    private lateinit var seekBar: SeekBar
    private lateinit var currentTimeTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var isSeeking = false

    @SuppressLint("MissingInflatedId")
    @OptIn(UnstableApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video1, container, false)

        seekBar = view.findViewById(R.id.seek_bar)
        currentTimeTextView = view.findViewById(R.id.current_time)
        val startButton = view.findViewById<Button>(R.id.start_video)

        startButton.setOnClickListener {
            val intent = Intent(requireContext(), VideoService::class.java)
            intent.putExtra("VIDEO_URI", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
            requireContext().startService(intent)
        }

        view.findViewById<Button>(R.id.next_fragment).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, VideoFragment2())
                .addToBackStack(null)
                .commit()
        }

        setupSeekBarListener()
        updateSeekBar()
        return view
    }

    @OptIn(UnstableApi::class)
    private fun updateSeekBar() {
        if (VideoService.playerInstance != null) {
            seekBar.visibility = View.VISIBLE
            currentTimeTextView.visibility = View.VISIBLE
            val player = VideoService.playerInstance

            handler.postDelayed(object : Runnable {
                override fun run() {
                    if (!isSeeking) {
                        player?.let {
                            seekBar.max = it.duration.toInt()
                            seekBar.progress = it.currentPosition.toInt()
                            currentTimeTextView.text = formatTime(it.currentPosition)
                        }
                    }
                    handler.postDelayed(this, 1000)
                }
            }, 1000)
        } else {
            seekBar.visibility = View.GONE
        }
    }

    private fun setupSeekBarListener() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentTimeTextView.text = formatTime(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeeking = true
            }

            @OptIn(UnstableApi::class)
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isSeeking = false
                VideoService.playerInstance?.seekTo(seekBar?.progress?.toLong() ?: 0L)
            }
        })
    }

    private fun formatTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        val hours = (milliseconds / (1000 * 60 * 60))

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}


//package com.qader.videoserviceapp
//
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.SeekBar
//import androidx.annotation.OptIn
//import androidx.fragment.app.Fragment
//import androidx.media3.common.util.UnstableApi
//
//class VideoFragment1 : Fragment() {
//
//    private lateinit var seekBar: SeekBar
//    private val handler = Handler(Looper.getMainLooper())
//
//    @OptIn(UnstableApi::class)
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_video1, container, false)
//        seekBar = view.findViewById(R.id.seek_bar)
//        val startButton = view.findViewById<Button>(R.id.start_video)
//
//        startButton.setOnClickListener {
//            Log.v("rtttt","startButton.setOnClickListener")
//            val intent = Intent(requireContext(), VideoService::class.java)
//            intent.putExtra("VIDEO_URI", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
//            requireContext().startService(intent)
//        }
//
//        view.findViewById<Button>(R.id.next_fragment).setOnClickListener {
//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, VideoFragment2())
//                .addToBackStack(null)
//                .commit()
//        }
//
//        updateSeekBar()
//        return view
//    }
//
//    @OptIn(UnstableApi::class)
//    private fun updateSeekBar() {
//        if (VideoService.playerInstance != null) {
//            // Make the seek bar visible if the player is running
//            seekBar.visibility = View.VISIBLE
//
//            // Get the player instance
//            val player = VideoService.playerInstance
//
//            // Update the seek bar periodically
//            handler.postDelayed(object : Runnable {
//                override fun run() {
//                    player?.let {
//                        seekBar.progress = it.currentPosition.toInt()
//                        seekBar.max = it.duration.toInt()
//                        handler.postDelayed(this, 1000)
//                    }
//                }
//            }, 1000)
//        } else {
//            // Hide the seek bar if the player is not running
//            seekBar.visibility = View.GONE
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        handler.removeCallbacksAndMessages(null)
//    }
//}
