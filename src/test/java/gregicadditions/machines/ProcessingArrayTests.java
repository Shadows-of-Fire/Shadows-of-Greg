package gregicadditions.machines;

import net.minecraft.init.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for validating correctness of logic for the Processing Array.
 */
public class ProcessingArrayTests {

    /**
     * Required. Without this all item-related operations will fail because registries haven't been initialized.
     */
    @BeforeAll
    public static void bootStrap() {
        Bootstrap.register();
    }

    @Test
    @Disabled
    public void test_goes_here() {
        fail();
    }

}