package eu.smartsocietyproject.pf;

import akka.actor.AbstractActor;

import java.util.*;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.DTO.*;
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
  private ActorRef parent;
  private ApplicationBasedCollective provisioningABC;
  private ImmutableList<CollectiveWithPlan> negotiables;
  private CollectiveWithPlan agreed;
  private State state;
  private Collective inputCollective;
  private LinkedList<Props> provisionHandlers;
  private Props provisionHandler;
  private LinkedList<Props> negotiationHandlers;
  private Props negotiationHandler;
  private LinkedList<Props> compositionHandlers;
  private Props compositionHandler;
  private LinkedList<Props> executionHandlers;
  private Props executionHandler;
  private LinkedList<Props> continuousOrchestrationHandlers;
  private Props continuousOrchestrationHandler;
  private LinkedList<Props> qualityAssuranceHandlers;
  private Props qualityAssuranceHandler;
  private TaskResult result;
  private boolean wasStarted;
  private boolean wasCancelled;
  private boolean wasInterrupted;
  private boolean wasExecutionException;
  private boolean done;
  private double finalStateQoS = 0.0;
  private final UUID uuid;
  private NegotiationHandlerDTO negotiationHandlerDTO;
  private ExecutionHandlerDTO executionHandlerDTO;
  private QualityAssuranceHandlerDTO qualityAssuranceHandlerDTO;
  public static Props props(ApplicationContext context, TaskRequest request,
                            TaskFlowDefinition definition) {
    return Props.create(CollectiveBasedTask.class, () -> new CollectiveBasedTask(context, request, definition));
  }

  @Override
  public void preStart() throws Exception {
    this.parent = getContext().getParent();
  }

  /**
   * The constructor to the CollectiveBasedTask that is also going to
   * spawn five children actors (Provision, Composition, Negotiation and Execution)
   *
   * @param context
   * @param request
   * @param definition
   */
  private CollectiveBasedTask(
          ApplicationContext context,
          TaskRequest request,
          TaskFlowDefinition definition) {

    this.context = context;
    this.request = request;
    this.definition = definition;
    this.uuid = UUID.randomUUID();
    this.state = State.INITIAL;
    this.laborMode = definition.getLaborMode();
    if (definition.getCollectiveForProvisioning().isPresent())
      this.inputCollective = definition.getCollectiveForProvisioning().get();
  }

  public static CollectiveBasedTask create(
          ApplicationContext context, TaskRequest request,
          TaskFlowDefinition definition) {
    return new CollectiveBasedTask(context, request, definition);
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
   */
  private void cancel() {

    getContext().children().foreach(v1 -> {
      v1.tell(PoisonPill.getInstance(), getSelf());
      return v1;
    });

    this.wasCancelled = true;
  }

  public boolean isRunning() {
    return (wasStarted && !done && !wasCancelled);
  }

  private void internalState(ActorRef actorRef) {
    CollectiveBasedTaskDTO collectiveBasedTaskDTO =
            new CollectiveBasedTaskDTO(request, result, wasCancelled, wasInterrupted, wasExecutionException,
                    isRunning(), state, finalStateQoS, inputCollective);
    actorRef.tell(collectiveBasedTaskDTO, getSelf());
  }

  private void continuousOrchestrationHandlerInit() {
    try {
      if (this.continuousOrchestrationHandlers == null)
        this.continuousOrchestrationHandlers = new LinkedList<>(definition.getContinuousOrchestrationHandlers());
      this.continuousOrchestrationHandler = this.continuousOrchestrationHandlers.pop();
      setState(State.CONTINUOUS_ORCHESTRATION);
    } catch (IllegalStateException | NoSuchElementException e) {
      setState(State.FAIL);
    }
  }

  private void continuousOrchestration() {

    try {
      getContext().actorOf(continuousOrchestrationHandler)
              .tell(State.CONTINUOUS_ORCHESTRATION, getSelf());
    } catch (Exception e) {
      setState(State.ORCH_FAIL);
    }

  }

  private void provisionHandlerInit() {

    try {
      if (this.provisionHandlers == null)
        this.provisionHandlers =
                new LinkedList<>(definition.getProvisioningHandlers());
      this.provisionHandler = this.provisionHandlers.pop();
      setState(State.PROVISIONING);
    } catch (IllegalStateException | NoSuchElementException e) {
      setState(State.FAIL);
    }
  }


  private void provision() {

    try {
      Collective collective = definition
              .getCollectiveForProvisioning()
              .orElseThrow(Exception::new);
      getContext().actorOf(this.provisionHandler)
              .tell(collective, getSelf());
    } catch (Exception e) {
      setState(this.definition
              .getProvisioningAdaptationPolicy()
              .adapt(getSelf()));
    }
  }

  private void compositionHandlerInit(ApplicationBasedCollective abc) {
    try {
      if (this.compositionHandlers == null)
        this.compositionHandlers = new LinkedList<>(definition.getCompositionHandlers());
      this.provisioningABC = abc;
      this.compositionHandler = this.compositionHandlers.pop();
      setState(State.COMPOSITION);
    } catch (IllegalStateException | NoSuchElementException e) {
      setState(State.FAIL);
    }
  }

  private void composition() {

    try {
      getContext().actorOf(this.compositionHandler).tell(this.provisioningABC, getSelf());
    } catch (Exception e) {
      setState(this.definition.getCompositionAdaptationPolicy().adapt(getSelf()));
    }

  }

  private void negotiationHandlerInit(NegotiationHandlerDTO negotiationHandlerDTO) {
    try {
      if (this.negotiationHandlers == null)
        this.negotiationHandlers = new LinkedList<>(definition.getNegotiationHandlers());
      this.negotiables = negotiationHandlerDTO.getCollectivesWithPlan();
      this.negotiationHandler = this.negotiationHandlers.pop();
      setState(State.NEGOTIATION);
    } catch (IllegalStateException | NoSuchElementException e) {
      setState(State.FAIL);
    }
  }

  private void negotiation() {
    try {
      getContext().actorOf(this.negotiationHandler).tell(new NegotiationHandlerDTO(this.negotiables), getSelf());
    } catch (Exception e) {
      setState(this.definition.getNegotiationAdaptationPolicy().adapt(getSelf()));
    }

  }

  private void executionHandlerInit(ExecutionHandlerDTO executionHandlerDTO) {
    try {
      if (this.executionHandlers == null)
        this.executionHandlers = new LinkedList<>(definition.getExecutionHandlers());
      this.agreed = executionHandlerDTO.getCollectiveWithPlan();
      this.executionHandler = executionHandlers.pop();
      setState(State.EXECUTION);
    } catch (IllegalStateException | NoSuchElementException e) {
      setState(State.FAIL);
    }
  }

  private void execution() {

    try {
      getContext().actorOf(this.executionHandler).tell(this.agreed, getSelf());
    } catch (Exception e) {
      setState(this.definition.getExecutionAdaptationPolicy().adapt(getSelf()));
    }

  }

  private void qualityAssuranceHandlerInit(QualityAssuranceHandlerDTO qualityAssuranceHandlerDTO) {
    try {
      if (this.qualityAssuranceHandlers == null)
        this.qualityAssuranceHandlers = new LinkedList<>(definition.getQualityAssuranceHandlers());
      this.result = qualityAssuranceHandlerDTO.getTaskResult();
      this.qualityAssuranceHandlerDTO = qualityAssuranceHandlerDTO;
      this.qualityAssuranceHandler = this.qualityAssuranceHandlers.pop();
      setState(State.QUALITY_ASSURANCE);
    } catch (IllegalStateException | NoSuchElementException e) {
      setState(State.FAIL);
    }
  }

  private void qualityAssurance() {
    try {
      getContext().actorOf(this.qualityAssuranceHandler).tell(this.result, getSelf());
    } catch (Exception e) {
      setState(this.definition.getQualityAssuranceAdaptionPolicy().adapt(getSelf()));
    }
  }


  private void isComparingWithIntermediateState(State compareWith) throws IllegalArgumentException {

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
    ) {
      throw new IllegalArgumentException("Cannot use intermediate states in comparison");
    }
  }

  private boolean isIn(State compareWith) throws IllegalArgumentException {
    isComparingWithIntermediateState(compareWith); // throws exception if illegal argument
    return this.state == compareWith;
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

  private boolean isWaitingFor(State thisState) {
    return this.getCurrentState() == thisState;
  }

  private State getCurrentState() {
    return this.state;
  }

  private void incentivize(IncentivizeDTO incentivizeDTO) {
    ArrayList<Collective> collectivesToIncentivize = new ArrayList<>();
    if (isIn(State.PROVISIONING) ||
            isIn(State.CONTINUOUS_ORCHESTRATION) ||
            isWaitingForContinuousOrchestration() || isWaitingForProvisioning()) {
      if (null != inputCollective) collectivesToIncentivize.add(inputCollective);
    } else if (isIn(State.COMPOSITION) || isWaitingForComposition()) {
      collectivesToIncentivize.add(provisioningABC);
    } else if (isIn(State.NEGOTIATION) || isWaitingForNegotiation()) {
      if (!laborMode.contains(LaborMode.OPEN_CALL)) {
        collectivesToIncentivize.add(provisioningABC);
      } else {
        if (null != negotiables && negotiables.size() > 0)
          for (CollectiveWithPlan cwp : negotiables) {
            collectivesToIncentivize.add(cwp.getCollective());
          }
      }

    } else {
      collectivesToIncentivize.add(agreed.getCollective());
    }

    collectivesToIncentivize.stream().forEach(c -> c.incentivize(incentivizeDTO.getIncentiveType(), incentivizeDTO.getIncentiveSpecificParams(), null));

  }

  private boolean isOpenCall() {
    return laborMode.contains(LaborMode.OPEN_CALL);
  }

  private void setState(State state) {

    this.state = state;
    switch (state) {
      case WAITING_FOR_PROVISIONING:
        provisionHandlerInit();
        break;
      case WAITING_FOR_CONTINUOUS_ORCHESTRATION:
        continuousOrchestrationHandlerInit();
        break;
      case PROVISIONING:
        provision();
        break;
      case PROV_FAIL:
        setState(this.definition
                .getProvisioningAdaptationPolicy()
                .adapt(getSelf()));
        break;
      case WAITING_FOR_COMPOSITION:
        compositionHandlerInit(this.provisioningABC);
        break;
      case COMPOSITION:
        composition();
        break;
      case COMP_FAIL:
        setState(this.definition
                .getCompositionAdaptationPolicy()
                .adapt(getSelf()));
        break;
      case WAITING_FOR_NEGOTIATION:
        negotiationHandlerInit(this.negotiationHandlerDTO);
        break;
      case NEGOTIATION:
        negotiation();
        break;
      case NEG_FAIL:
        setState(this.definition
                .getNegotiationAdaptationPolicy()
                .adapt(getSelf()));
        break;
      case WAITING_FOR_EXECUTION:
        executionHandlerInit(this.executionHandlerDTO);
        break;
      case EXECUTION:
        execution();
        break;
      case EXEC_FAIL:
        setState(this.definition
                .getExecutionAdaptationPolicy()
                .adapt(getSelf()));
        break;
      case WAITING_FOR_QUALITY_ASSURANCE:
        qualityAssuranceHandlerInit(this.qualityAssuranceHandlerDTO);
        break;
      case QUALITY_ASSURANCE:
        qualityAssurance();
        break;
      case QUALITY_ASSURANCE_FAIL:
        setState(this.definition.
                getQualityAssuranceAdaptionPolicy()
                .adapt(getSelf()));
        break;
      case CONTINUOUS_ORCHESTRATION:
        continuousOrchestration();
        break;
      default:
        cancel();
    }

    parent.tell(state, getSelf());
  }

  @Override
  public Receive createReceive() {

    return receiveBuilder()
            .match(State.class, this::setState)
            .match(ApplicationBasedCollective.class,
              abc -> {
                if (isOpenCall()) {
                  this.provisioningABC = abc;
                    setState(State
                      .WAITING_FOR_COMPOSITION);
                      } else {
                        this.negotiationHandlerDTO =
                          new NegotiationHandlerDTO(ImmutableList
                            .of(CollectiveWithPlan
                                    .of(abc, Plan.empty)));
                        setState(State.WAITING_FOR_NEGOTIATION);
                      }
                      getSender()
                              .tell(PoisonPill.getInstance(), getSelf());
                    })
            .match(NegotiationHandlerDTO.class,
                    negotiationHandlerDTO -> {
                      this.negotiationHandlerDTO = negotiationHandlerDTO;
                      setState(State.WAITING_FOR_NEGOTIATION);
                      getSender()
                              .tell(PoisonPill.getInstance(), getSelf());
                    })
            .match(ExecutionHandlerDTO.class,
                    executionHandlerDTO -> {
                      this.executionHandlerDTO = executionHandlerDTO;
                      setState(State.WAITING_FOR_EXECUTION);
                      getSender()
                              .tell(PoisonPill.getInstance(), getSelf());
                    })
            .match(QualityAssuranceHandlerDTO.class,
                    qualityAssuranceHandlerDTO -> {
                      this.qualityAssuranceHandlerDTO =
                              qualityAssuranceHandlerDTO;
                      setState(State.WAITING_FOR_QUALITY_ASSURANCE);
                      getSender()
                              .tell(PoisonPill.getInstance(), getSelf());
                    })
            .match(ResultDTO.class,
                    resultDTO -> {
                      this.finalStateQoS = resultDTO.getQor();
                      this.state = State.FINAL;
                      this.done = true;
                      parent.tell(resultDTO, getSelf());
                      getSender()
                              .tell(PoisonPill.getInstance(), getSelf());
                    })
            .match(IncentivizeDTO.class, this::incentivize)
            .build();
  }

}



