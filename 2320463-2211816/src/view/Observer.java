package view;

import model.Observable;

public interface Observer {
    void atualizar(Observable observado, String evento);

	void atualizar(Object fonte, String evento);
}
