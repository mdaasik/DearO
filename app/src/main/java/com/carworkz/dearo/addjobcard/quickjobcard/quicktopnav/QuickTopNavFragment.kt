package com.carworkz.dearo.addjobcard.quickjobcard.quicktopnav

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.quickjobcard.NavigationInteraction
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.databinding.LayoutShortJcTopNavBinding
import com.carworkz.dearo.domain.entities.JobCard
/*import kotlinx.android.synthetic.main.layout_short_jc_top_nav.**/
import timber.log.Timber

class QuickTopNavFragment : BaseFragment() {
    private lateinit var binding: LayoutShortJcTopNavBinding
    private lateinit var jobCardId: String
    private lateinit var jobCardStatus: String
    private var isViewOnly = false
    private lateinit var jobCard: JobCard
    private val sections = arrayListOf<Section>()

    private lateinit var interaction: NavigationInteraction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jobCardId = it.getString(ARG_JOB_CARD_ID)!!
            jobCardStatus = it.getString(ARG_STATUS)!!
            isViewOnly = it.getBoolean(ARG_IS_VIEW_ONLY)
            jobCard = it.getParcelable(ARG_JOB_CARD)!!
        }

        sections.add(Section(getString(R.string.quick_jc_nav_item_shopfloor), R.drawable.ic_person_black_24dp))
        sections.add(Section(getString(R.string.quick_jc_nav_item_inventory), R.drawable.ic_list_black_24dp))
        sections.add(Section(getString(R.string.quick_jc_nav_item_damages), R.drawable.ic_photo_camera_black_24dp))
        sections.add(Section(getString(R.string.quick_jc_nav_item_inspection), R.drawable.ic_opacity_black_24dp))
        if (jobCard.type == JobCard.TYPE_ACCIDENTAL)
            sections.add(Section(getString(R.string.quick_jc_nav_item_insurance), R.drawable.ic_security_black_24dp))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutShortJcTopNavBinding.inflate(inflater, container, false)
        return binding.root
       /* return inflater.inflate(R.layout.layout_short_jc_top_nav, container, false)*/
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationInteraction) {
            interaction = context
        } else {
            IllegalStateException("implemented iterafction")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.navItemRecyclerView.adapter = Adapter()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("here i come")
    }

    companion object {

        private const val ARG_IS_VIEW_ONLY = "arg_is_view_only"
        private const val ARG_JOB_CARD_ID = "arg_job_card_id"
        private const val ARG_STATUS = "arg_status"
        private const val ARG_JOB_CARD = "arg_job_card"

        fun newInstance(isViewOnly: Boolean, jobCardId: String, jobCard: JobCard, status: String): QuickTopNavFragment {
            val fragment = QuickTopNavFragment()
            val args = Bundle()
            args.putString(ARG_STATUS, status)
            args.putBoolean(ARG_IS_VIEW_ONLY, isViewOnly)
            args.putString(ARG_JOB_CARD_ID, jobCardId)
            args.putParcelable(ARG_JOB_CARD, jobCard)
            fragment.arguments = args
            return fragment
        }
    }

    inner class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_short_top_nav_bar, parent, false))
        }

        override fun getItemCount(): Int = sections.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.sectionIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), sections[position].icon))
            holder.sectionTitleView.text = sections[position].sectionName
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var sectionTitleView: TextView = view.findViewById(R.id.navTitleView)
            var sectionIconView: ImageView = view.findViewById(R.id.navIconView)
            private var sectionParentView: View = view.findViewById(R.id.navItemParentView)

            init {
                sectionParentView.setOnClickListener {
                    if (adapterPosition == -1)
                        return@setOnClickListener
                    when (sections[adapterPosition].sectionName) {

                        getString(R.string.quick_jc_nav_item_shopfloor) -> {
                            interaction.onStoryClick(NavigationInteraction.SCREEN_VOICE)
                        }

                        getString(R.string.quick_jc_nav_item_inventory) -> {
                            interaction.onStoryClick(NavigationInteraction.SCREEN_INVENTORY)
                        }

                        getString(R.string.quick_jc_nav_item_inspection) -> {
                            interaction.onStoryClick(NavigationInteraction.SCREEN_INSPECTION)
                        }

                        getString(R.string.quick_jc_nav_item_insurance) -> {
                            interaction.onStoryClick(NavigationInteraction.SCREEN_INSURANCE)
                        }

                        getString(R.string.quick_jc_nav_item_damages) -> {
                            interaction.onStoryClick(NavigationInteraction.SCREEN_DAMAGES)
                        }
                    }
                }
            }
        }
    }

    data class Section(val sectionName: String, val icon: Int)
}
