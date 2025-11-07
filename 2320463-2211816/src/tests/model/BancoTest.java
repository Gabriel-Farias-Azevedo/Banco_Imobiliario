package tests.model;

import org.junit.*;
import static org.junit.Assert.*;

import model.*;

public class BancoTest {

    private Banco banco;
    private Jogador jogador;

    @Before
    public void setUp() {
        banco = new Banco();
        jogador = new Jogador("Davi", "Verde");
    }

    @Test(timeout = 2000)
    public void testPagarJogador_TransferenciaCorreta() {
        banco.pagar(jogador, 200); // paga do banco para o jogador
        assertEquals("Jogador deveria receber 200", 4200, jogador.getSaldo());
        assertEquals("Banco deveria debitar 200", 199800, banco.getSaldo());
    }

    @Test(timeout = 2000)
    public void testReceberDoJogador_AjusteCorreto() {
        banco.receber(jogador, 1000); // recebe do jogador para o banco
        assertEquals("Jogador deveria perder 1000", 3000, jogador.getSaldo());
        assertEquals("Banco deveria creditar 1000", 201000, banco.getSaldo());
    }
}