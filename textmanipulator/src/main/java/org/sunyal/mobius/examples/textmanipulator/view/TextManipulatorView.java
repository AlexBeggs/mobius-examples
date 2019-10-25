package org.sunyal.mobius.examples.textmanipulator.view;

import org.sunyal.mobius.examples.textmanipulator.logic.TextManipulatorModel;

public interface TextManipulatorView {

    void bind(TextManipulatorModel model);

    void setViewListener(TextManipulatorViewListener viewListener);
}
