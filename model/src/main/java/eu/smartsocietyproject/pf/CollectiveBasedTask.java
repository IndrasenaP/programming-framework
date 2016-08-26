package eu.smartsocietyproject.pf;

import eu.smartsocietyproject.pf.cbthandlers.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CollectiveBasedTask implements Future<TaskResult> {

    private final SmartSocietyApplicationContext context;
    private final TaskRequest request;
    Logger logger = LoggerFactory.getLogger(CollectiveBasedTask.class);

    // transition flag constants
    protected static final int DO_PROVISIONING    = 1;
    protected static final int DO_COMPOSITION     = 2;
    protected static final int DO_NEGOTIATION     = 4;
    protected static final int DO_EXECUTION       = 8;
    protected static final int DO_CONTINUOUS_ORCHESTRATION = 16;

    private final Set<LaborMode> laborMode;
    private volatile CollectiveBasedTask.State state;
    private double finalStateQoS = 0.0;
    public final UUID uuid;


    // variable storing transition flags for various states. Initialized to TRUE for every transition.
    private int transition_flags =  DO_PROVISIONING |
                                    DO_COMPOSITION |
                                    DO_NEGOTIATION |
                                    DO_EXECUTION |
                                    DO_CONTINUOUS_ORCHESTRATION;

    public enum State {
        INITIAL,
        PROVISIONING, COMPOSITION, NEGOTIATION, EXECUTION, CONTINUOUS_ORCHESTRATION,
        WAITING_FOR_PROVISIONING, WAITING_FOR_COMPOSITION, WAITING_FOR_NEGOTIATION, WAITING_FOR_EXECUTION, WAITING_FOR_CONTINUOUS_ORCHESTRATION,
        PROV_FAIL, COMP_FAIL, NEG_FAIL, EXEC_FAIL, ORCH_FAIL,
        FINAL
    }


    /**
     * TODO:
     * Remove and update tests.
     */
    @Deprecated
    public CollectiveBasedTask() {
        this.uuid = UUID.randomUUID();
        this.laborMode = EnumSet.of(LaborMode.ON_DEMAND);
        this.state = CollectiveBasedTask.State.INITIAL;
        executor.execute(new CBTRunnable());
        context=null;
        request=null;
    }

    private CollectiveBasedTask(
        SmartSocietyApplicationContext context,
        TaskRequest request,
        TaskFlowDefinition definition) {
        this.context = context;
        this.request = request;
        this.uuid = UUID.randomUUID();
        this.laborMode = definition.getLaborMode();
        this.state = CollectiveBasedTask.State.INITIAL;
        executor.execute(new CBTRunnable());
    }

    public static CollectiveBasedTask create(
        SmartSocietyApplicationContext context, TaskRequest request,
        TaskFlowDefinition definition) {
        return new CollectiveBasedTask(context, request, definition);
    }

    private final static ExecutorService executor = new ThreadPoolExecutor(  //can return both Executor and ExecutorService
        30, // the number of threads to keep active in the pool, even if they are idle
        1000, // the maximum number of threads to allow in the pool. After that, the tasks are queued
        1L, TimeUnit.HOURS, // when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
        new LinkedBlockingQueue<Runnable>()
    );

    private final Lock lock = new ReentrantLock();
    private final Lock getMethodlock = new ReentrantLock();
    private final Condition canProceed = lock.newCondition();
    private final Condition getMethodCanReturn = getMethodlock.newCondition();
    private volatile boolean wasCancelled = false;
    private volatile boolean wasInterrupted = false;
    private volatile boolean wasExecutionException = false;


    private void cancelAllHandlers(){
        logger.debug("Cancelling/Stopping all futures.");
        if (this.provisioningFuture != null)
            if (provisioningFuture.cancel(true)){
                logger.debug("Cancelled PROVISIONING future.");
            }
        if (this.compositionFuture != null)
            if (compositionFuture.cancel(true)){
                logger.debug("Cancelled COMPOSITION future.");
            }
        if (this.negotiationFuture != null)
            if (negotiationFuture.cancel(true)){
                logger.debug("Cancelled NEGOTIATION future.");
            }
        if (this.executionFuture != null)
            if (executionFuture.cancel(true)){
                logger.debug("Cancelled EXECUTION future.");
            }
        if (this.continuousOrchestrationFuture != null)
            if (continuousOrchestrationFuture.cancel(true)){
                logger.debug("Cancelled CONTINUOUS ORCHESTRATION future.");
            }

    }

    /**
     * Sets the state to FINAL, and notifies remote services to stop (if CBT relies on any), e.g., Orch. Mgr.
     * If overriding, the minimum the method must do is set the state to FINAL.
     */
    private void finalizeCBTandCleanUp(){
        this.state = CollectiveBasedTask.State.FINAL;
        if (!wasCancelled) cancelAllHandlers();
        getMethodlock.lock();
        getMethodCanReturn.signal();
        getMethodlock.unlock();

    }

    private Future<ApplicationBasedCollective> provisioningFuture = null;
    private Future<List<CollectiveWithPlan>> compositionFuture = null;
    private Future<CollectiveWithPlan> negotiationFuture = null;
    private Future<TaskResult> executionFuture = null;
    private Future<CollectiveWithPlan> continuousOrchestrationFuture = null;

    private Collective inputCollective = null;
    private ApplicationBasedCollective provisioned = null;
    private CollectiveWithPlan agreed = null;
    private List<CollectiveWithPlan> negotiables = null;

    private TaskResult result = null; // result of execution

    public TaskRequest getTaskRequest() {
        return request;
    }

    private void invokeHandlerForCurrentState(){
        switch (state){
            case PROVISIONING:
                Callable<ApplicationBasedCollective> provisioningCallable= () -> {
                    ProvisioningHandler handler = new DummyProvisioningHandlerImpl();
                    ApplicationBasedCollective provisioned = handler.provision(getTaskRequest(), null); // this will last...
                    return provisioned;
                };
                this.provisioningFuture = executor.submit(provisioningCallable);
                break;

            case COMPOSITION:
                Callable<List<CollectiveWithPlan>> compositionCallable= () -> {
                    CompositionHandler handler = new DummyCompositionHandlerImpl();
                    return handler.compose(this.provisioned, getTaskRequest());
                };
                this.compositionFuture = executor.submit(compositionCallable);
                break;

            case NEGOTIATION:
                Callable<CollectiveWithPlan> negotiationCallable= () -> {
                    NegotiationHandler handler = new DummyNegotiationHandlerImpl();
                    return handler.negotiate(this.negotiables);
                };
                this.negotiationFuture = executor.submit(negotiationCallable);
                logger.debug("initialized negotiation handler.");
                break;

            case EXECUTION:
                Callable<TaskResult> executionCallable= () -> {
                    ExecutionHandler handler = new DummyExecutionHandlerImpl();
                    return handler.execute(this.agreed);
                };
                this.executionFuture = executor.submit(executionCallable);
                logger.debug("initialized execution handler.");
                break;
        }// end switch
    }

    private boolean isOnDemand(){
        return laborMode.contains(LaborMode.ON_DEMAND);
    }

    private boolean isOpenCall() {
        return laborMode.contains(LaborMode.OPEN_CALL);
    }

    private class CBTRunnable implements Runnable {
        @Override
        public void run() {
            try {
                lock.lock(); //tied to canProceed condition
                logger.debug("Starting CBT thread.");

                if (!wasStarted){
                    canProceed.await();
                }

                if (isOnDemand()) {
                    state = CollectiveBasedTask.State.WAITING_FOR_PROVISIONING;
                } else {
                    state = CollectiveBasedTask.State.WAITING_FOR_CONTINUOUS_ORCHESTRATION;
                }

                while (!isCancelled() && !isDone()) {
                    logger.debug("LOOP");

                    /* WAITING_FOR_PROVISIONING */
                    if (isWaitingForProvisioning()) {
                        while (!getDoProvision()) {
                            logger.debug("Flag disabled. Waiting for provisioning...");
                            canProceed.await();
                            if (isCancelled()) {
                                logger.debug("Thread cancelled while waiting on provisioning. Aborting");
                                finalizeCBTandCleanUp();
                                return;
                            }
                        }
                        state = CollectiveBasedTask.State.PROVISIONING;
                        invokeHandlerForCurrentState();
                        try {
                            lock.unlock();
                            logger.debug("Waiting for provisioning to return");
                            provisioned = provisioningFuture.get();
                            lock.lock();
                            //success
                            if (isOpenCall()) {
                                state = CollectiveBasedTask.State.WAITING_FOR_COMPOSITION;
                            }else{
                                state = CollectiveBasedTask.State.PROV_FAIL;
                            }
                        }catch (Exception e){
                            state = CollectiveBasedTask.State.PROV_FAIL;
                        }
                    }// end code for WAITING_FOR_PROVISIONING;
                    else

                    /* WAITING_FOR_COMPOSITION */
                    if (isWaitingForComposition()){
                        while (!getDoCompose()) {
                            logger.debug("Flag disabled. Waiting for composition...");
                            canProceed.await();
                            if (isCancelled()) {
                                logger.debug("Thread cancelled while waiting on compose. Aborting");
                                finalizeCBTandCleanUp();
                                break;
                            }
                        }
                        state = CollectiveBasedTask.State.COMPOSITION;
                        invokeHandlerForCurrentState();
                        try {
                            lock.unlock();
                            logger.debug("CBT run(): Waiting for composition to return");
                            negotiables = compositionFuture.get();
                            logger.debug("CBT run() Got result of composition. Trying to acquire lock.");
                            lock.lock();
                            if (wasCancelled) {finalizeCBTandCleanUp(); return;}
                            //success
                            state = CollectiveBasedTask.State.WAITING_FOR_NEGOTIATION;
                        }catch (ExecutionException e){
                            state = CollectiveBasedTask.State.COMP_FAIL;
                        }
                    }// end code for WAITING_FOR_COMPOSITION;
                    else

                    /* WAITING_FOR_NEGOTIATION */
                        if (isWaitingForNegotiation()){
                            while (!getDoNegotiate()) {
                                logger.debug("Flag disabled. Waiting for negotiation...");
                                canProceed.await();
                                if (isCancelled()) {
                                    logger.debug("Thread cancelled while waiting on negotiate. Aborting");
                                    finalizeCBTandCleanUp();
                                    break;
                                }
                            }
                            state = CollectiveBasedTask.State.NEGOTIATION;
                            invokeHandlerForCurrentState();
                            try {
                                lock.unlock();
                                logger.debug("CBT run(): Waiting for negotiation to return");
                                agreed = negotiationFuture.get();
                                logger.debug("CBT run() Got result of negotiation. Trying to acquire lock.");
                                lock.lock();
                                if (wasCancelled) {finalizeCBTandCleanUp(); break;}
                                //success
                                state = CollectiveBasedTask.State.WAITING_FOR_EXECUTION;
                            }catch (ExecutionException e){
                                state = CollectiveBasedTask.State.NEG_FAIL;
                            }
                        }// end code for WAITING_FOR_NEGOTIATION;
                        else

                    /* WAITING_FOR_EXECUTION */
                            if (isWaitingForExecution()){
                                while (!getDoExecute()) {
                                    logger.debug("Flag disabled. Waiting for execution...");
                                    canProceed.await();
                                    if (isCancelled()) {
                                        logger.debug("Thread cancelled while waiting on execute. Aborting");
                                        finalizeCBTandCleanUp();
                                        break;
                                    }
                                }
                                state = CollectiveBasedTask.State.EXECUTION;
                                invokeHandlerForCurrentState();
                                try {
                                    lock.unlock();
                                    logger.debug("CBT run(): Waiting for execution to return");
                                    result = executionFuture.get();
                                    logger.debug("CBT run() Got result of execution. Trying to acquire lock.");
                                    lock.lock();
                                    if (wasCancelled) {finalizeCBTandCleanUp(); break;}
                                    //success
                                    state = CollectiveBasedTask.State.FINAL;
                                    finalStateQoS = 1.0; // will be read from TEM normally
                                }catch (ExecutionException e){
                                    state = CollectiveBasedTask.State.EXEC_FAIL;
                                }
                            }// end code for WAITING_FOR_EXECUTION;
                            else

                    /* WAITING_FOR_CONTINUOUS_ORCHESTRATION */
                                if (isWaitingForContinuousOrchestration()){
                                    while (!getDoContinuousOrchestration()) {
                                        logger.debug("Flag disabled. Waiting for continuous orchestration...");
                                        canProceed.await();
                                        if (isCancelled()) {
                                            logger.debug("Thread cancelled while waiting on continuous orchestration. Aborting");
                                            finalizeCBTandCleanUp();
                                            break;
                                        }
                                    }
                                    state = CollectiveBasedTask.State.CONTINUOUS_ORCHESTRATION;
                                    invokeHandlerForCurrentState();
                                    try {
                                        lock.unlock();
                                        logger.debug("CBT run(): Waiting for continuous orchestration to return");
                                        agreed = continuousOrchestrationFuture.get();
                                        logger.debug("CBT run() Got result of continuous orchestration. Trying to acquire lock.");
                                        lock.lock();
                                        if (wasCancelled) {finalizeCBTandCleanUp(); break;}
                                        //success
                                        state = CollectiveBasedTask.State.WAITING_FOR_EXECUTION;
                                    }catch (ExecutionException e){
                                        state = CollectiveBasedTask.State.ORCH_FAIL;
                                    }
                                }// end code for WAITING_FOR_CONTINUOUS_ORCHESTRATION;
                                else
                    /* TODO: Implement non-dummy business logic for FAIL states. Currently, fail permanently*/
                    /* FAIL STATES */
                                if (state == CollectiveBasedTask.State.PROV_FAIL){
                                    logger.debug("Provisioning failed. Go to FINAL state");
                                    state = CollectiveBasedTask.State.FINAL;
                                    finalStateQoS = 0.0;
                                }
                                else
                                if (state == CollectiveBasedTask.State.COMP_FAIL){
                                    logger.debug("Composition failed. Go to FINAL state");
                                    state = CollectiveBasedTask.State.FINAL;
                                    finalStateQoS = 0.0;
                                }
                                else
                                if (state == CollectiveBasedTask.State.NEG_FAIL){
                                    logger.debug("Negotiation failed. Go to FINAL state");
                                    state = CollectiveBasedTask.State.FINAL;
                                    finalStateQoS = 0.0;
                                }
                                else
                                if (state == CollectiveBasedTask.State.EXEC_FAIL){
                                    logger.debug("Execution failed. Go to FINAL state");
                                    state = CollectiveBasedTask.State.FINAL;
                                    finalStateQoS = 0.0;
                                }
                                else
                                if (state == CollectiveBasedTask.State.ORCH_FAIL){
                                    logger.debug("Orchestration failed. Go to FINAL state");
                                    state = CollectiveBasedTask.State.FINAL;
                                    finalStateQoS = 0.0;
                                }

                    //TODO: Change this:
                    if (state == State.FINAL) {
                        logger.debug("normal end of CBT. ABout to cancel futures");
                        finalizeCBTandCleanUp();
                        logger.debug("Done.");
                    }

                } // end big while loop over waiting states




            }catch (InterruptedException iexc){
                // If InterruptedException was caught, interrupted status was reset. Let's set it back
                logger.debug("CTB thread " +  uuid.toString() + " received InterruptedException. Stopping.");
                wasInterrupted = true;
                Thread.currentThread().interrupt();
            }catch (CancellationException cex){
                if (!wasCancelled){
                    logger.debug("Something cancelled one of the futures");
                }
                // otherwise we cancelled the futures, so at get() the exception was thrown, but this is expected
            }
            catch (Exception e) {
                wasExecutionException = true;
            }
            finally {
                lock.unlock();
            }

        }
    }



    public final CollectiveBasedTask.State getCurrentState() {
         return this.state;
    }

    public final boolean isWaitingFor(CollectiveBasedTask.State thisState){
        return this.getCurrentState() == thisState;
    }

    private boolean isWaitingForExecution() {
        return isWaitingFor(CollectiveBasedTask.State.WAITING_FOR_EXECUTION);
    }
    private boolean isWaitingForComposition() {
        return isWaitingFor(CollectiveBasedTask.State.WAITING_FOR_COMPOSITION);
    }
    private boolean isWaitingForNegotiation() {
        return isWaitingFor(CollectiveBasedTask.State.WAITING_FOR_NEGOTIATION);
    }
    private boolean isWaitingForProvisioning() {
        return isWaitingFor(CollectiveBasedTask.State.WAITING_FOR_PROVISIONING);
    }
    private boolean isWaitingForContinuousOrchestration() {
        return isWaitingFor(CollectiveBasedTask.State.WAITING_FOR_CONTINUOUS_ORCHESTRATION);
    }

    public final boolean isWaitingForStart() {
        return !wasStarted;
    } // waiting in the initial state to enter any main CollectiveBasedTask.State.

    /* Getters and setters for transition flags*/
    public final boolean getDoProvision(){
        return (DO_PROVISIONING & this.transition_flags) > 0;
    }

    public final boolean getDoCompose(){
        return (DO_COMPOSITION & this.transition_flags) > 0;
    }

    public final boolean getDoNegotiate(){
        return (DO_NEGOTIATION & this.transition_flags) > 0;
    }

    public final boolean getDoExecute(){
        return (DO_EXECUTION & this.transition_flags) > 0;
    }
    public final boolean getDoContinuousOrchestration(){
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







    //
    //
    //
    /* IMPLEMENTATION OF THE Future API */
    //
    //
    //

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
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (getCurrentState() == CollectiveBasedTask.State.FINAL) return false; // already completed
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
                return true;
            }
            cancelAllHandlers();
        }

        wasCancelled = true; // to make sure subsequent calls to isDone() will return true
        getMethodlock.lock();
        getMethodCanReturn.signal();
        getMethodlock.unlock();

        lock.unlock();

        return false; // cause we did not explicitly manage to cancel it with this invocation
    }



    public boolean isRunning() {
        return (wasStarted && !isDone() && !isCancelled());
    }

    private volatile boolean wasStarted = false;
    public void start(){
        logger.debug("start() invoked");

        if (!wasStarted) {
            logger.debug("CBT was stopped until now.");
            lock.lock();
            logger.debug("Signalling start");
            wasStarted = true;
            canProceed.signal();
            lock.unlock();
        }
    }
    /**
     * Returns {@code true} if this task was cancelled before it completed
     * normally.
     *
     * @return {@code true} if this task was cancelled before it completed
     */
    @Override
    public boolean isCancelled() {
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
    @Override
    public boolean isDone() {
        if (this.state == CollectiveBasedTask.State.FINAL || this.wasCancelled){
            return true;
        }
        return false;
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
    @Override
    public TaskResult get() throws InterruptedException, ExecutionException, CancellationException {
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
        getMethodCanReturn.await();

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
    @Override
    public TaskResult get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
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

    private void isComparingWithIntermediateState(CollectiveBasedTask.State compareWith) throws IllegalArgumentException{

        if (compareWith == CollectiveBasedTask.State.WAITING_FOR_PROVISIONING ||
                compareWith == CollectiveBasedTask.State.WAITING_FOR_COMPOSITION ||
                compareWith == CollectiveBasedTask.State.WAITING_FOR_NEGOTIATION ||
                compareWith == CollectiveBasedTask.State.WAITING_FOR_EXECUTION ||
                compareWith == CollectiveBasedTask.State.WAITING_FOR_CONTINUOUS_ORCHESTRATION ||
                compareWith == CollectiveBasedTask.State.PROV_FAIL ||
                compareWith == CollectiveBasedTask.State.COMP_FAIL ||
                compareWith == CollectiveBasedTask.State.NEG_FAIL ||
                compareWith == CollectiveBasedTask.State.EXEC_FAIL ||
                compareWith == CollectiveBasedTask.State.ORCH_FAIL
                )
        {
            throw new IllegalArgumentException("Cannot use intermediate states in comparison");
        }
    }

    private int getStateSequenceNumber(CollectiveBasedTask.State theState){
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
    public boolean isAfter(CollectiveBasedTask.State compareWith) throws IllegalArgumentException {

        isComparingWithIntermediateState(compareWith); // throws exception if illegal argument
        CollectiveBasedTask.State cbtState = this.state; // read once, so no need for locking
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


    public boolean isBefore(CollectiveBasedTask.State compareWith) throws IllegalArgumentException {

        isComparingWithIntermediateState(compareWith); // throws exception if illegal argument
        CollectiveBasedTask.State cbtState = this.state; // read once, so no need for locking
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

    public boolean isIn(CollectiveBasedTask.State compareWith) throws IllegalArgumentException {
        isComparingWithIntermediateState(compareWith); // throws exception if illegal argument
        return this.state == compareWith;
    }


    public enum LaborMode {
        ON_DEMAND,
        OPEN_CALL
    }
}



