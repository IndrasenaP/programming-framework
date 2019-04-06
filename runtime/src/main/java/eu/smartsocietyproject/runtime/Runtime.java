package eu.smartsocietyproject.runtime;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.reflect.ClassPath;
import com.typesafe.config.Config;
import eu.smartsocietyproject.Task;
import eu.smartsocietyproject.pf.*;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Runtime extends AbstractActor {
    private Logger logger=Logger.getLogger(this.getClass().getName());
    private final ApplicationContext context;
    private final Application application;
    private final ConcurrentHashMap<UUID, TaskRunner> runnerDescriptors = new ConcurrentHashMap<>();
    private ActorRef parent;

    public static Props props(ApplicationContext context, Application application){
        return Props.create(Runtime.class, () -> new Runtime(context, application));
    }

    @Override
    public void preStart() throws Exception {
        this.parent = getContext().getParent();
    }

    public Runtime(ApplicationContext context, Application application) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(application);
        this.context = context;
        this.application = application;
    }

    public Runtime init(Config config) {
        application.init(context, config);
        return this;
    }

    public boolean startTask(TaskDefinition definition)  {
        TaskRequest request = null;
        try {
            request = application.createTaskRequest(definition);
        } catch (ApplicationException e) {
            logger.log(Level.SEVERE, "Error creating the task request", e);
            return false;
        }

        TaskRunner runner = application.createTaskRunner(request);
        runner.self().tell(Task.START, getSelf());
        runnerDescriptors.put(definition.getId(), runner);
        return true;
    }

    Optional<JsonNode> monitor(UUID taskId) {
        return
            Optional
                .ofNullable(runnerDescriptors.get(taskId))
            .map(TaskRunner::getStateDescription);
    }

    void cancel(UUID taskId) {
        TaskRunner descriptor = runnerDescriptors.get(taskId);
        if ( descriptor != null )
            descriptor.self().tell(Task.STOP, getSelf());
    }

    public ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TaskDefinition.class,
                        this::startTask)
                .build();
    }

    public static Runtime fromApplication(Config config, SmartSocietyComponents components) throws IOException, InstantiationException {
        return fromApplication(config, components, getApplicationClass());
    }

    public static Runtime fromApplication(
        Config config,
        SmartSocietyComponents components,
        Class<? extends Application> applicationClass) throws IOException, InstantiationException {
        Application application = instantiateApplication(applicationClass);
        CollectiveKindRegistry registry = createCollectiveKindRegistry(application);
        SmartSocietyApplicationContext context =
            new SmartSocietyApplicationContext(
                registry,
                components.getPeerManagerFactory(),
                components.getSmartComServiceFactory(),
                components.getPaymentService());
        return new Runtime(context, application).init(config);
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Application> getApplicationClass() throws IOException {
        ClassLoader cl = new Integer(2).getClass().getClassLoader();
        Set<ClassPath.ClassInfo> classesInPackage = ClassPath.from(cl).getAllClasses();
        List<? extends Class<?>> applicationClasses = classesInPackage
            .stream()
            .map(c -> c.load())
            .filter(c ->
                        !Modifier.isAbstract(c.getModifiers())
                            && Modifier.isInterface(c.getModifiers())
                            && Application.class.isAssignableFrom(c))
            .collect(Collectors.toList());

        Preconditions.checkArgument(
            !applicationClasses.isEmpty(),
            "No concrete implementation of eu.smartsocietyproject.pf.Application found");
        Preconditions.checkArgument(applicationClasses.size() > 1, "" +
            "Too many concrete implementations of eu.smartsocietyproject.pf.Application found");

        return (Class<? extends Application>) applicationClasses.get(0);
    }

    private static Application instantiateApplication(Class<? extends Application> applicationClass)
        throws InstantiationException {
        try {
            return applicationClass.newInstance();
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(
                String.format(
                    "Unable to access empty constructor for class %s",
                    applicationClass.getCanonicalName()));
        }
    }

    private static CollectiveKindRegistry createCollectiveKindRegistry(Application application) {
        return
            new CollectiveKindRegistry(
                application
                    .listCollectiveKinds()
                    .stream()
                    .collect(Collectors.toMap(k->k.getId(), Function.identity())));
    }

}
