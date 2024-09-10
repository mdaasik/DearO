package com.carworkz.dearo.addjobcard.createjobcard.damage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.addjobcard.createjobcard.ICreateJobCardInteraction
import com.carworkz.dearo.addjobcard.createjobcard.damage.capturedamage.ClickPictureActivity
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.BaseActivity
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.FragmentDamagePictureBinding
import com.carworkz.dearo.domain.entities.FileObject
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.extensions.AnkoInternals.addView
import com.carworkz.dearo.utils.PermissionUtil
import com.carworkz.dearo.utils.Utility
import com.google.android.material.floatingactionbutton.FloatingActionButton
/*import kotlinx.android.synthetic.main.activity_pdc.**/
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class DamageFragment : BaseFragment(), DamageContract.View, EventsManager.EventSubscriber, EditDamageCallback {
    private lateinit var binding: FragmentDamagePictureBinding
    private var isViewOnly: Boolean = false
    private lateinit var jobCardId: String
    private lateinit var interaction: ICreateJobCardInteraction
    private lateinit var listType: String

    @Inject
    lateinit var presenter: DamagePresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isViewOnly = it.getBoolean(ARG_IS_VIEW_ONLY)
            jobCardId = it.getString(ARG_JOB_CARD_ID)!!
            listType = it.getString(ARG_TYPE)!!
        }

        ((activity?.application) as DearOApplication)
                .repositoryComponent
                .COMPONENT(DamagePresenterModule(this))
                .inject(this)
        screenTracker.sendScreenEvent(activity, if (isViewOnly) ScreenTracker.SCREEN_VIEW_DAMAGE else ScreenTracker.SCREEN_DAMAGE, this.javaClass.name)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDamagePictureBinding.inflate(inflater, container, false)

      //  val view = inflater.inflate(R.layout.fragment_damage_picture, container, false)
       /* damageView = view.find(R.id.ll_damage_section_container)
        binding.llPdcSectionContainer. = view.find(R.id.ll_pdc_section_container)
        cameraFabView = view.find(R.id.camera_btn) as FloatingActionButton
        noDamagesView = view.find(R.id.tv_damage_empty) as TextView*/

        if ((listType == JobCard.STATUS_IN_PROGRESS || listType == JobCard.STATUS_INITIATED).not()) {
            Utility.setVisibility(false, binding.cameraBtn)
        } else {
            binding.cameraBtn.setOnClickListener {
                Log.d("checkkk","0000");
                if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)) {
                    if (PermissionUtil.checkPermission(
                            activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        startActivity(
                            ClickPictureActivity.getIntent(
                                requireContext(),
                                jobCardId,
                                isViewOnly,
                                ClickPictureActivity.ACTION_DAMAGE
                            )
                        )
                    } else {
                        PermissionUtil.requestPermissions(
                            activity as BaseActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            R.string.permission_save_file
                        ) { _, _, grantResults ->
                            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                startActivity(
                                    ClickPictureActivity.getIntent(
                                        requireContext(),
                                        jobCardId,
                                        isViewOnly,
                                        ClickPictureActivity.ACTION_DAMAGE
                                    )
                                )
                            }
                        }
                    }
                }
                else{
                    startActivity(
                        ClickPictureActivity.getIntent(
                            requireContext(),
                            jobCardId,
                            isViewOnly,
                            ClickPictureActivity.ACTION_DAMAGE
                        )
                    )

                }
            }
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ICreateJobCardInteraction) {
            interaction = context
        } else
            throw IllegalStateException("Activity must implement ICreateJobCardInteraction interface ")
    }

    override fun onResume() {
        super.onResume()
        EventsManager.register(this)
        binding.llDamageSectionContainer.removeAllViews()
        binding.llPdcSectionContainer.removeAllViews()
        getImages()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventsManager.unregister(this)
        presenter.detachView()
    }

    override fun displayDamages(imageList: List<FileObject>) {
        Timber.d("Display Images")
        if (imageList.isEmpty()) {
            binding.tvDamageEmpty.visibility = View.VISIBLE
            binding.llDamageSectionContainer.visibility = View.GONE
        } else {
            binding.llDamageSectionContainer.removeAllViews()
            binding.tvDamageEmpty.visibility = View.GONE
            binding.llDamageSectionContainer.visibility = View.VISIBLE
            if (isViewOnly) {
                inflateInProgressList(imageList.filter { it.meta == null || it.meta?.category == FileObject.WORK_IN_PROGESS })
            }
            inflateInspectionList(imageList.filter { it.meta?.category == FileObject.INSPECTION_AND_DAMAGES })
        }
    }

    override fun displayPdcImages(imageList: List<FileObject>) {
        if(imageList.isNotEmpty())
        {
            binding.tvDamageEmpty.visibility = View.GONE
            binding.llPdcSectionContainer.visibility = View.VISIBLE
            inflatePdcImageList(imageList)
        }
    }

    override fun showProgressIndicator() {
        (activity as ScreenContainerActivity).showProgressBar()
    }

    override fun dismissProgressIndicator() {
        (activity as ScreenContainerActivity).dismissProgressBar()
    }

    override fun moveToNextScreen() {
    }

    override fun updateAlert() {
        getImages()
    }

    override fun onEditCaption(imageId: String, caption: String) {
        if (checkIfNetworkAvailable()) {
            presenter.editCaption(imageId, caption, jobCardId)
        }
    }

    override fun onDeleteImage(fileObject: FileObject) {
        if (checkIfNetworkAvailable()) {
            presenter.deleteDamage(fileObject)
        }
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    @Subscribe
    fun onNextBtnEvent(actionButtonEvent: ActionButtonClickEvent) {
        Timber.v("ActionButton Click Event" + actionButtonEvent.hashCode())
        interaction.onJobSuccess()
    }

    private fun getImages() {
        if (checkIfNetworkAvailable()) {
            binding.llDamageSectionContainer.removeAllViews()
            binding.llPdcSectionContainer.removeAllViews()
            presenter.getGalleryData(jobCardId)
//            presenter.getPdcData(jobCardId)
        } else {
            toast("No Internet Connection")
        }
    }

    private fun inflateInProgressList(inProgressList: List<FileObject>) {
        Timber.d("inProgressList")
        val view = layoutInflater.inflate(R.layout.section_damage_layout, binding.llDamageSectionContainer, false)
        val recyclerView: RecyclerView = view.find(R.id.rv_damage_grid)
        val emptyView: TextView = view.find(R.id.tv_empty)
        val titleView: TextView = view.find(R.id.tv_title)
        val countView: TextView = view.find(R.id.tv_count)
        if (inProgressList.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        titleView.text = FileObject.WORK_IN_PROGESS
        countView.text = String.format(Locale.getDefault(), getString(R.string.damage_template_number_of_photos), inProgressList.size)
        recyclerView.adapter = DamageRecycleAdapter(inProgressList as ArrayList<FileObject>, this, listType == JobCard.STATUS_IN_PROGRESS, FileObject.WORK_IN_PROGESS)
        binding.llDamageSectionContainer.addView(view)
    }

    private fun inflateInspectionList(inspectionImageList: List<FileObject>) {
        Timber.d("inflateInspectionList")
        val view = layoutInflater.inflate(R.layout.section_damage_layout, binding.llDamageSectionContainer, false)
        val recyclerView: RecyclerView = view.find(R.id.rv_damage_grid)
        val titleView: TextView = view.find(R.id.tv_title)
        val countView: TextView = view.find(R.id.tv_count)
        val emptyView: TextView = view.find(R.id.tv_empty)
        if (inspectionImageList.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        titleView.text = FileObject.INSPECTION_AND_DAMAGES
        countView.text = String.format(Locale.getDefault(), getString(R.string.damage_template_number_of_photos), inspectionImageList.size)
        recyclerView.adapter = DamageRecycleAdapter(inspectionImageList as ArrayList<FileObject>, this, listType == JobCard.STATUS_INITIATED, FileObject.INSPECTION_AND_DAMAGES)
        binding.llDamageSectionContainer.addView(view)
    }

    private fun inflatePdcImageList(pdcImageList: List<FileObject>) {
        Timber.d("inflateInspectionList")
        val view = layoutInflater.inflate(R.layout.section_damage_layout, binding.llPdcSectionContainer, false)
        val recyclerView: RecyclerView = view.find(R.id.rv_damage_grid)
        val titleView: TextView = view.find(R.id.tv_title)
        val countView: TextView = view.find(R.id.tv_count)
        val emptyView: TextView = view.find(R.id.tv_empty)
        if (pdcImageList.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        titleView.text = FileObject.PDC_AND_DAMAGES
        countView.text = String.format(Locale.getDefault(), getString(R.string.damage_template_number_of_photos), pdcImageList.size)
        recyclerView.adapter = DamageRecycleAdapter(pdcImageList as java.util.ArrayList<FileObject>, this, true, FileObject.PDC_AND_DAMAGES)
        binding.llPdcSectionContainer.addView(view)
    }

    companion object {
        private const val ARG_IS_VIEW_ONLY = "is_view_only"
        private const val ARG_TYPE = "arg_type"
        private const val ARG_JOB_CARD_ID = "arg_job_card_id"

        @JvmStatic
        fun newInstance(status: String, isViewOnly: Boolean, jobCardId: String): DamageFragment {
            val fragment = DamageFragment()
            val args = Bundle()
            args.putBoolean(ARG_IS_VIEW_ONLY, isViewOnly)
            args.putString(ARG_JOB_CARD_ID, jobCardId)
            args.putString(ARG_TYPE, status)
            fragment.arguments = args
            return fragment
        }
    }
}
