package com.carworkz.dearo.addjobcard.createjobcard.damage.damagegallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.ViewPager
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityDamageGalleryBinding
import com.carworkz.dearo.domain.entities.FileObject
import com.carworkz.dearo.domain.entities.FileObject.CREATOR.PDC_AND_DAMAGES
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.screencontainer.FullScreenContainer
import javax.inject.Inject

class DamageGalleryActivity : ScreenContainerActivity(), ToolBarInteractionProvider, DamageGalleryContract.View {

    private lateinit var binding: ActivityDamageGalleryBinding
    @Inject
    lateinit var presenter: DamageGalleryPresenter

    lateinit var toolbar: Toolbar
    private lateinit var viewPager: ViewPager
    private lateinit var screenContainer: FullScreenContainer
    private var imageList = ArrayList<FileObject>()
    lateinit var type: String
    lateinit var jobCardId: String
    private var selection = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        type = intent.extras?.getString(ARG_TYPE).toString()
        jobCardId = intent.extras?.getString(ARG_JOBCARD_ID).toString()
        selection = intent.extras?.getInt(ARG_SELECTION, 0)!!
        super.onCreate(savedInstanceState)
        (application as DearOApplication).repositoryComponent
                .COMPONENT(DamageGalleryPresenterModule(this))
                .inject(this)
        viewPager = find(R.id.galleryPager)
        viewPager.onPageChangeListener {
            this.onPageScrolled { position, _, _ ->
                screenContainer.toolbar!!.title = imageList[position].title
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onStart() {
        super.onStart()
        if (type==FileObject.PDC_AND_DAMAGES)
        {
            presenter.getPdcData(jobCardId)
        }
        else
        {
            presenter.getGalleryImages(jobCardId, type)
        }
    }

    override fun getProgressView(): View {
        return ProgressBar(this)
    }

    override fun getToolBarTitle(): String {
        return "Gallery"
    }

    override fun getActionBtnTitle(): String = ""

    override fun onActionBtnClick() {}

    override fun createScreenContainer(): ScreenContainer {
        screenContainer = FullScreenContainer(this)
        return screenContainer
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityDamageGalleryBinding.inflate(layoutInflater)
       return binding
    }

    override fun displayGalleryImages(list: ArrayList<FileObject>) {

        imageList.clear()
        imageList.addAll(list)
        viewPager.adapter = DamageGalleryAdapter(imageList)
        viewPager.setCurrentItem(selection, false)
        screenContainer.toolbar!!.title = imageList[selection].title
    }

    override fun showProgressIndicator() {
        super.showProgressBar()
    }

    override fun dismissProgressIndicator() {
        super.dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }


    companion object {
        const val ARG_JOBCARD_ID = "jobcard_id"
        const val ARG_TYPE = "ARG_TYPE"
        const val ARG_SELECTION = "selection"
    }
}
