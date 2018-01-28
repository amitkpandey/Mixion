package com.taskail.mixion.feed

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.taskail.mixion.R
import com.taskail.mixion.data.models.SteemDiscussion
import com.taskail.mixion.utils.GetTimeAgo
import com.taskail.mixion.utils.StringUtils
import com.taskail.mixion.utils.getFirstImgFromJsonMeta
import kotlinx.android.synthetic.main.cardview_steem_post.view.*
import kotlinx.android.synthetic.main.layout_bottom_card_buttons.view.*

/**
 *Created by ed on 1/24/18.
 */
class FeedRVAdapter(private val steemDiscussion: List<SteemDiscussion>,
                    private val callBack: FeedAdapterCallBack) :
        RecyclerView.Adapter<FeedRVAdapter.FeedViewHolder>() {

    class FeedViewHolder(itemView : View, private val callBack: FeedAdapterCallBack) :
            RecyclerView.ViewHolder(itemView){

        fun setSteemDiscussion(discussion: SteemDiscussion){

            with(discussion){
                itemView.steemTitle.text = title
                itemView.steem_body.text = StringUtils.getShorterBody(body)
                itemView.text_timeago.text = GetTimeAgo.getlongtoago(created)
                itemView.payout.text = pendingPayoutValue.replace("SBD", "")
                itemView.votes_count.text = netVotes.toString()
                itemView.replies_count.text = children.toString()

                Glide.with(itemView.rootView)
                        .load(jsonMetadata.getFirstImgFromJsonMeta())
                        .into(itemView.preview_image)

                itemView.setOnClickListener {
                    callBack.onDiscussionSelected(this)
                }
            }

        }
    }

    interface FeedAdapterCallBack{
        fun onDiscussionSelected(discussion: SteemDiscussion)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeedViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.cardview_steem_post,
                parent, false)
        return FeedRVAdapter.FeedViewHolder(itemView, callBack)
    }

    override fun getItemCount(): Int {
        return steemDiscussion.size
    }

    override fun onBindViewHolder(holder: FeedViewHolder?, position: Int) {
        val discussion = steemDiscussion[position]
        holder?.setSteemDiscussion(discussion)
    }
}