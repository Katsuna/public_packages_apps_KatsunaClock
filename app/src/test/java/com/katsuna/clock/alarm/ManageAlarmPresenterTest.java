package com.katsuna.clock.alarm;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.services.utils.IAlarmsScheduler;
import com.katsuna.clock.validators.AlarmValidator;
import com.katsuna.clock.validators.IAlarmValidator;
import com.katsuna.clock.validators.ValidationResult;

import junit.framework.Assert;

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
import static org.mockito.Mockito.times;
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

    @Mock
    private IAlarmsScheduler mAlarmsScheduler;

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
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.ALARM, mAlarmsDataSource,
                mManageAlarmView, mAlarmValidator, mAlarmsScheduler);

        // Then the presenter is set to the view
        verify(mManageAlarmView).setPresenter(mManageAlarmPresenter);
    }

    @Test
    public void startPresenterForNewAlarm_InitializeAlarmType() {
        // Get a reference to the class under test
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.ALARM, mAlarmsDataSource,
                mManageAlarmView, mAlarmValidator, mAlarmsScheduler);

        // Then the presenter is set to the view
        Assert.assertTrue(mManageAlarmPresenter.getAlarmType() == AlarmType.ALARM);
    }

    @Test
    public void startPresenterForExistingAlarm_LoadsAlarm() {
        // Get a reference to the class under test
        long id = 123;
        mManageAlarmPresenter = new ManageAlarmPresenter(id, AlarmType.ALARM, mAlarmsDataSource,
                mManageAlarmView, mAlarmValidator, mAlarmsScheduler);

        mManageAlarmPresenter.start();

        verify(mAlarmsDataSource).getAlarm(eq(id), mGetAlarmCallbackCaptor.capture());
    }

    @Test(expected = RuntimeException.class)
    public void populateAlarmForNonExistingAlarm_ThrowsException() {
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.ALARM, mAlarmsDataSource,
                mManageAlarmView, mAlarmValidator, mAlarmsScheduler);
        mManageAlarmPresenter.populateAlarm();
    }

    @Test
    public void saveNewAlarmToRepository_showsSuccessMessageUi() {
        // Get a reference to the class under test
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.ALARM, mAlarmsDataSource,
                mManageAlarmView, mAlarmValidator, mAlarmsScheduler);

        mManageAlarmPresenter.start();
        AlarmType alarmType = AlarmType.ALARM;
        String hour = "12";
        String minute = "30";
        mManageAlarmPresenter.validateAlarmTypeInfo(alarmType, null);
        mManageAlarmPresenter.validateAlarmTime(hour, minute);
        mManageAlarmPresenter.saveAlarm(null, hour, minute, false, false, false,
                false, false, false, false, "ringtone", true);

        // Then an alarm is saved in the repository and the view updated
        verify(mAlarmsDataSource).saveAlarm(any(Alarm.class)); // saved to the model
        verify(mAlarmsScheduler).reschedule(any(Alarm.class));
        verify(mManageAlarmView).showAlarmsList(any(Alarm.class)); // shown in the UI
    }

    @Test
    public void saveNewAlarmWithValidationErrors_showsTheErrors() {
        // Get a reference to the class under test
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.ALARM, mAlarmsDataSource,
                mManageAlarmView, new AlarmValidator(), mAlarmsScheduler);

        mManageAlarmPresenter.start();
        String hour = "24";
        String minute = "61";
        mManageAlarmPresenter.validateAlarmTypeInfo(null, null);
        mManageAlarmPresenter.validateAlarmTime(hour, minute);
        //noinspection ConstantConditions
        mManageAlarmPresenter.saveAlarm(null, hour, minute, false, false, false,
                false, false, false, false, "ringtone", true);

        // we expect 3 invocation of showValidationResults
        verify(mManageAlarmView, times(3)).showValidationResults(anyListOf(ValidationResult.class));
    }

    @Test
    public void saveExistingTaskToRepository_showsSuccessMessageUi() {
        Alarm testAlarm = new Alarm(AlarmType.ALARM, null);

        testAlarm.setAlarmId(1);

        // Get a reference to the class under test
        mManageAlarmPresenter = new ManageAlarmPresenter(testAlarm.getAlarmId(), AlarmType.ALARM,
                mAlarmsDataSource, mManageAlarmView, mAlarmValidator, mAlarmsScheduler);

        // When the presenter is asked to save an existing task
        mManageAlarmPresenter.start();

        verify(mAlarmsDataSource).getAlarm(eq(testAlarm.getAlarmId()),
                mGetAlarmCallbackCaptor.capture());
        mGetAlarmCallbackCaptor.getValue().onAlarmLoaded(testAlarm);

        AlarmType alarmType = AlarmType.ALARM;
        String description = "desc";
        String hour = "12";
        String minute = "30";
        mManageAlarmPresenter.validateAlarmTypeInfo(alarmType, description);
        mManageAlarmPresenter.validateAlarmTime(hour, minute);
        mManageAlarmPresenter.saveAlarm(description, hour, minute, false, false, false,
                false, false, false, false, "ringtone", false);

        // Then a task is saved in the repository and the view updated
        verify(mAlarmsDataSource).saveAlarm(any(Alarm.class)); // saved to the model
        verify(mAlarmsScheduler).reschedule(any(Alarm.class));
        verify(mManageAlarmView).showAlarmsList(any(Alarm.class)); // shown in the UI
    }

    @Test
    public void populateAlarm_callsDataSourceAndUpdatesView() {
        Alarm testAlarm = new Alarm(AlarmType.ALARM, "DESCRIPTION");
        testAlarm.setAlarmId(1);
        // Get a reference to the class under test
        mManageAlarmPresenter = new ManageAlarmPresenter(testAlarm.getAlarmId(), AlarmType.ALARM,
                mAlarmsDataSource, mManageAlarmView, mAlarmValidator, mAlarmsScheduler);

        // When the presenter is asked to populate an existing task
        mManageAlarmPresenter.populateAlarm();

        // Then the task repository is queried and the view updated
        verify(mAlarmsDataSource).getAlarm(eq(testAlarm.getAlarmId()), mGetAlarmCallbackCaptor.capture());
        assertThat(mManageAlarmPresenter.isDataMissing(), is(true));

        // Simulate callback
        mGetAlarmCallbackCaptor.getValue().onAlarmLoaded(testAlarm);

        verify(mManageAlarmView).loadAlarm(testAlarm);
        assertThat(mManageAlarmPresenter.isDataMissing(), is(false));
        verify(mManageAlarmView).showNextStepFab(true);
    }

    @Test
    public void populateNonExistingAlarm_callsOnDataNotAvailable() {
        Alarm testAlarm = new Alarm(AlarmType.ALARM, "DESCRIPTION");
        testAlarm.setAlarmId(1);
        // Get a reference to the class under test
        mManageAlarmPresenter = new ManageAlarmPresenter(testAlarm.getAlarmId(), AlarmType.ALARM,
                mAlarmsDataSource, mManageAlarmView, mAlarmValidator, mAlarmsScheduler);

        // When the presenter is asked to populate an existing task
        mManageAlarmPresenter.populateAlarm();

        // Then the task repository is queried and the view updated
        verify(mAlarmsDataSource).getAlarm(eq(testAlarm.getAlarmId()),
                mGetAlarmCallbackCaptor.capture());

        // Simulate callback
        mGetAlarmCallbackCaptor.getValue().onDataNotAvailable();
        verify(mManageAlarmView).showEmptyAlarmError();
    }

    @Test
    public void reminderTypeSelection_showsDescriptionControl() {
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.REMINDER, mAlarmsDataSource,
                mManageAlarmView, new AlarmValidator(), mAlarmsScheduler);

        Assert.assertTrue(mManageAlarmPresenter.getCurrentStep() == ManageAlarmStep.DESCRIPTION);
    }

    @Test
    public void alarmTypeSelection_showsTimeSelectionStep() {
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.ALARM, mAlarmsDataSource,
                mManageAlarmView, new AlarmValidator(), mAlarmsScheduler);

        mManageAlarmPresenter.start();

        // Then presenter step should change properly
        Assert.assertTrue(mManageAlarmPresenter.getCurrentStep() == ManageAlarmStep.TIME);

        // And view calls should be made
        verify(mManageAlarmView).showDescriptionControl(false);
        verify(mManageAlarmView).showAlarmTimeControl(true);
        verify(mManageAlarmView).showAlarmDaysControl(false);
        verify(mManageAlarmView).showAlarmOptionsControl(false);
        verify(mManageAlarmView).showPreviousStepFab(false);

    }

    @Test
    public void returnFromAlarmTimeStep_showsAlarmTypeStep() {
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.REMINDER, mAlarmsDataSource,
                mManageAlarmView, new AlarmValidator(), mAlarmsScheduler);

        mManageAlarmPresenter.start();

        // When an alarm type is selected and set
        mManageAlarmPresenter.validateAlarmTypeInfo(AlarmType.REMINDER, "description");

        // And previous button is pressed
        mManageAlarmPresenter.previousStep();

        // Then presenter step should change properly
        Assert.assertTrue(mManageAlarmPresenter.getCurrentStep() == ManageAlarmStep.DESCRIPTION);

        // And view calls must be made
        verify(mManageAlarmView).showDescriptionControl(true);
        verify(mManageAlarmView).showAlarmTimeControl(false);
        verify(mManageAlarmView).showPreviousStepFab(false);
    }

    @Test
    public void alarmDaysStepCompleted_showsAlarmOptionsStep() {
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.ALARM, mAlarmsDataSource,
                mManageAlarmView, new AlarmValidator(), mAlarmsScheduler);

        mManageAlarmPresenter.start();

        // when next button is pressed
        mManageAlarmPresenter.validateAlarmTime("21", "34");

        // and show options requested
        mManageAlarmPresenter.showStep(ManageAlarmStep.OPTIONS);

        // Then presenter step should change properly
        Assert.assertTrue(mManageAlarmPresenter.getCurrentStep() == ManageAlarmStep.OPTIONS);

        // And view calls must be made
        verify(mManageAlarmView).showAlarmOptionsControl(true);
    }

    @Test
    public void returnFromAlarmOptionsStep_showsAlarmDaysStep() {
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.ALARM, mAlarmsDataSource,
                mManageAlarmView, new AlarmValidator(), mAlarmsScheduler);

        mManageAlarmPresenter.start();

        // when next button is pressed
        mManageAlarmPresenter.validateAlarmTime("21", "34");

        // and show options requested
        mManageAlarmPresenter.showStep(ManageAlarmStep.OPTIONS);

        // And previous button is pressed
        mManageAlarmPresenter.previousStep();

        // Then presenter step should change properly
        Assert.assertTrue(mManageAlarmPresenter.getCurrentStep() == ManageAlarmStep.DAYS);
    }

    @Test
    public void alarmTimeStepCompleted_showsAlarmDaysStep() {
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.ALARM, mAlarmsDataSource,
                mManageAlarmView, new AlarmValidator(), mAlarmsScheduler);

        mManageAlarmPresenter.start();

        // when next button is pressed
        mManageAlarmPresenter.validateAlarmTime("21", "34");

        // Then presenter step should change properly
        Assert.assertTrue(mManageAlarmPresenter.getCurrentStep() == ManageAlarmStep.DAYS);

        // And view calls must be made
        verify(mManageAlarmView).showAlarmTimeControl(false);
        verify(mManageAlarmView).showAlarmDaysControl(true);
    }

    @Test
    public void returnFromAlarmDaysTimeStep_showsAlarmTimeStep() {
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.ALARM, mAlarmsDataSource,
                mManageAlarmView, new AlarmValidator(), mAlarmsScheduler);

        mManageAlarmPresenter.start();

        // when next button is pressed
        mManageAlarmPresenter.validateAlarmTime("21", "34");

        // And previous button is pressed
        mManageAlarmPresenter.previousStep();

        // Then presenter step should change properly
        Assert.assertTrue(mManageAlarmPresenter.getCurrentStep() == ManageAlarmStep.TIME);

        // And view calls must be made
        verify(mManageAlarmView, times(3)).showDescriptionControl(false);
        verify(mManageAlarmView, times(2)).showAlarmTimeControl(true);
        verify(mManageAlarmView, times(2)).showAlarmDaysControl(false);
        verify(mManageAlarmView, times(1)).showPreviousStepFab(true);
    }

    @Test
    public void addHourAction_increasesHour() {
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.ALARM, mAlarmsDataSource,
                mManageAlarmView, new AlarmValidator(), mAlarmsScheduler);

        mManageAlarmPresenter.start();

        // When add hour button is pressed
        mManageAlarmPresenter.addHours("23", 1);

        // view call must be made
        verify(mManageAlarmView).setHour("00");
    }

    @Test
    public void subtractHourAction_decreasesHour() {
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.ALARM, mAlarmsDataSource,
                mManageAlarmView, new AlarmValidator(), mAlarmsScheduler);

        mManageAlarmPresenter.start();

        // When subtract hour button is pressed
        mManageAlarmPresenter.addHours("00", -1);

        // view call must be made
        verify(mManageAlarmView).setHour("23");
    }

    @Test
    public void addMinuteAction_increasesMinute() {
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.ALARM, mAlarmsDataSource,
                mManageAlarmView, new AlarmValidator(), mAlarmsScheduler);

        mManageAlarmPresenter.start();

        // when add minutes button is pressed
        mManageAlarmPresenter.addMinutes("59", 1);

        // view call must be made
        verify(mManageAlarmView).setMinute("00");
    }

    @Test
    public void subtractMinuteAction_decreasesMinute() {
        mManageAlarmPresenter = new ManageAlarmPresenter(0, AlarmType.ALARM, mAlarmsDataSource,
                mManageAlarmView, new AlarmValidator(), mAlarmsScheduler);

        mManageAlarmPresenter.start();

        // When subtract minutes button is pressed
        mManageAlarmPresenter.addMinutes("00", -1);

        // view call must be made
        verify(mManageAlarmView).setMinute("59");
    }
}

