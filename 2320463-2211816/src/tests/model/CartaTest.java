package tests.model;

import org.junit.*;
import static org.junit.Assert.*;
import model.*;


public class CartaTest {
    private BaralhoCartas baralho;
    private Jogador jogador;
    private Jogo jogo;

    @Before
    public void setUp() {
        baralho = new BaralhoCartas();
        jogador = new Jogador("Teste", "azul");
        jogo = new Jogo(4);
    }
    
    @Test
    public void testPegarCartaSorte() {
        Carta carta = baralho.pegarCartaSorte();
        assertNotNull(carta);
        assertEquals(TipoCarta.SORTE, carta.getTipo());
    }
    
    @Test
    public void testPegarCartaReves() {
        Carta carta = baralho.pegarCartaReves();
        assertNotNull(carta);
        assertEquals(TipoCarta.REVES, carta.getTipo());
    }    
    
    @Test
    public void testCartaReceberDinheiro() {
        Carta carta = new Carta("Teste receber", TipoCarta.SORTE, AcaoCarta.RECEBER_DINHEIRO, 100);
        int saldoAnterior = jogador.getSaldo();
        carta.executarAcao(jogador, jogo, null);
        assertEquals(saldoAnterior + 100, jogador.getSaldo());
    }
    
    @Test
    public void testCartaPagarDinheiro() {
        Carta carta = new Carta("Teste pagar", TipoCarta.REVES, AcaoCarta.PAGAR_DINHEIRO, 50);
        int saldoAnterior = jogador.getSaldo();
        carta.executarAcao(jogador, jogo, null);
        assertEquals(saldoAnterior - 50, jogador.getSaldo());
    }

    // Outros itens podem ser adicionados como verificar se o jogador recebe carta de sair da prisão, etc.

}