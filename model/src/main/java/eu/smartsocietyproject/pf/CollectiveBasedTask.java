package eu.smartsocietyproject.pf;

import akka.actor.AbstractActor;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import eu.smartsocietyproject.pf.enummerations.LaborMode;
import eu.smartsocietyproject.pf.enummerations.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CollectiveBasedTask extends AbstractActor {

    private final ApplicationContext context;
    private final TaskRequest request;
    private final TaskFlowDefinition definition;
    private Logger logger = LoggerFactory.getLogger(CollectiveBasedTask.class);
    private Set<LaborMode> laborMode;

    // transition flag constants
    private static final int DO_PROVISIONING    = 1;
    private static final int DO_COMPOSITION     = 2;
    private static final int DO_NEGOTIATION     = 4;
    private static final int DO_EXECUTION       = 8;
    private static final int DO_CONTINUOUS_ORCHESTRATION = 16;


    private volatile State state;
    private double finalStateQoS = 0.0;
    public final UUID uuid;


    // variable storing transition flags for various states. Initialized to TRUE for every transition.
    private int transition_flags =  DO_PROVISIONING |
                                    DO_COMPOSITION |
                                    DO_NEGOTIATION |
                                    DO_EXECUTION |
                                    DO_CONTINUOUS_ORCHESTRATION;

    /**
     * TODO:
     * Remove and update tests.
     */
    @Deprecated
    public CollectiveBasedTask() {
        this.uuid = UUID.randomUUID();
        this.laborMode = EnumSet.of(LaborMode.ON_DEMAND);
        this.state = State.INITIAL;
        context=null;
        request=null;
        definition=null;
    }

    private CollectiveBasedTask(
        ApplicationContext context,
        TaskRequest request,
        TaskFlowDefinition definition) {
        this.context = context;
        this.request = request;
        this.definition = definition;
        this.uuid = UUID.randomUUID();
        this.state = State.INITIAL;
        if (definition.getCollectiveforProvisioning().isPresent())
            this.inputCollective = definition.getCollectiveforProvisioning().get();
    }

    public static CollectiveBasedTask create(
        ApplicationContext context, TaskRequest request,
        TaskFlowDefinition definition) {
        return new CollectiveBasedTask(context, request, definition);
    }

    private volatile boolean wasCancelled = false;
    private volatile boolean wasInterrupted = false;
    private volatile boolean wasExecutionException = false;

    /**
     * Sets the state to FINAL, and notifies remote services to stop (if CBT relies on any), e.g., Orch. Mgr.
     * If overriding, the minimum the method must do is set the state to FINAL.
     */
    private void finalizeCBTandCleanUp(){
        this.state = State.FINAL;
        //TODO kill children processes (CBTRunner and QAService)
    }

    private Collective inputCollective = null;
    private ApplicationBasedCollective provisioned = null;
    private CollectiveWithPlan agreed = null;
    private List<CollectiveWithPlan> negotiables = null;

    private TaskResult result;


    private State getCurrentState() {
         return this.state;
    }

    private boolean isWaitingFor(State thisState){
        return this.getCurrentState() == thisState;
    }

    private boolean isWaitingForExecution() {
        return isWaitingFor(State.WAITING_FOR_EXECUTION);
    }
    private boolean isWaitingForComposition() {
        return isWaitingFor(State.WAITING_FOR_COMPOSITION);
    }
    private boolean isWaitingForNegotiation() {
        return isWaitingFor(State.WAITING_FOR_NEGOTIATION);
    }
    private boolean isWaitingForProvisioning() {
        return isWaitingFor(State.WAITING_FOR_PROVISIONING);
    }
    private boolean isWaitingForContinuousOrchestration() {
        return isWaitingFor(State.WAITING_FOR_CONTINUOUS_ORCHESTRATION);
    }

    private boolean isWaitingForStart() {
        return !wasStarted;
    } // waiting in the initial state to enter any main State.

    /* Getters and setters for transition flags*/
    private boolean getDoProvision(){
        return (DO_PROVISIONING & this.transition_flags) > 0;
    }

    private boolean getDoCompose(){
        return (DO_COMPOSITION & this.transition_flags) > 0;
    }

    private boolean getDoNegotiate(){
        return (DO_NEGOTIATION & this.transition_flags) > 0;
    }

    private boolean getDoExecute(){
        return (DO_EXECUTION & this.transition_flags) > 0;
    }
    private boolean getDoContinuousOrchestration(){
        return (DO_CONTINUOUS_ORCHESTRATION & this.transition_flags) > 0;
    }



    public final void setDoProvision(boolean newValue){
        if (newValue) {
            this.transition_flags |= DO_PROVISIONING;
        }else{
            this.transition_flags &= ~DO_PROVISIONING;
        }
    }

    public final void setDoCompose(boolean newValue){
        if (newValue) {
            this.transition_flags |= DO_COMPOSITION;
        }else{
            this.transition_flags &= ~DO_COMPOSITION;
        }
    }

    public final void setDoNegotiate(boolean newValue){
        if (newValue) {
            this.transition_flags |= DO_NEGOTIATION;
        }else{
            this.transition_flags &= ~DO_NEGOTIATION;
        }
    }

    public final void setDoExecute(boolean newValue){
        if (newValue) {
            this.transition_flags |= DO_EXECUTION;
        }else{
            this.transition_flags &= ~DO_EXECUTION;
        }
    }

    public final void setDoContinuousOrchestration(boolean newValue){
        if (newValue) {
            this.transition_flags |= DO_CONTINUOUS_ORCHESTRATION;
        }else{
            this.transition_flags &= ~DO_CONTINUOUS_ORCHESTRATION;
        }
    }

    public final void setAllTransitionsTo(boolean tf){
        this.transition_flags =     DO_PROVISIONING |
                                    DO_COMPOSITION |
                                    DO_NEGOTIATION |
                                    DO_EXECUTION |
                                    DO_CONTINUOUS_ORCHESTRATION;
    }

    public boolean finishedWithSuccess(){
        return finalStateQoS >= 0.5;
    }

    /**
     * Attempts to cancel execution of this task.  This attempt will
     * fail if the task has already completed, has already been cancelled,
     * or could not be cancelled for some other reason. If successful,
     * and this task has not started when {@code cancel} is called,
     * this task should never run.  If the task has already started,
     * then the {@code mayInterruptIfRunning} parameter determines
     * whether the thread executing this task should be interrupted in
     * an attempt to stop the task.
     * <p>
     * <p>After this method returns, subsequent calls to {@link #isDone} will
     * always return {@code true}.  Subsequent calls to {@link #isCancelled}
     * will always return {@code true} if this method returned {@code true}.
     *
     * @param mayInterruptIfRunning {@code true} if the thread executing this
     *                              task should be interrupted; otherwise, in-progress tasks are allowed
     *                              to complete
     * @return {@code false} if the task could not be cancelled,
     * typically because it has already completed normally;
     * {@code true} otherwise
     */

    public boolean cancel(boolean mayInterruptIfRunning) {

        //TODO send message to child CBTRunner to cancel

        /*
        if (getCurrentState() == State.FINAL) return false; // already completed
        if (wasCancelled) return false; // already been cancelled

        lock.lock();
        if (isWaitingForStart()){
            boolean do_return = false;

            if (isWaitingForStart()) { //check again
                do_return = true;
                wasCancelled = true;
                getMethodlock.lock();
                getMethodCanReturn.signal();
                getMethodlock.unlock();
                canProceed.signal();       // if not even started, task will not run
            }
            lock.unlock();
            if (do_return) return true;
        }
        if (!isWaitingForStart()){ // already running, or already finished.
            if (isDone()) { // already finished
                lock.unlock();
                return false;
            }
            if (mayInterruptIfRunning) {  // interrupt only if explicitly allowed by user
                wasCancelled = true;

                getMethodlock.lock();
                getMethodCanReturn.signal();
                getMethodlock.unlock();

                canProceed.signal();
                lock.unlock();

                //todo-sv: check with ogi. moved this up here so handler get canceld in case of force
                cancelAllHandlers();

                return true;
            }
        }

        wasCancelled = true; // to make sure subsequent calls to isDone() will return true
        getMethodlock.lock();
        getMethodCanReturn.signal();
        getMethodlock.unlock();

        lock.unlock();
*/
        return false; // cause we did not explicitly manage to cancel it with this invocation
    }



    public boolean isRunning() {
        return (wasStarted && !isDone() && !isCancelled());
    }

    private boolean wasStarted = false;
    public void start(){
        logger.debug("start() invoked");

        if (!wasStarted) {
            logger.debug("CBT was stopped until now.");
            wasStarted = true;
        }
    }
    /**
     * Returns {@code true} if this task was cancelled before it completed
     * normally.
     *
     * @return {@code true} if this task was cancelled before it completed
     */
    //@Override
    public boolean isCancelled() {
        //TODO modify this method
        return this.wasCancelled;
    }

    /**
     * Returns {@code true} if this task completed.
     * <p>
     * Completion may be due to normal termination, an exception, or
     * cancellation -- in all of these cases, this method will return
     * {@code true}.
     *
     * @return {@code true} if this task completed
     */
    //@Override
    public boolean isDone() {
        //TODO modify this method
       return this.state == State.FINAL || this.wasCancelled;
    }


    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     *
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException    if the computation threw an
     *                               exception
     * @throws InterruptedException  if the current thread was interrupted
     *                               while waiting
     */
    //@Override
    public TaskResult get() throws InterruptedException, ExecutionException, CancellationException {
        //TODO remove this method
        /*
        try {
            return get(-1, null);
        } catch (TimeoutException ex) {
            //should never happen since it uses await()
            throw new ExecutionException(ex);
        } */
        return null;
    }

    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result, if available.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException    if the computation threw an
     *                               exception
     * @throws InterruptedException  if the current thread was interrupted
     *                               while waiting
     * @throws TimeoutException      if the wait timed out
     */
    //@Override
    public TaskResult get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

        //TODO remove this method

        /*
        getMethodlock.lock();
        if (isCancelled()) {
            getMethodlock.unlock();
            throw new CancellationException("Future.get() called on already cancelled CBT");
        }
        if (isDone()) {
            getMethodlock.unlock();
            return result;
        }

        // it might be waiting to start or started
        if(timeout == -1) {
            getMethodCanReturn.await();
        } else {
            if (!getMethodCanReturn.await(timeout, unit)) {
                //cancel running handlers if timed out
                this.cancel(true);
                //check if we have allready obtained result thats "good enough"
                if (definition.getExecutionHandler() != null) {
                    result = definition.getExecutionHandler()
                            .getResultIfQoRGoodEnough();
                    if (result != null) {
                        getMethodlock.unlock();
                        return result;
                    }
                }
                //otherwise throw timeout exception
                throw new TimeoutException("CBT timed out!");
            }
        }

        getMethodlock.unlock();

        if (isCancelled()) {
            throw new CancellationException("Future.get() called on already cancelled CBT");
        }

        if (finishedWithSuccess()){
            return result;
        }else{
            //TODO: Throw ExecutionException
            return null;
        }
        */
        return null;
    }


    /**
     *
     * @return returns the Collective that was used as the input for the CBT
     */
    public Collective getCollectiveInput() {
        return inputCollective;
    }

    /**
     *
     * @return  returns the ‘provisioned’ collective
     */
    public ApplicationBasedCollective getCollectiveProvisioned(){
        //TODO: We can either make the handler APIs more specific, to make sure they return ABCs,
        //      or we can make sure that upon exposing the collectives created at runtime they
        //      get correctly cast to ABCs.
        return (ApplicationBasedCollective) provisioned;
    }
    public ApplicationBasedCollective getCollectiveAgreed(){
        if (agreed == null) return null;
        return (ApplicationBasedCollective) agreed.getCollective();
    }

    public List<ApplicationBasedCollective> getNegotiables(){
        if (negotiables== null) return null;
        return negotiables.stream().map(cwp -> (ApplicationBasedCollective) cwp.getCollective()).collect(Collectors.toCollection(ArrayList::new));
    }

    private void isComparingWithIntermediateState(State compareWith) throws IllegalArgumentException{

        if (compareWith == State.WAITING_FOR_PROVISIONING ||
                compareWith == State.WAITING_FOR_COMPOSITION ||
                compareWith == State.WAITING_FOR_NEGOTIATION ||
                compareWith == State.WAITING_FOR_EXECUTION ||
                compareWith == State.WAITING_FOR_CONTINUOUS_ORCHESTRATION ||
                compareWith == State.PROV_FAIL ||
                compareWith == State.COMP_FAIL ||
                compareWith == State.NEG_FAIL ||
                compareWith == State.EXEC_FAIL ||
                compareWith == State.ORCH_FAIL
                )
        {
            throw new IllegalArgumentException("Cannot use intermediate states in comparison");
        }
    }

    private int getStateSequenceNumber(State theState){
        switch (theState){
            case INITIAL:
                return 0;
            case PROVISIONING:
                return 2;
            case COMPOSITION:
                return 4;
            case NEGOTIATION:
                return 6;
            case EXECUTION:
                return 8;
            case CONTINUOUS_ORCHESTRATION:
                return 1;
            case FINAL:
                return 100;
            default:
                return -1;
        }
    }

    /**
     * Returns true if the CBT has finished executing the `compareWith' state;
     * this also includes waiting on the subsequent state.
     * Throws exception if the comparison is illogical.
     * Add logic to make sure this is consistent with start/end states,
     * and also continuous orchestration.
     * Throw exceptions for impossible comparisons,
     * e.g., if cbt is of CollaborationType.OC, that means  continuous_orchestration will be used.
     * In this case, comparison cbt.isAfter(CBTState.NEGOTIATION) makes no sense and should throw exception.
     *
     * @param compareWith state to compare to
     * @throws IllegalArgumentException
     * @return
     */
    public boolean isAfter(State compareWith) throws IllegalArgumentException {

        isComparingWithIntermediateState(compareWith); // throws exception if illegal argument
        State cbtState = this.state; // read once, so no need for locking
        if (cbtState == compareWith) return false;

        int stateIndex = getStateSequenceNumber(cbtState);
        int compareWithIndex = getStateSequenceNumber(compareWith);

        if (compareWithIndex == 1) {
            if (stateIndex < 1) return false;
            if (stateIndex >= 8) return true;
            throw new IllegalArgumentException("Cannot compare CONTINUOUS ORCHESTRATION with purely on-demand states");
        }

        return stateIndex > compareWithIndex;

    }


    public boolean isBefore(State compareWith) throws IllegalArgumentException {

        isComparingWithIntermediateState(compareWith); // throws exception if illegal argument
        State cbtState = this.state; // read once, so no need for locking
        if (cbtState == compareWith) return false;

        int stateIndex = getStateSequenceNumber(cbtState);
        int compareWithIndex = getStateSequenceNumber(compareWith);

        if (compareWithIndex == 1) {
            if (stateIndex < 1) return true;
            if (stateIndex >= 8) return false;
            throw new IllegalArgumentException("Cannot compare CONTINUOUS ORCHESTRATION with purely on-demand states");
        }

        return stateIndex < compareWithIndex;

    }

    public boolean isIn(State compareWith) throws IllegalArgumentException {
        isComparingWithIntermediateState(compareWith); // throws exception if illegal argument
        return this.state == compareWith;
    }


    public void incentivize(String incentiveType, Object incentiveSpecificParams){
        ArrayList<Collective> collectivesToIncentivize = new ArrayList<>();
        if (    isIn(State.PROVISIONING) ||
                isIn(State.CONTINUOUS_ORCHESTRATION) ||
                isWaitingForContinuousOrchestration() || isWaitingForProvisioning()) {
            if (null != inputCollective) collectivesToIncentivize.add(inputCollective);
        }else if (isIn(State.COMPOSITION) || isWaitingForComposition()) {
            collectivesToIncentivize.add(provisioned);
        }else if (isIn(State.NEGOTIATION) || isWaitingForNegotiation()) {
            if ( !laborMode.contains(LaborMode.OPEN_CALL) ){
                collectivesToIncentivize.add(provisioned);
            }else{
                if (null != negotiables && negotiables.size() > 0)
                for (CollectiveWithPlan cwp : negotiables){
                    collectivesToIncentivize.add(cwp.getCollective());
                }
            }

        }else {
            collectivesToIncentivize.add(agreed.getCollective());
        }

        collectivesToIncentivize.stream().forEach(c -> c.incentivize(incentiveType, incentiveSpecificParams, null));

    }

    @Override
    public Receive createReceive() {
        return null;
    }
}



