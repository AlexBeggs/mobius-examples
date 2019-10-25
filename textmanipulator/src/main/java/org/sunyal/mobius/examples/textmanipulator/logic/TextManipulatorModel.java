package org.sunyal.mobius.examples.textmanipulator.logic;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class TextManipulatorModel {

    public abstract String textValue();

    public abstract Operation operation();

    public abstract String displayValue();

    abstract Builder toBuilder();

    public TextManipulatorModel withTextAndDisplayValue(String textValue, String displayValue) {
        return toBuilder().textValue(textValue).displayValue(displayValue).build();
    }

    public TextManipulatorModel withOperation(Operation operation, String displayValue) {
        return toBuilder().operation(operation).displayValue(displayValue).build();
    }

    public static Builder builder() {
        return new AutoValue_TextManipulatorModel.Builder();
    }

    @AutoValue.Builder
    public interface Builder {
        Builder textValue(String textValue);

        Builder operation(Operation operation);

        Builder displayValue(String displayValue);

        TextManipulatorModel build();
    }

}
