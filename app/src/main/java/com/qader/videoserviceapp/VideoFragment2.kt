package com.qader.videoserviceapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView

class VideoFragment2 : Fragment() {

    private lateinit var playerView: PlayerView

    @OptIn(UnstableApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video2, container, false)

        // Initialize the PlayerView
        playerView = view.findViewById(R.id.player_view)

        // Attach the player instance from VideoService
        playerView.player = VideoService.playerInstance

        // Handle the previous fragment button click
        view.findViewById<Button>(R.id.previous_fragment).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Detach the player from the PlayerView to prevent memory leaks
        playerView.player = null
    }
}
