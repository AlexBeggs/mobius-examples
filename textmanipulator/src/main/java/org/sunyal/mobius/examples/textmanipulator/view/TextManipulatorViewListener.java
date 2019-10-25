package org.sunyal.mobius.examples.textmanipulator.view;

import org.sunyal.mobius.examples.textmanipulator.logic.Operation;

public interface TextManipulatorViewListener {

    void textChanged(String text);

    void operationChanged(Operation operation);

}