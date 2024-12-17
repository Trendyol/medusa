package com.trendyol.medusa

import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer

class SamplePreloadFragment : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_sample_preload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startTimer()
    }

    private fun startTimer() {
        getChronometer().apply {
            base = SystemClock.elapsedRealtime()
            start()
        }
    }

    private fun stopTimer() {
        getChronometer().stop()
    }

    override fun onDestroyView() {
        stopTimer()
        super.onDestroyView()
    }

    private fun getChronometer(): Chronometer = requireView().findViewById(R.id.chronometer)

    companion object {
        const val TAG = "SamplePreloadFragmentTag"
        fun newInstance(): SamplePreloadFragment = SamplePreloadFragment()
    }
}