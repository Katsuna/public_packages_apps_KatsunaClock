package com.katsuna.clock.alarm;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.data.source.AlarmsDataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link ManageAlarmPresenter}
 */
public class ManageAlarmPresenterTest {

    @Mock
    private AlarmsDataSource mAlarmsDataSource;

    @Mock
    private ManageAlarmContract.View mManageAlarmView;

    @Captor
    private ArgumentCaptor<AlarmsDataSource.GetAlarmCallback> mGetAlarmCallbackCaptor;

    private ManageAlarmPresenter mManageAlarmPresenter;

    @Before
    public void setupMocksAndView() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createPresenter_setsThePresenterToView(){
        // Get a reference to the class under test
        mManageAlarmPresenter = new ManageAlarmPresenter(null, mAlarmsDataSource, mManageAlarmView);

        // Then the presenter is set to the view
        verify(mManageAlarmView).setPresenter(mManageAlarmPresenter);
    }

    @Test
    public void saveNewAlarmToRepository_showsSuccessMessageUi() {
        // Get a reference to the class under test
        mManageAlarmPresenter = new ManageAlarmPresenter(null, mAlarmsDataSource, mManageAlarmView);

        // When the presenter is asked to save an alarm
        Alarm alarm = new Alarm(AlarmType.ALARM, "desc");
        mManageAlarmPresenter.saveAlarm(alarm);

        // Then a task is saved in the repository and the view updated
        verify(mAlarmsDataSource).saveAlarm(any(Alarm.class)); // saved to the model
        verify(mManageAlarmView).showAlarmsList(); // shown in the UI
    }

    @Test
    public void saveExistingTaskToRepository_showsSuccessMessageUi() {
        // Get a reference to the class under test
        mManageAlarmPresenter = new ManageAlarmPresenter("1", mAlarmsDataSource, mManageAlarmView);

        // When the presenter is asked to save an existing task
        mManageAlarmPresenter.saveAlarm(new Alarm(AlarmType.ALARM, "desc"));

        // Then a task is saved in the repository and the view updated
        verify(mAlarmsDataSource).saveAlarm(any(Alarm.class)); // saved to the model
        verify(mManageAlarmView).showAlarmsList(); // shown in the UI
    }

    @Test
    public void populateAlarm_callsDataSourceAndUpdatesView() {
        Alarm testAlarm = new Alarm(AlarmType.ALARM, "DESCRIPTION");
        // Get a reference to the class under test
        mManageAlarmPresenter = new ManageAlarmPresenter(testAlarm.getId(), mAlarmsDataSource,
                mManageAlarmView);

        // When the presenter is asked to populate an existing task
        mManageAlarmPresenter.populateAlarm();

        // Then the task repository is queried and the view updated
        verify(mAlarmsDataSource).getAlarm(eq(testAlarm.getId()), mGetAlarmCallbackCaptor.capture());
        assertThat(mManageAlarmPresenter.isDataMissing(), is(true));

        // Simulate callback
        mGetAlarmCallbackCaptor.getValue().onAlarmLoaded(testAlarm);

        //verify(mManageAlarmView).setTitle(testAlarm.getTitle());
        verify(mManageAlarmView).setDescription(testAlarm.getDescription());
        assertThat(mManageAlarmPresenter.isDataMissing(), is(false));
    }

}

