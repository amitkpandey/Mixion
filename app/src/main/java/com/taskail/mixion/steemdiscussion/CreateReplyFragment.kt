package com.taskail.mixion.steemdiscussion

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.taskail.mixion.R
import com.taskail.mixion.ui.animation.DismissableAnimation
import com.taskail.mixion.ui.animation.RevealAnimationSettings
import com.taskail.mixion.ui.animation.registerCircularRevealAnimation
import com.taskail.mixion.ui.animation.startCircularExitAnimation

/**
 *Created by ed on 3/29/18.
 */

class CreateReplyFragment : Fragment(), DismissableAnimation {

    companion object {

        const val ARG_REVEAL = "args_reveal"

        fun newInstance(revealAnimationSettings: RevealAnimationSettings) : CreateReplyFragment {
            val bundle = Bundle()
            bundle.putParcelable(ARG_REVEAL, revealAnimationSettings)
            val fragment = CreateReplyFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_reply, container, false)

        val revealAnim: RevealAnimationSettings = arguments?.getParcelable(ARG_REVEAL)!!
        registerCircularRevealAnimation(view!!,
                revealAnim,
                ContextCompat.getColor(context!!, R.color.primary),
                ContextCompat.getColor(context!!, R.color.white))

        return  view
    }


    override fun dismiss(listner: DismissableAnimation.OnDismissedListener) {
        val revealAnim: RevealAnimationSettings = arguments?.getParcelable(ARG_REVEAL)!!
        startCircularExitAnimation(view!!,
                revealAnim,
                ContextCompat.getColor(context!!, R.color.white),
                ContextCompat.getColor(context!!, R.color.primary),
                object : DismissableAnimation.OnDismissedListener {
                    override fun onDismissed() {
                        listner.onDismissed()
                    }

                })
    }

}