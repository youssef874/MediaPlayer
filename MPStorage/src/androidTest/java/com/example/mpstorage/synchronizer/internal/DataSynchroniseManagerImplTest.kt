package com.example.mpstorage.synchronizer.internal

import androidx.test.platform.app.InstrumentationRegistry
import com.example.mpeventhandler.MPEventHandlerApi
import com.example.mpeventhandler.data.MPEvent
import com.example.mpeventhandler.internal.MPEventListener
import com.example.mpstorage.synchronizer.event.SynchronisationChanges
import com.example.mpstorage.synchronizer.event.SynchronisationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class DataSynchroniseManagerImplTest{

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private lateinit var dataSynchronizeManager: IDataSynchronizeManager

    private lateinit var successSataSynchronizer: IDataSynchronize
    private lateinit var failedDataSynchronizer: IDataSynchronize

    @Before
    fun setup(){
        dataSynchronizeManager  = DataSynchroniseManagerImpl
        successSataSynchronizer = FakeDataSynchronize(true)
        failedDataSynchronizer = FakeDataSynchronize(false)
    }

    @Test
    fun test_synchronize_with_one_success_dataSynchronizer()= runTest {
        val flow = MutableStateFlow<MPEvent?>(null)
        MPEventHandlerApi.subscribe(
            events = arrayOf(
                SynchronisationChanges(SynchronisationType.SYNCHRONISATION_COMPLETED),
                SynchronisationChanges(SynchronisationType.SYNCHRONIZATION_FAILED),
                SynchronisationChanges(SynchronisationType.SYNCHRONISATION_STARTED)
            ),
            object :MPEventListener{
                override fun onEvent(event: MPEvent) {
                    flow.value = event
                }
            }
        )
        dataSynchronizeManager.synchronize(successSataSynchronizer, context = context)
        backgroundScope.launch {
            with(flow){
                assert(firstOrNull { it?.type == SynchronisationType.SYNCHRONIZATION_FAILED } == null)
                assert(firstOrNull { it?.type == SynchronisationType.SYNCHRONISATION_STARTED } != null)
                assert(firstOrNull { it?.type == SynchronisationType.SYNCHRONISATION_COMPLETED } != null)
            }
        }
    }

    @Test
    fun test_synchronize_with_one_failed_dataSynchronizer() = runTest {
        val flow = MutableStateFlow<MPEvent?>(null)
        MPEventHandlerApi.subscribe(
            events = arrayOf(
                SynchronisationChanges(SynchronisationType.SYNCHRONISATION_COMPLETED),
                SynchronisationChanges(SynchronisationType.SYNCHRONIZATION_FAILED),
                SynchronisationChanges(SynchronisationType.SYNCHRONISATION_STARTED)
            ),
            object :MPEventListener{
                override fun onEvent(event: MPEvent) {
                    flow.value = event
                }
            }
        )
        dataSynchronizeManager.synchronize(failedDataSynchronizer, context = context)
        backgroundScope.launch {
            with(flow){
                assert(firstOrNull { it?.type == SynchronisationType.SYNCHRONIZATION_FAILED } != null)
                assert(firstOrNull { it?.type == SynchronisationType.SYNCHRONISATION_STARTED } != null)
                assert(firstOrNull { it?.type == SynchronisationType.SYNCHRONISATION_COMPLETED } == null)
            }
        }
    }

    @Test
    fun test_synchronize_with_tow_success_dataSynchronizer() = runTest {
        val flow = MutableStateFlow<MPEvent?>(null)
        MPEventHandlerApi.subscribe(
            events = arrayOf(
                SynchronisationChanges(SynchronisationType.SYNCHRONISATION_COMPLETED),
                SynchronisationChanges(SynchronisationType.SYNCHRONIZATION_FAILED),
                SynchronisationChanges(SynchronisationType.SYNCHRONISATION_STARTED)
            ),
            object :MPEventListener{
                override fun onEvent(event: MPEvent) {
                    flow.value = event
                }
            }
        )
        dataSynchronizeManager.synchronize(successSataSynchronizer,successSataSynchronizer, context = context)
        backgroundScope.launch {
            with(flow){
                assert(firstOrNull { it?.type == SynchronisationType.SYNCHRONIZATION_FAILED } == null)
                assert(firstOrNull { it?.type == SynchronisationType.SYNCHRONISATION_STARTED } != null)
                assert(firstOrNull { it?.type == SynchronisationType.SYNCHRONISATION_COMPLETED } != null)
            }
        }
    }

    @Test
    fun test_synchronize_with_one_success_and_one_failed_dataSynchronizer() = runTest {
        val flow = MutableStateFlow<MPEvent?>(null)
        MPEventHandlerApi.subscribe(
            events = arrayOf(
                SynchronisationChanges(SynchronisationType.SYNCHRONISATION_COMPLETED),
                SynchronisationChanges(SynchronisationType.SYNCHRONIZATION_FAILED),
                SynchronisationChanges(SynchronisationType.SYNCHRONISATION_STARTED)
            ),
            object :MPEventListener{
                override fun onEvent(event: MPEvent) {
                    flow.value = event
                }
            }
        )
        dataSynchronizeManager.synchronize(successSataSynchronizer,failedDataSynchronizer, context = context)
        backgroundScope.launch {
            with(flow){
                assert(firstOrNull { it?.type == SynchronisationType.SYNCHRONIZATION_FAILED } != null)
                assert(firstOrNull { it?.type == SynchronisationType.SYNCHRONISATION_STARTED } != null)
                assert(firstOrNull { it?.type == SynchronisationType.SYNCHRONISATION_COMPLETED } == null)
            }
        }
    }

    @Test
    fun test_synchronize_with_tow_failed_dataSynchronizer() = runTest {
        val flow = MutableStateFlow<MPEvent?>(null)
        MPEventHandlerApi.subscribe(
            events = arrayOf(
                SynchronisationChanges(SynchronisationType.SYNCHRONISATION_COMPLETED),
                SynchronisationChanges(SynchronisationType.SYNCHRONIZATION_FAILED),
                SynchronisationChanges(SynchronisationType.SYNCHRONISATION_STARTED)
            ),
            object :MPEventListener{
                override fun onEvent(event: MPEvent) {
                    flow.value = event
                }
            }
        )
        dataSynchronizeManager.synchronize(failedDataSynchronizer,failedDataSynchronizer, context = context)
        backgroundScope.launch {
            with(flow){
                assert(firstOrNull { it?.type == SynchronisationType.SYNCHRONIZATION_FAILED } != null)
                assert(firstOrNull { it?.type == SynchronisationType.SYNCHRONISATION_STARTED } != null)
                assert(firstOrNull { it?.type == SynchronisationType.SYNCHRONISATION_COMPLETED } == null)
            }
        }
    }
}