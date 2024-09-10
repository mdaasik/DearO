package com.carworkz.dearo.pdf.managers

import android.os.Parcelable
import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.domain.entities.PDF
import com.carworkz.dearo.pdf.DaggerStateManagerComponent
import com.carworkz.dearo.pdf.StateManagerActionContract
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/**
 * [StateManager] is an adaptation of State design pattern. Primary purpose is to abstract business logic of different pdfs away from Android Components(activities,fragments).
 *
 *[StateManager] also manages fetching data from [DearODataRepository] instead of presenter like in other cases and communicates through [StateManagerActionContract] to the activity .
 *
 * @see <a href="https://www.journaldev.com/1751/state-design-pattern-java">State design pattern</a>
 * @author Farhan Patel
 *
 * @property pdfs represent all the pdf files for the current state.
 * @property stateManagerActionInteractionProvider provides callback mechanism to communicate with activity.
 *
 *
 *
 * */
abstract class StateManager : Parcelable, CoroutineScope {

    private val parentJob = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parentJob

    protected var stateManagerActionInteractionProvider: StateManagerActionContract? = null

    @IgnoredOnParcel
    protected val repository: DearODataRepository = DaggerStateManagerComponent.builder().build().repository

    val pdfs = mutableListOf<PDF>()

    /**
     * Each child of this class gets to decide what action can be performed while on that state. Carrying activity has no knowledge about possible actions.</br>
     * Called by the activity when user wants(clicks) to perform action E.g. Raise Invoice,Update payment,starting activity etc.
     * */
    abstract fun executeAction()

    /**
     * @return text to be displayed on action button.
    * */
    abstract fun getActionText(): String?

    /**
     * called when action is completed
     * @param reactionValue resulted value from the performed action.
    * */
    abstract fun <T> react(reactionValue: ReactionValue<T>?)

    /**
     * called to move from current state to the next state. E.g Proforma -> Invoice
    * */
    abstract fun goNext()

    abstract fun start()

    abstract fun getMainTitle(selectedPdfPos: Int): String

    fun setInteractionProvider(provider: StateManagerActionContract) {
        stateManagerActionInteractionProvider = provider
        Timber.d("is repo null $repository")
    }

    /**
     * releases all references to the view and cancel any running job.
     *
     *
    * */
    fun detach() {
        stateManagerActionInteractionProvider = null
        parentJob.cancel()
    }

    class ReactionValue<T>(val obj: T)
}