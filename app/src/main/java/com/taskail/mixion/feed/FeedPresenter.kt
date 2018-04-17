package com.taskail.mixion.feed

import android.util.Log
import com.taskail.mixion.FEED_STEEMJ_USER
import com.taskail.mixion.data.SteemitRepository
import com.taskail.mixion.User
import com.taskail.mixion.steemJ.RxSteemJManager
import java.util.*

/**
 *Created by ed on 1/24/18.
 */
class FeedPresenter(val feedView: FeedContract.View,
                    val steemitRepository: SteemitRepository) :
        FeedContract.Presenter {

    val TAG = javaClass.simpleName

    var sortBy = "Trending"

    init {
        feedView.presenter = this
        steemitRepository.remoteRepository.tag = "steemit"
    }

    /**
     * Start follows onResume from FeedFragment
     */
    override fun start() {
        firstCall()
        if (User.userIsLoggedIn) {
            RxSteemJManager.registerSteemJUser(FEED_STEEMJ_USER)
        }
    }

    override fun onDestroy() {
        if (User.userIsLoggedIn) {
            RxSteemJManager.deregisterSteemJUser(FEED_STEEMJ_USER)
        }
    }

    /**
     * get the current users feed, this will only be called
     * if there is a user logged in
     */
    override fun getMyFeed() {
        if (User.userIsLoggedIn){
            if (steemitRepository.remoteRepository.tag != getUserName()){
                steemitRepository.remoteRepository.tag = getUserName()!!
                feedView.discussionFromResponse.clear()
                feedView.clearItemsForNewFeed()
                fetchUserFeed()
            }
        }
    }

    private fun firstCall(){
        if (!feedIsLoaded()){

            if (User.userIsLoggedIn){
                steemitRepository.remoteRepository.tag = getUserName()!!
                fetchUserFeed()
            } else{
                fetch()
            }

        }
    }

    /**
     * This will sort the feed either by Hot, Trending, Promoted,
     * or New. if the @param[sortBy] is already selected nothing will happen.
     * we also need to clear the [steemitRepository.remoteRepository.tag] if we had
     * previously queried a user feed
     */
    override fun sortBy(sortBy: String) {
        if (this.sortBy != sortBy){
            this.sortBy = sortBy

            if (steemitRepository.remoteRepository.tag == getUserName()){
                steemitRepository.remoteRepository.tag = "steemit"
            }
            performCleanFetch()
        }
    }

    private fun performCleanFetch(){
        feedView.discussionFromResponse.clear()
        feedView.clearItemsForNewFeed()

        fetch()
    }

    override fun getByTag(tag: String) {
        if (steemitRepository.remoteRepository.tag != tag){
            steemitRepository.remoteRepository.tag = tag
            feedView.clearItemsForNewFeed()
            performCleanFetch()
        }
    }

    /**
     * this will only be called when the user logs in
     */
    override fun userStatus(loggedIn: Boolean) {
        if (loggedIn){
            feedView.userHasLoggedIn()
        }
    }

    private fun fetchUserFeed(){
         steemitRepository.getUserFeed({
             feedView.discussionFromResponse.addAll(it)
             feedView.showFeed()
             setToolbarTitle("My Feed")
         }, {
             Log.e(TAG, it.message)
         })
    }

    private fun fetch(){

        steemitRepository.getFeed(sortBy, {
            feedView.discussionFromResponse.addAll(it)
            feedView.showFeed()
            setToolbarTitle(sortBy)
        }, {
            Log.e(TAG, it.message)
        })

    }

    /**
     * If we are currently on the user's feed, got more items for the user's feed,
     * otherwise get from the regular feed.
     */
    override fun fetchMore(lastPostLocation: Int) {

        if (User.userIsLoggedIn && getUserName() == steemitRepository.remoteRepository.tag){

            getMoreUserFeed(lastPostLocation)

        } else {

            steemitRepository.getMoreFeed(sortBy, getStartAuthor(lastPostLocation),
                    getStartPermlink(lastPostLocation), {
                /**
                 * Skip the first item from the returned list as it will be the same item
                 * from the previous last item.
                 */
                Collections.addAll(feedView.discussionFromResponse, *Arrays.copyOfRange(it, 1, 10))

                feedView.showMoreFeed(lastPostLocation, feedView.discussionFromResponse.size)
            }, {
                Log.e(TAG, it.message)
            })
        }
    }

    private fun getMoreUserFeed(lastPostLocation: Int){

        steemitRepository.getMoreUserFeed(getStartAuthor(lastPostLocation),
                getStartPermlink(lastPostLocation),
                {
                    /**
                     * Skip the first item from the returned list as it will be the same item
                     * from the previous last item.
                     */
                    Collections.addAll(feedView.discussionFromResponse, *Arrays.copyOfRange(it, 1, 10))
                    feedView.showMoreFeed(lastPostLocation, feedView.discussionFromResponse.size)
                }, {
            Log.e(TAG, it.message)
        })

    }

    override fun getDtube(){
        getByTag("dtube")
        setToolbarTitle("dtube")
    }

    override fun getDmania(){
        Log.d(TAG, "Fetch dMania posts")

    }

    private fun getUserName(): String?{
        var user: String? = null
        return user ?: User.getUserName().apply { user = this }
    }
    
    private fun setToolbarTitle(title: String){
        if (steemitRepository.remoteRepository.tag == "dtube"){
            feedView.showFeedType("$sortBy @ Dtube")
        } else {
            feedView.showFeedType(title)
        }
    }

    /**
     * onResume will call a a query, check to see if items are empty before doing a network call
     */
    private fun feedIsLoaded(): Boolean{
        return feedView.discussionFromResponse.isNotEmpty()
    }

    /**
     * get the last author on the current list to fetch more items
     */
    private fun getStartAuthor(lastPostLocation: Int) : String{
        return feedView.discussionFromResponse[lastPostLocation-1].author
    }

    /**
     * get the last permlink on the current list to fetch more items
     */
    private fun getStartPermlink(lastPostLocation: Int) : String{
        return feedView.discussionFromResponse[lastPostLocation-1].permlink
    }
}