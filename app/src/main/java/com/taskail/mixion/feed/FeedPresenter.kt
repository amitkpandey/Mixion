package com.taskail.mixion.feed

import com.taskail.mixion.data.SteemitDataSource
import com.taskail.mixion.data.SteemitRepository
import com.taskail.mixion.data.models.SteemDiscussion
import com.taskail.mixion.profile.User
import java.util.*

/**
 *Created by ed on 1/24/18.
 */
class FeedPresenter(val feedView: FeedContract.View,
                    val steemitRepository: SteemitRepository) :
        FeedContract.Presenter {

    var discussionFromResponse = ArrayList<SteemDiscussion>()
    var sortBy = "Trending"

    init {
        feedView.presenter = this
        feedView.discussionFromResponse = discussionFromResponse

        steemitRepository.remoteRepository.tag = "steemit"
    }

    /**
     * Start follows onResume from FeedFragment
     */
    override fun start() {
        firstCall()
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
                feedView.clearItems()
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
        feedView.clearItems()

        fetch()
    }

    override fun getByTag(tag: String) {
        if (steemitRepository.remoteRepository.tag != tag){
            steemitRepository.remoteRepository.tag = tag
            feedView.clearItems()
            performCleanFetch()
        }
    }

    private fun fetchUserFeed(){
         steemitRepository.getUserFeed(object : SteemitDataSource.DataLoadedCallback<SteemDiscussion>{
             override fun onDataLoaded(list: List<SteemDiscussion>) {
             }

             override fun onDataLoaded(array: Array<SteemDiscussion>) {
                 feedView.discussionFromResponse.addAll(array)
                 feedView.showFeed()
             }

             override fun onLoadError(error: Throwable) {
             }

         })
    }

    private fun fetch(){

        steemitRepository.getFeed(object : SteemitDataSource.DataLoadedCallback<SteemDiscussion>{
            override fun onDataLoaded(list: List<SteemDiscussion>) {
            }

            override fun onDataLoaded(array: Array<SteemDiscussion>) {
                feedView.discussionFromResponse.addAll(array)
                feedView.showFeed()
            }

            override fun onLoadError(error: Throwable) {
            }

        }, sortBy)

    }

    /**
     * If we are currently on the user's feed, got more items for the user's feed,
     * otherwise get from the regular feed.
     */
    override fun fetchMore(lastPostLocation: Int) {

        if (User.userIsLoggedIn && getUserName() == steemitRepository.remoteRepository.tag){

            getMoreUserFeed(lastPostLocation)

        } else {

            steemitRepository.getMoreFeed(object : SteemitDataSource.DataLoadedCallback<SteemDiscussion> {
                override fun onDataLoaded(list: List<SteemDiscussion>) {
                }

                override fun onDataLoaded(array: Array<SteemDiscussion>) {

                    /**
                     * Skip the first item from the returned list as it will be the same item
                     * from the previous last item.
                     */
                    Collections.addAll(feedView.discussionFromResponse, *Arrays.copyOfRange(array, 1, 10))

                    feedView.showMoreFeed(lastPostLocation, feedView.discussionFromResponse.size)
                }

                override fun onLoadError(error: Throwable) {
                }

            }, sortBy, getStartAuthor(lastPostLocation), getStartPermlink(lastPostLocation))
        }
    }

    private fun getMoreUserFeed(lastPostLocation: Int){

        steemitRepository.getMoreUserFeed(getStartAuthor(lastPostLocation), getStartPermlink(lastPostLocation),
                object : SteemitDataSource.DataLoadedCallback<SteemDiscussion> {

            override fun onDataLoaded(list: List<SteemDiscussion>) {
            }

            override fun onDataLoaded(array: Array<SteemDiscussion>) {

                /**
                 * Skip the first item from the returned list as it will be the same item
                 * from the previous last item.
                 */
                Collections.addAll(feedView.discussionFromResponse, *Arrays.copyOfRange(array, 1, 10))

                feedView.showMoreFeed(lastPostLocation, feedView.discussionFromResponse.size)
            }

            override fun onLoadError(error: Throwable) {
            }

        })

    }

    private fun getUserName(): String?{
        var user: String? = null
        return user ?: User.getUserName().apply { user = this }
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