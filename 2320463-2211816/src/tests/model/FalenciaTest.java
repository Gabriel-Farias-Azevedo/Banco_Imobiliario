package tests.model;

import org.junit.*;
import static org.junit.Assert.*;
import model.*;

public class FalenciaTest {

    private Jogador jogador;

    @Before
    public void setUp() {
        jogador = new Jogador("Miguel", "Branco");
    }

    @Test(timeout = 2000)
    public void testFalencia_SemDinheiroSaiDoJogo() {
        jogador.debitar(4000);
        assertTrue("Jogador deveria estar falido", jogador.isFalido());
    }
}
