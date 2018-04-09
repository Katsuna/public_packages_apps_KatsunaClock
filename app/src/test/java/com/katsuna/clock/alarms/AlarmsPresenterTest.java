package com.katsuna.clock.alarms;

import com.google.common.collect.Lists;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.data.source.AlarmsDataSource.LoadAlarmsCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link AlarmsPresenter}
 */
public class AlarmsPresenterTest {

    private static List<Alarm> ALARMS;

    @Mock
    private AlarmsDataSource mAlarmsDataSource;

    @Mock
    private AlarmsContract.View mAlarmsView;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<LoadAlarmsCallback> mLoadAlarmsCallbackCaptor;

    @Captor
    private ArgumentCaptor<List<Alarm>> mAlarmsCaptor;

    private AlarmsPresenter mAlarmsPresenter;

    @Before
    public void setupAlarmsPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mAlarmsPresenter = new AlarmsPresenter(mAlarmsDataSource, mAlarmsView);

        // We start the alarms to 3
        ALARMS = Lists.newArrayList(new Alarm(AlarmType.ALARM, "Description1"),
                new Alarm(AlarmType.ALARM, "Description2"),
                new Alarm(AlarmType.ALARM, "Description3"));
    }

    @Test
    public void createPresenter_setsThePresenterToView() {
        // Then the presenter is set to the view
        verify(mAlarmsView).setPresenter(mAlarmsPresenter);
    }

    @Test
    public void startPresenterLoadsAllAlarms() {
        mAlarmsPresenter.start();

        verify(mAlarmsDataSource).getAlarms(mLoadAlarmsCallbackCaptor.capture());
        mLoadAlarmsCallbackCaptor.getValue().onAlarmsLoaded(ALARMS);
    }

    @Test
    public void loadAllAlarmsFromDataSourceAndLoadIntoView() {
        // Given an initialized AlarmsPresenter with initialized alarms
        // When loading of Alarms is requested
        mAlarmsPresenter.loadAlarms();

        // Callback is captured and invoked with stubbed alarms
        verify(mAlarmsDataSource).getAlarms(mLoadAlarmsCallbackCaptor.capture());
        mLoadAlarmsCallbackCaptor.getValue().onAlarmsLoaded(ALARMS);

        // Then all alarms are shown in UI
        verify(mAlarmsView).showAlarms(mAlarmsCaptor.capture());
        assertTrue(mAlarmsCaptor.getValue().size() == 3);
    }

    @Test
    public void clickOnFab_ShowsAddTaskUi() {
        // When adding a new task
        mAlarmsPresenter.addNewAlarm();

        // Then add task UI is shown
        verify(mAlarmsView).showAddAlarm();
    }

    @Test
    public void clickOnTask_ShowsDetailUi() {
        // Given a stubbed active alarm
        Alarm requestedAlarm = new Alarm(AlarmType.ALARM, "Details Requested");

        // When open alarm details is requested
        mAlarmsPresenter.openAlarmDetails(requestedAlarm);

        // Then task detail UI is shown
        verify(mAlarmsView).showAlarmDetailsUi(any(String.class));
    }

    @Test
    public void unavailableAlarms_ShowsError() {
        // When alarms are loaded
        mAlarmsPresenter.loadAlarms();

        // And the alarms aren't available in the datasource
        verify(mAlarmsDataSource).getAlarms(mLoadAlarmsCallbackCaptor.capture());
        mLoadAlarmsCallbackCaptor.getValue().onDataNotAvailable();

        // Then an error message is shown
        verify(mAlarmsView).showNoAlarms();
    }

    @Test
    public void alarmDeletion_removesDeletedAlarm() {
        // Given an initialized AlarmsPresenter with initialized alarms
        // When loading of Alarms is requested
        mAlarmsPresenter.loadAlarms();

        // Callback is captured and invoked with stubbed alarms
        verify(mAlarmsDataSource).getAlarms(mLoadAlarmsCallbackCaptor.capture());
        mLoadAlarmsCallbackCaptor.getValue().onAlarmsLoaded(ALARMS);

        mAlarmsPresenter.deleteAlarm(ALARMS.get(0));
        verify(mAlarmsView).removeAlarm(ALARMS.get(0));
    }
}