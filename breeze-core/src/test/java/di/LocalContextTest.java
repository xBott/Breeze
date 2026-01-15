package di;

import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.PostConstructHook;
import me.bottdev.breezeapi.di.BeanScope;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.platforms.SLF4JLogPlatform;
import me.bottdev.breezeapi.log.trace.LogTrace;
import me.bottdev.breezeapi.log.trace.TraceScope;
import me.bottdev.breezecore.di.LocalContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LocalContextTest {

    public interface UserService extends PostConstructHook {

        @Override
        default void onPostConstruct() {
            System.out.println("User service is constructed!");
        }

        void debug();

    }

    public static class UserServiceImpl implements UserService {

        @Override
        public void debug() {
            System.out.println("this is a user service implementation!");
        }

    }

    public interface OrderService {

        void makeOrder();

    }

    public static class OrderServiceImpl implements OrderService {

        private final UserService userService;

        @Inject
        public OrderServiceImpl(UserService userService) {
            this.userService = userService;
        }

        @Override
        public void makeOrder() {
            userService.debug();
            System.out.println("this is a order service implementation!");
        }

    }

    static BreezeLogger logger;
    LogTrace trace;
    BreezeContext context;

    @BeforeAll
    static void setupGlobal() {
        logger = SLF4JLogPlatform.getFactory().simple("LocalContextTest");
    }

    @BeforeEach
    void setup() {
        context = new LocalContext();
        trace = new LogTrace();
        trace.addListener(logger);
    }

    @Test
    void shouldBindBean() {

        try (TraceScope scope = trace.scope("Bind bean test", 0)) {

            int size;

            size = context.getBindings().size();
            assertEquals(0, size);

            context.bind(UserService.class)
                    .scope(BeanScope.SINGLETON)
                    .to(UserServiceImpl.class);

            size = context.getBindings().size();
            assertEquals(1, size);

            UserService userService = context.get(UserService.class);

            assertNotNull(userService);
            assertEquals(UserServiceImpl.class, userService.getClass());

            userService.debug();

        }

    }

    @Test
    void shouldWireBeanToAnotherBean() {

        try (TraceScope scope = trace.scope("Wire bean to another bean test", 0)) {
            int size;

            size = context.getBindings().size();
            assertEquals(0, size);

            context.bind(UserService.class)
                    .scope(BeanScope.SINGLETON)
                    .to(UserServiceImpl.class);

            context.bind(OrderService.class)
                    .scope(BeanScope.SINGLETON)
                    .to(OrderServiceImpl.class);

            size = context.getBindings().size();
            assertEquals(2, size);

            OrderService orderService = context.get(OrderService.class);

            assertNotNull(orderService);
            assertEquals(OrderServiceImpl.class, orderService.getClass());

            orderService.makeOrder();
        }

    }

}
