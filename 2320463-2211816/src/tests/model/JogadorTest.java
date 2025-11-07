package tests.model;

import org.junit.*;
import static org.junit.Assert.*;
import model.*;

public class JogadorTest {

    private Jogador jogador;

    @Before
    public void setUp() {
        jogador = new Jogador("Carlos", "Azul");
        jogador.setPosicao(0);
    }

    @Test(timeout = 2000)
    public void testDeslocarPiao_SomaCorreta() {
        jogador.deslocar(7);
        assertEquals("O pião deve ir para a casa 7", 7, jogador.getPosicao());
    }

    @Test(timeout = 2000)
    public void testDeslocarPiao_PassaDoFimVoltaInicio() {
        jogador.setPosicao(38);
        jogador.deslocar(4);
        assertEquals("O pião deve circular para o início", 2, jogador.getPosicao());
    }

    @Test(timeout = 2000)
    public void testReceberDinheiro_AumentaSaldo() {
        jogador.creditar(200);
        assertEquals("Saldo incorreto", 4200, jogador.getSaldo());
    }

    @Test(timeout = 2000)
    public void testPagarDinheiro_DiminuiSaldo() {
        jogador.debitar(300);
        assertEquals("Saldo incorreto após pagamento", 3700, jogador.getSaldo());
    }
}
