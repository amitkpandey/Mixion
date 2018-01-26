package com.taskail.mixion.feed

import com.taskail.mixion.BasePresenter
import com.taskail.mixion.BaseView
import com.taskail.mixion.data.models.SteemDiscussion
import java.util.ArrayList

/**
 *Created by ed on 1/24/18.
 */
interface FeedContract {

    interface View : BaseView<Presenter>{

        var discussionFromResponse: ArrayList<SteemDiscussion>

        fun showFeed()

        fun clearItems()

        fun showMoreFeed(previousSize: Int, newSize: Int)

    }

    interface Presenter : BasePresenter{

        fun getNew()

        fun getHot()

        fun getTrending()

        fun getPromoted()

        fun fetchMore(lastPostLocation: Int)
    }
}