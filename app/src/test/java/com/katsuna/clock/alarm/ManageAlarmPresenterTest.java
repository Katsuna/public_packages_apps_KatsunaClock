package com.katsuna.clock.alarm;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.validators.AlarmValidator;
import com.katsuna.clock.validators.IAlarmValidator;
import com.katsuna.clock.validators.ValidationResult;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
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

    @Mock
    private IAlarmValidator mAlarmValidator;

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
        mManageAlarmPresenter = new ManageAlarmPresenter(null, mAlarmsDataSource, mManageAlarmView,
                mAlarmValidator);

        // Then the presenter is set to the view
        verify(mManageAlarmView).setPresenter(mManageAlarmPresenter);
    }

    @Test
    public void startPresenterForExistingAlarm_LoadsAlarm() {
        // Get a reference to the class under test
        String id = "123";
        mManageAlarmPresenter = new ManageAlarmPresenter(id, mAlarmsDataSource, mManageAlarmView,
                mAlarmValidator);

        mManageAlarmPresenter.start();

        verify(mAlarmsDataSource).getAlarm(eq(id), mGetAlarmCallbackCaptor.capture());
    }

    @Test(expected = RuntimeException.class)
    public void populateAlarmForNonExistingAlarm_ThrowsException() {
        mManageAlarmPresenter = new ManageAlarmPresenter(null, mAlarmsDataSource, mManageAlarmView,
                mAlarmValidator);
        mManageAlarmPresenter.populateAlarm();
    }

    @Test
    public void saveNewAlarmToRepository_showsSuccessMessageUi() {
        // Get a reference to the class under test
        mManageAlarmPresenter = new ManageAlarmPresenter(null, mAlarmsDataSource, mManageAlarmView,
                mAlarmValidator);

        mManageAlarmPresenter.start();
        mManageAlarmPresenter.setAlarmTypeInfo(AlarmType.ALARM, "desc");
        mManageAlarmPresenter.setAlarmTime("12", "30");
        mManageAlarmPresenter.saveAlarm(false, false, false, false, false, false, false,
                AlarmStatus.ACTIVE);

        // Then an alarm is saved in the repository and the view updated
        verify(mAlarmsDataSource).saveAlarm(any(Alarm.class)); // saved to the model
        verify(mManageAlarmView).showAlarmsList(); // shown in the UI
    }

    @Test
    public void saveNewAlarmWithValidationErrors_showsTheErrors() {
        // Get a reference to the class under test
        mManageAlarmPresenter = new ManageAlarmPresenter(null, mAlarmsDataSource, mManageAlarmView,
                new AlarmValidator());

        mManageAlarmPresenter.start();
        mManageAlarmPresenter.setAlarmTypeInfo(AlarmType.ALARM, "desc");
        mManageAlarmPresenter.setAlarmTime("24", "61");
        mManageAlarmPresenter.saveAlarm(false, false, false, false, false, false, false,
                AlarmStatus.ACTIVE);

        verify(mManageAlarmView).showValidationResults(anyListOf(ValidationResult.class));
    }

    @Test
    public void saveExistingTaskToRepository_showsSuccessMessageUi() {
        Alarm testAlarm = new Alarm();
        testAlarm.setId("1");

        // Get a reference to the class under test
        mManageAlarmPresenter = new ManageAlarmPresenter(testAlarm.getId(), mAlarmsDataSource,
                mManageAlarmView, mAlarmValidator);

        // When the presenter is asked to save an existing task
        mManageAlarmPresenter.start();

        verify(mAlarmsDataSource).getAlarm(eq(testAlarm.getId()),
                mGetAlarmCallbackCaptor.capture());
        mGetAlarmCallbackCaptor.getValue().onAlarmLoaded(testAlarm);

        mManageAlarmPresenter.setAlarmTypeInfo(AlarmType.ALARM, "desc");
        mManageAlarmPresenter.setAlarmTime("12", "30");
        mManageAlarmPresenter.saveAlarm(false, false, false, false, false, false, false,
                AlarmStatus.ACTIVE);

        // Then a task is saved in the repository and the view updated
        verify(mAlarmsDataSource).saveAlarm(any(Alarm.class)); // saved to the model
        verify(mManageAlarmView).showAlarmsList(); // shown in the UI
    }

    @Test
    public void populateAlarm_callsDataSourceAndUpdatesView() {
        Alarm testAlarm = new Alarm(AlarmType.ALARM, "DESCRIPTION");
        // Get a reference to the class under test
        mManageAlarmPresenter = new ManageAlarmPresenter(testAlarm.getId(), mAlarmsDataSource,
                mManageAlarmView, mAlarmValidator);

        // When the presenter is asked to populate an existing task
        mManageAlarmPresenter.populateAlarm();

        // Then the task repository is queried and the view updated
        verify(mAlarmsDataSource).getAlarm(eq(testAlarm.getId()), mGetAlarmCallbackCaptor.capture());
        assertThat(mManageAlarmPresenter.isDataMissing(), is(true));

        // Simulate callback
        mGetAlarmCallbackCaptor.getValue().onAlarmLoaded(testAlarm);

        verify(mManageAlarmView).loadAlarm(testAlarm);
        assertThat(mManageAlarmPresenter.isDataMissing(), is(false));
    }

    @Test
    public void populateNonExistingAlarm_callsOnDataNotAvailable() {
        Alarm testAlarm = new Alarm(AlarmType.ALARM, "DESCRIPTION");
        // Get a reference to the class under test
        mManageAlarmPresenter = new ManageAlarmPresenter(testAlarm.getId(), mAlarmsDataSource,
                mManageAlarmView, mAlarmValidator);

        // When the presenter is asked to populate an existing task
        mManageAlarmPresenter.populateAlarm();

        // Then the task repository is queried and the view updated
        verify(mAlarmsDataSource).getAlarm(eq(testAlarm.getId()),
                mGetAlarmCallbackCaptor.capture());

        // Simulate callback
        mGetAlarmCallbackCaptor.getValue().onDataNotAvailable();
        verify(mManageAlarmView).showEmptyAlarmError();
    }

    @Test
    public void alarmTypeSelection_showsNextStepFab() {
        mManageAlarmPresenter = new ManageAlarmPresenter(null, mAlarmsDataSource, mManageAlarmView,
                new AlarmValidator());

        // When an alarm type is selected
        mManageAlarmPresenter.alarmTypeSelected(AlarmType.ALARM);

        // Then next step fab should be shown
        verify(mManageAlarmView).showNextStepFab(true);

        // And description control should remain invisible for this type of alarm.
        verify(mManageAlarmView).showDescriptionControl(false);
    }

    @Test
    public void reminderTypeSelection_showsDescriptionControl() {
        mManageAlarmPresenter = new ManageAlarmPresenter(null, mAlarmsDataSource, mManageAlarmView,
                new AlarmValidator());

        // When an alarm type is selected
        mManageAlarmPresenter.alarmTypeSelected(AlarmType.REMINDER);

        // Then description control should be visible for this type of alarm.
        verify(mManageAlarmView).showDescriptionControl(true);
    }


}

