package tests.model;

import org.junit.*;
import static org.junit.Assert.*;
import model.Dado;

public class DadoTest {

    private Dado dado;

    @Before
    public void setUp() {
        dado = new Dado();
    }


    @Test(timeout = 2000)
    public void testLancar_DoisDadosValoresValidos() {
        dado.rolar(); // gera valores aleatórios

        int valorA = dado.getValorA();
        int valorB = dado.getValorB();

        assertTrue("ValorA fora do intervalo [1,6]", valorA >= 1 && valorA <= 6);
        assertTrue("ValorB fora do intervalo [1,6]", valorB >= 1 && valorB <= 6);
    }
}
