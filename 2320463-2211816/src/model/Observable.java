package model;

import view.Observer;
import java.util.*;

public interface Observable {
    void addObserver(Observer o);
    void removeObserver(Observer o);
}
