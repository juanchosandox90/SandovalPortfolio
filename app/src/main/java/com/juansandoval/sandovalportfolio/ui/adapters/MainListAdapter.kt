package com.juansandoval.sandovalportfolio.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.data.Skills
import com.juansandoval.sandovalportfolio.utils.*

class MainListAdapter(context: Context) : RecyclerView.Adapter<MainListAdapter.ListViewHolder>() {

    private var animationPlaybackSpeed: Double = 0.7

    private val listItemHorizontalPadding: Float by bindDimen(
        context,
        R.dimen.list_item_horizontal_padding
    )
    private val listItemVerticalPadding: Float by bindDimen(
        context,
        R.dimen.list_item_vertical_padding
    )
    private val originalWidth = context.screenWidth - 48.dp
    private val expandedWidth = context.screenWidth - 24.dp
    private var originalHeight = -1 // will be calculated dynamically
    private var expandedHeight = -1 // will be calculated dynamically

    private val listItemExpandDuration: Long get() = (300L / animationPlaybackSpeed).toLong()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private lateinit var recyclerView: RecyclerView
    private var expandedModel: Skills? = null
    private var isScaledDown = false

    private val modelList = listOf(
        Skills("Kotlin", "85%", "Corutines", "Extensions"),
        Skills("Java", "90%", "Java 8", "Lambdas"),
        Skills("Android", "90%", "AndroidX", "Sensors"),
        Skills("MVVM", "70%", "ViewModel", "Lifecycle"),
        Skills("MVP", "80%", "Presenter", "View"),
        Skills("RxAndroid", "70%", "RxJava", "RxKotlin"),
        Skills("JetPack", "80%", "Navigation", "Room")
    )
    private val adapterList: List<Skills> = modelList

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val expandView: View by bindView(R.id.expand_view)
        val chevron: View by bindView(R.id.chevron)
        val cardContainer: View by bindView(R.id.card_container)
        val scaleContainer: View by bindView(R.id.scale_container)
        val listItemFg: View by bindView(R.id.list_item_fg)
        val title: TextView by bindView(R.id.title)
        val subtitlePercentage: TextView by bindView(R.id.subtitle2)
        val subtitleskill: TextView by bindView(R.id.subtitleskill)
        val subtitleskill2: TextView by bindView(R.id.subtitleskill2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder =
        ListViewHolder(inflater.inflate(R.layout.item_list, parent, false))

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int = adapterList.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val model = adapterList[position]

        expandItem(holder, model == expandedModel, animate = false)
        scaleDownItem(holder, position, isScaledDown)
        holder.title.text = model.skill_title
        holder.subtitlePercentage.text = model.skill_percentage
        holder.subtitleskill.text = model.subskill_title
        holder.subtitleskill2.text = model.subskill_title2
        holder.cardContainer.setOnClickListener {
            when (expandedModel) {
                null -> {

                    // expand clicked view
                    expandItem(holder, expand = true, animate = true)
                    expandedModel = model
                }
                model -> {

                    // collapse clicked view
                    expandItem(holder, expand = false, animate = true)
                    expandedModel = null
                }
                else -> {

                    // collapse previously expanded view
                    val expandedModelPosition = adapterList.indexOf(expandedModel!!)
                    val oldViewHolder =
                        recyclerView.findViewHolderForAdapterPosition(expandedModelPosition) as? ListViewHolder
                    if (oldViewHolder != null) expandItem(
                        oldViewHolder,
                        expand = false,
                        animate = true
                    )

                    // expand clicked view
                    expandItem(holder, expand = true, animate = true)
                    expandedModel = model
                }
            }
        }
    }

    private fun expandItem(holder: ListViewHolder, expand: Boolean, animate: Boolean) {
        if (animate) {
            val animator = getValueAnimator(
                expand, listItemExpandDuration, AccelerateDecelerateInterpolator()
            ) { progress -> setExpandProgress(holder, progress) }

            if (expand) animator.doOnStart { holder.expandView.isVisible = true }
            else animator.doOnEnd { holder.expandView.isVisible = false }

            animator.start()
        } else {

            // show expandView only if we have expandedHeight (onViewAttached)
            holder.expandView.isVisible = expand && expandedHeight >= 0
            setExpandProgress(holder, if (expand) 1f else 0f)
        }
    }

    override fun onViewAttachedToWindow(holder: ListViewHolder) {
        super.onViewAttachedToWindow(holder)

        // get originalHeight & expandedHeight if not gotten before
        if (expandedHeight < 0) {
            expandedHeight = 0 // so that this block is only called once

            holder.cardContainer.doOnLayout { view ->
                originalHeight = view.height

                // show expandView and record expandedHeight in next layout pass
                // (doOnPreDraw) and hide it immediately. We use onPreDraw because
                // it's called after layout is done. doOnNextLayout is called during
                // layout phase which causes issues with hiding expandView.
                holder.expandView.isVisible = true
                view.doOnPreDraw {
                    expandedHeight = view.height
                    holder.expandView.isVisible = false
                }
            }
        }
    }

    private fun setExpandProgress(holder: ListViewHolder, progress: Float) {
        if (expandedHeight > 0 && originalHeight > 0) {
            holder.cardContainer.layoutParams.height =
                (originalHeight + (expandedHeight - originalHeight) * progress).toInt()
        }
        holder.cardContainer.layoutParams.width =
            (originalWidth + (expandedWidth - originalWidth) * progress).toInt()

        //  holder.cardContainer.setBackgroundColor(blendColors(originalBg, expandedBg, progress))
        holder.cardContainer.requestLayout()

        holder.chevron.rotation = 90 * progress
    }

    ///////////////////////////////////////////////////////////////////////////
    // Scale Down Animation
    ///////////////////////////////////////////////////////////////////////////

    private inline val LinearLayoutManager.visibleItemsRange: IntRange
        get() = findFirstVisibleItemPosition()..findLastVisibleItemPosition()

    private fun setScaleDownProgress(holder: ListViewHolder, position: Int, progress: Float) {
        val itemExpanded = position >= 0 && adapterList[position] == expandedModel
        holder.cardContainer.layoutParams.apply {
            width =
                ((if (itemExpanded) expandedWidth else originalWidth) * (1 - 0.1f * progress)).toInt()
            height =
                ((if (itemExpanded) expandedHeight else originalHeight) * (1 - 0.1f * progress)).toInt()
        }
        holder.cardContainer.requestLayout()

        holder.scaleContainer.scaleX = 1 - 0.05f * progress
        holder.scaleContainer.scaleY = 1 - 0.05f * progress

        holder.scaleContainer.setPadding(
            (listItemHorizontalPadding * (1 - 0.2f * progress)).toInt(),
            (listItemVerticalPadding * (1 - 0.2f * progress)).toInt(),
            (listItemHorizontalPadding * (1 - 0.2f * progress)).toInt(),
            (listItemVerticalPadding * (1 - 0.2f * progress)).toInt()
        )

        holder.listItemFg.alpha = progress
    }

    /** Convenience method for calling from onBindViewHolder */
    private fun scaleDownItem(holder: ListViewHolder, position: Int, isScaleDown: Boolean) {
        setScaleDownProgress(holder, position, if (isScaleDown) 1f else 0f)
    }
}