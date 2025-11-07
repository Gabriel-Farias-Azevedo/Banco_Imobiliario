package tests.model;

import org.junit.*;
import static org.junit.Assert.*;
import model.*;

public class PrisaoTest {

    private Jogador jogador;
    private Prisao prisao;

    @Before
    public void setUp() {
        jogador = new Jogador("Lucia", "Rosa");
        prisao = new Prisao();
    }

    @Test(timeout = 2000)
    public void testEntraNaPrisao_EstaPreso() {
        prisao.prender(jogador);
        assertTrue("Jogador deveria estar preso", jogador.getPreso());
    }

    @Test(timeout = 2000)
    public void testSaiDaPrisao_AposDuplo() {
        prisao.prender(jogador);
        prisao.soltarDado(jogador, 3, 3);
        assertFalse("Jogador deveria sair com números iguais", jogador.getPreso());
    }
}
