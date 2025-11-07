package tests.model;

import org.junit.*;
import static org.junit.Assert.*;
import model.*;

public class PropriedadeTest {

    private Jogador jogador;
    private Propriedade propriedade;

    @Before
    public void setUp() {
        jogador = new Jogador("Ana", "Vermelho");
        propriedade = new Propriedade("Avenida Brasil", 200);
    }

    @Test(timeout = 2000)
    public void testComprarPropriedade_SemDono() {
        boolean comprou = propriedade.comprar(jogador);
        assertTrue("Jogador deveria conseguir comprar propriedade", comprou);
        assertEquals("Dono incorreto", jogador, propriedade.getDono());
        assertEquals("Saldo incorreto após compra", 3800, jogador.getSaldo());
    }

    @Test(timeout = 2000)
    public void testConstruirCasa_AumentaNumeroDeCasas() {
        propriedade.comprar(jogador);
        propriedade.construirCasa();
        assertEquals("Número de casas incorreto", 1, propriedade.getCasas());
    }

    @Test(timeout = 2000)
    public void testPagarAluguel_DeOutroJogador() {
        // Arrange
        Jogador dono = new Jogador("Beatriz", "Amarelo");
        propriedade.comprar(dono);
        propriedade.construirCasa();

        int saldoJogadorAntes = jogador.getSaldo();
        int saldoDonoAntes = dono.getSaldo();

        // Act
        propriedade.cobrarAluguel(jogador);

        int saldoJogadorDepois = jogador.getSaldo();
        int saldoDonoDepois = dono.getSaldo();

        // Assert
        assertTrue("Jogador deve pagar algum valor de aluguel", saldoJogadorDepois < saldoJogadorAntes);
        assertTrue("Dono deve receber o valor do aluguel", saldoDonoDepois > saldoDonoAntes);
        assertEquals("Soma dos saldos deve permanecer constante (sem perda de dinheiro)",
                     saldoJogadorAntes + saldoDonoAntes, saldoJogadorDepois + saldoDonoDepois);
    }
}
