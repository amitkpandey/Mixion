package com.taskail.mixion.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.taskail.mixion.R
import com.taskail.mixion.adapters.ViewPagerAdapter
import com.taskail.mixion.data.SteemitRepository
import com.taskail.mixion.data.source.RemoteDataSource
import com.taskail.mixion.data.source.SteemAPI
import com.taskail.mixion.data.source.getRetrofitClient
import com.taskail.mixion.feed.FeedFragment
import com.taskail.mixion.feed.FeedPresenter
import com.taskail.mixion.utils.getCallback
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_main.*

/**
 *Created by ed on 1/19/18.
 */
class MainFragment : Fragment(), FeedFragment.Callback {

    val TAG = "MainFragment"

    override fun onFeedSearchRequested() {
        Log.d(TAG, "Feed Search Requested")
    }

    override fun onAccountRequested() {
        Log.d(TAG, "account Requested")
    }

    override fun getFilterMenuAnchor(): View? {
        return getCallback()?.getFilterMenuAnchor()
    }

    interface Callback{
        fun getFilterMenuAnchor(): View?
    }
    private var feedDisposable = CompositeDisposable()
    private var steemitRepository: SteemitRepository? = null
    private var steemitAPI: SteemAPI? = null
    private val FEED_FRAGMENT = 0
    private lateinit var feedPresenter: FeedPresenter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pagerAdapter = ViewPagerAdapter(childFragmentManager)

        initViews(pagerAdapter)
    }

    private fun initViews(adapter: ViewPagerAdapter){

        val feedFragment = FeedFragment.getInstance()
        adapter.addFragment(feedFragment, "feed Fragment").also {
            feedPresenter = FeedPresenter(feedFragment, createRepo())
        }

        lockableViewPager.adapter = adapter
        lockableViewPager.currentItem = FEED_FRAGMENT
    }

    private fun createRepo(): SteemitRepository{
        return steemitRepository ?:
        SteemitRepository.getInstance(createRemoteRepo()).apply {
            steemitRepository = this
        }
    }

    private fun createRemoteRepo() : RemoteDataSource{
        return RemoteDataSource(feedDisposable, createSteemApi())
    }

    private fun createSteemApi() : SteemAPI{
        return steemitAPI ?: getRetrofitClient().create(SteemAPI::class.java).apply {
            steemitAPI = this
        }
    }

    companion object {
        @JvmStatic fun newInstance(): MainFragment{
            val fragment = MainFragment()
            fragment.retainInstance = true
            return fragment
        }
    }

    override fun onDestroy() {
        feedDisposable.dispose()
        super.onDestroy()
    }

    private fun getCallback(): Callback? {
        return getCallback(this, Callback::class.java)
    }
}
