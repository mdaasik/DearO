package com.carworkz.dearo.thirdpartydetails

import com.carworkz.dearo.data.DearODataRepository
import com.carworkz.dearo.data.Result
import com.carworkz.dearo.domain.entities.Address
import com.carworkz.dearo.domain.entities.ThirdParty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class ThirdPartyPresenterTest {

    @Mock
    internal lateinit var repository: DearODataRepository

    @Mock
    internal var view: ThirdPartyDetailsContract.View? = null

    private lateinit var presenter: ThirdPartyDetailsPresenter

    val testCoroutineDispatcher = TestCoroutineDispatcher()
    val testScope = TestCoroutineScope(testCoroutineDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testCoroutineDispatcher)
        MockitoAnnotations.initMocks(this)
        presenter = ThirdPartyDetailsPresenter(view, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
    }

    @Test
    fun whenValidationFailed() {
        val thirdParty = ThirdParty(true, "", "882830632", "farhan@.com", "FFFF", Address())
        val result = presenter.validate(thirdParty)
        verify(view)?.invalidEmail()
        verify(view)?.invalidMobileNumber()
        verify(view)?.invalidGstNumber()
        verify(view)?.invalidAddress()
        verify(view)?.invalidPincode()
        verify(view)?.invalidName()
        verifyNoMoreInteractions(view)
        Assert.assertEquals(result, false)
    }

    @Test
    fun whenSaveDetailsAndSuccess() {
        val thirdParty = ThirdParty(true, "mehul", "7276144790", "mehul@fmail.com", "20AGQPB0013L2ZZ", Address().apply {
            pincode = 400008
            street = "test street"
        })
        val invoiceId = "1236213821312"

        testScope.runBlockingTest {
            `when`(repository.saveThirdPartyDetails(invoiceId, thirdParty)).thenReturn(Result.Success(thirdParty))
            presenter.save(invoiceId, thirdParty)
            verify(view)?.showProgressIndicator()
            verify(repository).saveThirdPartyDetails(invoiceId, thirdParty)
            verify(view)?.dismissProgressIndicator()
            verify(view)?.onSaveSuccess(thirdParty)
            verifyNoMoreInteractions(view)
        }
    }
}
